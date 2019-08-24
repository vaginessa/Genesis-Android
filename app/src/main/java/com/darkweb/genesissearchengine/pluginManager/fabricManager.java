package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class fabricManager
{
    /*Private Variables*/

    private static final fabricManager ourInstance = new fabricManager();
    private pluginController plugin_controller;
    private AppCompatActivity app_context;

    /*Initializations*/

    public static fabricManager getInstance()
    {
        return ourInstance;
    }

    private fabricManager(){
        plugin_controller = pluginController.getInstance();
        app_context = plugin_controller.getAppContext();
    }

    public void init(){
        // Fabric.with(app_context, new Crashlytics());
        // plugin_controller.initializeAnalyticsManager();
    }
}
