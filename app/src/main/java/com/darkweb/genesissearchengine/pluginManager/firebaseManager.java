package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

public class firebaseManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private callbackManager.callbackListener callback;

    /*Initializations*/

    firebaseManager(AppCompatActivity app_context,callbackManager.callbackListener callback){
        this.app_context = app_context;
        this.callback = callback;
    }

}
