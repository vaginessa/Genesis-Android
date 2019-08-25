package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

public class settingController extends AppCompatActivity
{

    /*Private Variables*/

    private setting_view_controller viewController;
    private Spinner search;
    private Spinner javascript;
    private Spinner history;
    private homeModel home_model;
    private homeController home_controller;
    private settingModel setting_model;
    private settingsEhandler settings_ehandler;

    /*Initializations*/

    public settingController(){
        home_controller = homeModel.getInstance().getHomeInstance();
        setting_model = settingModel.getInstance();
        settings_ehandler = settingsEhandler.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        setting_model.init_status();
        viewsInitializations();
        listenersInitializations();
        initializeModel();

        setting_model.java_status = java_status;
        setting_model.history_status = history_status;
        setting_model.search_status = status.search_status;
    }

    public void viewsInitializations()
    {
        search = findViewById(R.id.search_manager);
        javascript = findViewById(R.id.javascript_manager);
        history = findViewById(R.id.history_manager);

        viewController = new setting_view_controller(search,javascript,history);
    }

    public void listenersInitializations()
    {
        initializeItemSelectedListener(search);
        initializeItemSelectedListener(javascript);
        initializeItemSelectedListener(history);
    }

    public void initializeModel()
    {
        setting_model.setSettingInstance(this);
    }

    /*Event Handlers*/

    public void initializeItemSelectedListener(Spinner view)
    {
        settings_ehandler.onItemListnerInitialization(view);
    }

    public void onBackPressed(View view)
    {
        settings_ehandler.onBackPressed();
    }

    /*View Handlers*/

    @Override
    public void onBackPressed(){
        closeView();
    }

    public void closeView()
    {
        viewController.closeView();
    }

    /*Home Handlers*/

    public void initSearchEngine(){
        home_controller.initSearchEngine();
    }

    public void reInitGeckoView(){
        home_controller.onReInitGeckoView();
    }

    /*Plugin Manager*/

}