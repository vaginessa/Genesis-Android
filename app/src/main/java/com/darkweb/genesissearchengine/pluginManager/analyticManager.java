package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import com.darkweb.genesissearchengine.constants.constants;

import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
class analyticManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;
    private String uniqueID = null;

    /*Initializations*/

    analyticManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
        initialize();
    }

    private void initialize(){
        final String PREF_UNIQUE_ID = constants.unique_key_id;

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
        Crashlytics.setUserEmail(constants.user_email);
        Crashlytics.setUserName(uniqueID);
    }

}
