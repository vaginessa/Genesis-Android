package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

class firebaseManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    /*Initializations*/

    firebaseManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
    }

}
