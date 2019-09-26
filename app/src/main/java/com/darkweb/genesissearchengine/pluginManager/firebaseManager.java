package com.darkweb.genesissearchengine.pluginManager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

class firebaseManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;
    private FirebaseAnalytics mFirebaseAnalytics;

    /*Initializations*/

    firebaseManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;

        initialize();
    }

    public void initialize()
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(app_context.getApplicationContext());
    }

    /*Helper Methods*/

    void logEvent(String value, String id)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, value);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
