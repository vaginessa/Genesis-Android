package com.darkweb.genesissearchengine.pluginManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class exitManager extends Service {

    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    /*Initializations*/

    /*exitManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
    }*/

    /*Helper Methods*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //event.invokeObserver(null,null);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        //event.invokeObserver(null,null);
   }
}