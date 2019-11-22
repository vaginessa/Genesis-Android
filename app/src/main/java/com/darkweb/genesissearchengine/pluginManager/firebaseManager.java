package com.darkweb.genesissearchengine.pluginManager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.google.firebase.analytics.FirebaseAnalytics;

class firebaseManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private FirebaseAnalytics mFirebaseAnalytics;

    /*Initializations*/

    firebaseManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;

        initialize();
    }

    public void initialize()
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mAppContext.getApplicationContext());
    }

    /*Helper Methods*/

    void logEvent(String value)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, value);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
