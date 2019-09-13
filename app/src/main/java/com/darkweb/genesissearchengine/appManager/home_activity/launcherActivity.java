package com.darkweb.genesissearchengine.appManager.home_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

public class launcherActivity extends AppCompatActivity
{
    boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invalid_setup_view);

        helperMethod.openActivity(homeController.class, constants.list_history, this);
    }

    @Override
    public void onResume()
    {
        if(isStarted){
            onReset();

            new Handler().postDelayed(() -> helperMethod.openActivity(homeController.class, constants.list_history, launcherActivity.this), 0);
        }
        else {
            isStarted = true;
        }
        super.onResume();
    }

    public void onReset(){
        status.isAppStarted = false;
    }


}
