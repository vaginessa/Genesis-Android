package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

class fabricManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private callbackManager.callbackListener callback;

    /*Initializations*/

    fabricManager(AppCompatActivity app_context,callbackManager.callbackListener callback){
        this.app_context = app_context;
        this.callback = callback;
        initialize();
    }

    private void initialize(){
        // Fabric.with(app_context, new Crashlytics());
        // plugin_controller.initializeAnalyticsManager();
        callback.callbackSuccess(null,null);
    }
}
