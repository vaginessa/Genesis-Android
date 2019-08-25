package com.darkweb.genesissearchengine.pluginManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class exitManager extends Service {

    /*Private Variables*/

    private AppCompatActivity app_context;
    private callbackManager.callbackListener callback;

    /*Initializations*/

    exitManager(AppCompatActivity app_context, callbackManager.callbackListener callback){
        this.app_context = app_context;
        this.callback = callback;
    }

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
        callback.callbackSuccess(null,null);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        callback.callbackSuccess(null,null);
   }
}