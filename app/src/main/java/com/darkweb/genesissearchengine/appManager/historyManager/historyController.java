package com.darkweb.genesissearchengine.appManager.historyManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

public class historyController extends AppCompatActivity
{
    /*Private Variables*/

    private historyModel list_model;
    private homeController home_controller;
    private activityContextManager contextManager;

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;
    private Button clearButton;

    private historyViewController viewController;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_view);
        initializeListModel();
        initializeViews();
        initializeList();
        onEditorInvoked();
    }

    public void initializeListModel(){
        list_model = new historyModel();
        list_model.setList(dataController.getInstance().getHistory());
        contextManager = activityContextManager.getInstance();
        home_controller = homeModel.getInstance().getHomeInstance();
        contextManager.setHistoryController(this);
    }
    public void initializeViews(){
        emptyListNotifier = findViewById(R.id.empty_list);
        searchBar = findViewById(R.id.search);
        listView = findViewById(R.id.listview);
        clearButton = findViewById(R.id.clearButton);
        viewController = new historyViewController(emptyListNotifier,searchBar,listView,clearButton);
    }
    public void initializeList(){
        historyAdapter adapter = new historyAdapter(list_model.getList(),new adapterCallback());
        adapter.invokeFilter(false);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        viewController.updateIfListEmpty(list_model.getList().size(),0);
    }

    /*View Handlers*/

    public void onEditorInvoked(){

        searchBar.setOnEditorActionListener((v, actionId, event) ->{
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                ((historyAdapter) listView.getAdapter()).setFilter(searchBar.getText().toString());
                ((historyAdapter) listView.getAdapter()).invokeFilter(true);
            }
        });
    }

    public void onBackPressed(View view){
        this.finish();
    }
    public void onclearDataTrigger(View view){
        pluginController.getInstance().MessageManagerHandler(this,"",enums.popup_type.clear_history);
    }
    public void onclearData(){
        list_model.clearList();
        ((historyAdapter)listView.getAdapter()).invokeFilter(true );
        viewController.clearList();
    }



    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(Object data, enums.history_eventType e_type)
        {
            if(e_type.equals(enums.history_eventType.url_triggered)){
                String url_temp = helperMethod.completeURL(data.toString());
                home_controller.addNavigation(data.toString(), enums.navigationType.onion);
                home_controller.onloadURL(url_temp,false,false,false);
                finish();
            }
            else if(e_type.equals(enums.history_eventType.url_clear)){
                list_model.onManualClear((int)data);
            }
            else if(e_type.equals(enums.history_eventType.is_empty)){
                viewController.removeFromList((int)data);
                viewController.updateIfListEmpty(list_model.getList().size(),300);
            }
            else if(e_type.equals(enums.history_eventType.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data,"history");
            }
        }
    }

}
