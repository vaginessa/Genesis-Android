package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.example.myapplication.R;

public class settingsEhandler
{
    /*Private Variables*/

    private settingModel setting_model;
    private settingController setting_controller;

    /*Initializations*/

    private static final settingsEhandler ourInstance = new settingsEhandler();
    public static settingsEhandler getInstance()
    {
        return ourInstance;
    }

    private settingsEhandler()
    {
        this.setting_controller = settingModel.getInstance().getSettingInstance();
        this.setting_model = settingModel.getInstance();
    }

    /*Listeners*/

    private void onJavaScriptListener(int position)
    {
        if(position==1 && setting_model.java_status)
        {
            setting_model.java_status = false;
        }
        else if(position==0 && !setting_model.java_status)
        {
            setting_model.java_status = true;
        }
    }

    private void onSearchListner(AdapterView<?> parentView,int position)
    {
        if(!setting_model.search_status.equals(parentView.getItemAtPosition(position).toString()))
        {
            setting_model.search_status = parentView.getItemAtPosition(position).toString();
        }
    }

    private void onHistoryListener(int position)
    {
        if(position==1 && setting_model.history_status)
        {
            setting_model.history_status = false;
        }
        else if(position==0 && !setting_model.history_status)
        {
            setting_model.history_status = true;
        }
    }

    void onBackPressed()
    {
        setting_controller.closeView();
    }

    /*Listener Initializations*/

    void onItemListnerInitialization(Spinner view)
    {
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(parentView.getId()== R.id.search_manager)
                {
                    onSearchListner(parentView,position);
                }
                else if(parentView.getId()== R.id.javascript_manager)
                {
                    onJavaScriptListener(position);
                }
                else if(parentView.getId()== R.id.history_manager)
                {
                    onHistoryListener(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

}
