package com.darkweb.genesissearchengine.pluginManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class exitManager extends Service {

    /*Private Variables*/

    private pluginController plugin_controller;

    /*Initializations*/

    public exitManager(){
        plugin_controller = pluginController.getInstance();
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
        plugin_controller.stopAllServices();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        plugin_controller.stopAllServices();
   }
}