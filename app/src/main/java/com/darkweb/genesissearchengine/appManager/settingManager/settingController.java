package com.darkweb.genesissearchengine.appManager.settingManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

public class settingController extends AppCompatActivity
{
    /*Private Observer Classes*/

    private homeController home_controller;
    private settingViewController viewController;
    private settingModel setting_model;

    /*Private Variables*/

    private Spinner search;
    private Spinner javascript;
    private Spinner history;

    /*Initializations*/

    public settingController(){
        home_controller = activityContextManager.getInstance().getHomeController();
        setting_model = new settingModel(new settingModelCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        viewsInitializations();
        modelInitialization();
        listenersInitializations();
    }

    public void modelInitialization(){
        setting_model.setJavaStatus(java_status);
        setting_model.setHistoryStatus(history_status);
        setting_model.setSearchStatus(status.search_status);
    }

    public void viewsInitializations()
    {
        search = findViewById(R.id.search_manager);
        javascript = findViewById(R.id.javascript_manager);
        history = findViewById(R.id.history_manager);

        String currentSearchEngine = dataController.getInstance().getString(keys.search_engine, strings.darkweb);
        viewController = new settingViewController(search,javascript,history,this,currentSearchEngine,new settingModelCallback());
    }

    public void listenersInitializations()
    {
        initializeItemSelectedListener(search);
        initializeItemSelectedListener(javascript);
        initializeItemSelectedListener(history);
    }

    /*Event Handlers*/

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
        finish();
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        setting_model.onCloseView();
    }

    public void initializeItemSelectedListener(Spinner view){
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(parentView.getId()== R.id.search_manager)
                {
                    setting_model.setSearchStatus(getEngineURL(position));
                }
                else if(parentView.getId()== R.id.javascript_manager)
                {
                    setting_model.setJavaStatus(position==0);
                }
                else if(parentView.getId()== R.id.history_manager)
                {
                    setting_model.setHistoryStatus(position==0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void onNavigationBackPressed(View view){
        setting_model.onCloseView();
    }

    /*Event Observer*/

    public class settingViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {

        }
    }

    public class settingModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            if(e_type == enums.eventType.update_searcn){
                status.search_status = (String)data;
                home_controller.onHomeButton(null);
                dataController.getInstance().setString(keys.search_engine, setting_model.getSearchStatus());
            }
            else if(e_type == enums.eventType.update_javascript){
                status.java_status = (boolean)data;
                home_controller.reloadJavaScript();
                dataController.getInstance().setBool(keys.java_script, status.java_status);
            }
            else if(e_type == enums.eventType.update_history){
                history_status = (boolean)data;
                dataController.getInstance().setBool(keys.history_clear, history_status);
            }
            else if(e_type == enums.eventType.close_view){
                finish();
            }
        }
    }

    /*Helper Methods*/

    public String getEngineURL(int index){

        if (index == 0)
        {
            return constants.backendGenesis;
        }
        else if (index == 1)
        {
            return constants.backendGoogle;
        }
        else
        {
            return constants.backendDuckDuckGo;
        }
    }

}