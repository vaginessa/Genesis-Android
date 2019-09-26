package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

public class bookmarkController extends AppCompatActivity
{
    /*Private Variables*/

    private bookmarkModel list_model;
    private homeController home_controller;
    private activityContextManager contextManager;

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;
    private Button clearButton;

    private bookmarkViewController viewController;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_view);
        initializeListModel();
        initializeViews();
        initializeList();
        onEditorInvoked();
    }

    public void initializeListModel(){
        list_model = new bookmarkModel();
        list_model.setList(dataController.getInstance().getBookmark());
        contextManager = activityContextManager.getInstance();
        home_controller = activityContextManager.getInstance().getHomeController();
        contextManager.setBookmarkController(this);
        pluginController.getInstance().logEvent(strings.bookmark_opened,"");
    }
    public void initializeViews(){
        emptyListNotifier = findViewById(R.id.empty_list);
        searchBar = findViewById(R.id.search);
        listView = findViewById(R.id.listview);
        clearButton = findViewById(R.id.clearButton);
        viewController = new bookmarkViewController(emptyListNotifier,searchBar,listView,clearButton,this);
        clearButton.setText("CLEAR BOOKMARK");
    }
    public void initializeList(){
        bookmarkAdapter adapter = new bookmarkAdapter(list_model.getList(),new adapterCallback());
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
                ((bookmarkAdapter) listView.getAdapter()).setFilter(searchBar.getText().toString());
                ((bookmarkAdapter) listView.getAdapter()).invokeFilter(true);
            }
        });
    }

    public void onBackPressed(View view){
        this.finish();
    }
    public void onclearDataTrigger(View view){
        pluginController.getInstance().MessageManagerHandler(this,"",enums.popup_type.clear_bookmark);
    }
    public void onclearData(){
        list_model.clearList();
        ((bookmarkAdapter)listView.getAdapter()).invokeFilter(true );
        viewController.clearList();
        databaseController.getInstance().execSQL("delete from bookmark where 1",null);
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
        public void invokeObserver(Object data, enums.bookmark_eventType e_type)
        {
            if(e_type.equals(enums.bookmark_eventType.url_triggered)){
                String url_temp = helperMethod.completeURL(data.toString());
                pluginController.getInstance().logEvent(strings.bookmark_triggered,"");
                home_controller.loadURL(url_temp);
                finish();
            }
            else if(e_type.equals(enums.bookmark_eventType.url_clear)){
                list_model.onManualClear((int)data);
            }
            else if(e_type.equals(enums.bookmark_eventType.is_empty)){
                viewController.removeFromList((int)data);
                viewController.updateIfListEmpty(list_model.getList().size(),300);
            }
            else if(e_type.equals(enums.bookmark_eventType.remove_from_database)){
                databaseController.getInstance().deleteFromList((int)data,"bookmark");
            }
        }
    }

}
