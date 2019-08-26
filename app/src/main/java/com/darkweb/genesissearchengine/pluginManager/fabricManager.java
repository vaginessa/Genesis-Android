package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

class fabricManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    /*Initializations*/

    fabricManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
        initialize();
    }

    private void initialize(){
        // Fabric.with(app_context, new Crashlytics());
        // plugin_controller.initializeAnalyticsManager();
        event.invokeObserver(null,null);
    }
}
