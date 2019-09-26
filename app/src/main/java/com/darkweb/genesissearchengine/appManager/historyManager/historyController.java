package com.darkweb.genesissearchengine.appManager.historyManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.util.ArrayList;

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
    private ImageButton moreButton;

    private historyViewController history_view_controller;

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
        contextManager = activityContextManager.getInstance();
        home_controller = activityContextManager.getInstance().getHomeController();
        contextManager.setHistoryController(this);
        activityContextManager.getInstance().setHistoryController(this);
        pluginController.getInstance().logEvent(strings.history_opened,"");
    }
    public void initializeViews(){
        emptyListNotifier = findViewById(R.id.empty_list);
        searchBar = findViewById(R.id.search);
        listView = findViewById(R.id.listview);
        clearButton = findViewById(R.id.clearButton);
        moreButton = findViewById(R.id.load_more);
        history_view_controller = new historyViewController(emptyListNotifier,searchBar,listView,clearButton,moreButton,this);
    }
    public void initializeList(){
        ArrayList<historyRowModel> model = dataController.getInstance().getHistory();
        list_model.setList(model);
        historyAdapter adapter = new historyAdapter(list_model.getList(),new adapterCallback());
        adapter.invokeFilter(false);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        history_view_controller.updateIfListEmpty(list_model.getList().size(),0);
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
        history_view_controller.clearList();
        databaseController.getInstance().execSQL("delete from history where 1",null);
    }

    public void onLoadMoreHostory(View view)
    {
        dataController.getInstance().loadMoreHistory();
    }

    public void updateHistory(){
        initializeList();
        history_view_controller.updateList();
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.isAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().setBool(keys.low_memory,true);
            finish();
        }
    }

    @Override
    public void onResume()
    {
        status.isAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        status.isAppPaused = true;
        super.onPause();
    }

    /*Event Observer*/

    public class adapterCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(Object data, enums.history_eventType e_type)
        {
            if(e_type.equals(enums.history_eventType.url_triggered)){
                String url_temp = helperMethod.completeURL(data.toString());
                pluginController.getInstance().logEvent(strings.history_triggered,"");
                home_controller.loadURL(url_temp);
                finish();
            }
            else if(e_type.equals(enums.history_eventType.url_clear)){
                list_model.onManualClear((int)data);
            }
            else if(e_type.equals(enums.history_eventType.url_clear_at)){
                dataController.getInstance().removeHistory(data.toString());
            }
            else if(e_type.equals(enums.history_eventType.is_empty)){
                history_view_controller.removeFromList((int)data);
                history_view_controller.updateIfListEmpty(list_model.getList().size(),300);
            }
            else if(e_type.equals(enums.history_eventType.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data,"history");
            }
        }
    }

}
