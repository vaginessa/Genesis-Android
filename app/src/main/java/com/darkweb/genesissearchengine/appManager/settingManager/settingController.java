package com.darkweb.genesissearchengine.appManager.settingManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

public class settingController extends AppCompatActivity
{
    /*Private Observer Classes*/

    private homeController home_controller;
    private settingViewController setting_view_controller;
    private settingModel setting_model;

    /*Private Variables*/

    private Spinner search;
    private Spinner javascript;
    private Spinner history;
    private Spinner font_adjustable;
    private SeekBar font_size;
    private TextView font_size_percentage;

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
        initializeFontSizeListener();
    }

    public void modelInitialization(){
        setting_model.setJavaStatus(java_status);
        setting_model.setHistoryStatus(history_status);
        setting_model.setSearchStatus(status.search_status);
        setting_model.setAdjustableStatus(status.fontAdjustable);
        setting_model.setFontSize(status.fontSize);
    }

    public void viewsInitializations()
    {
        search = findViewById(R.id.search_manager);
        javascript = findViewById(R.id.javascript_manager);
        history = findViewById(R.id.history_manager);
        font_size = findViewById(R.id.font_size);
        font_adjustable = findViewById(R.id.font_adjustable);
        font_size_percentage = findViewById(R.id.font_size_percentage);

        String currentSearchEngine = dataController.getInstance().getString(keys.search_engine, strings.darkweb);
        setting_view_controller = new settingViewController(search,javascript,history,font_size,font_adjustable,font_size_percentage,this,currentSearchEngine,new settingModelCallback(),this);
    }

    public void listenersInitializations()
    {
        initializeItemSelectedListener(search);
        initializeItemSelectedListener(javascript);
        initializeItemSelectedListener(history);
        initializeItemSelectedListener(font_adjustable);
        pluginController.getInstance().logEvent(strings.settings_opened,"");
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
                else if(parentView.getId()== R.id.font_adjustable)
                {
                    setting_model.setAdjustableStatus(position==0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void initializeFontSizeListener(){

        font_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float cur_progress = seekBar.getProgress();
                float progress = (cur_progress);
                setting_model.setFontSize(progress);
                setting_view_controller.updatePercentage(font_size.getProgress());
                if(cur_progress<1){
                    font_size.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
            else if(e_type == enums.eventType.update_font_adjustable || e_type == enums.eventType.update_font_size){
                home_controller.onUpdateFont();
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