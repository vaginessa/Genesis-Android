package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

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
        Fabric.with(app_context, new Crashlytics());
        event.invokeObserver(null,null);
    }
}
