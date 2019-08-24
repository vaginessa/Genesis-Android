package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")

public class analyticManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private pluginController plugin_controller;
    private String uniqueID = null;

    /*Initializations*/

    public static analyticManager getInstance() {
        return ourInstance;
    }
    private static final analyticManager ourInstance = new analyticManager();

    private analyticManager(){
        plugin_controller = pluginController.getInstance();
        app_context = plugin_controller.getAppContext();
    }

    void initialize(){
        final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

        if (uniqueID == null)
        {
            SharedPreferences sharedPrefs = app_context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
    }

    /*Helper Methods*/

    void logUser(){
        Crashlytics.setUserIdentifier(uniqueID);
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName(uniqueID);
    }

}
