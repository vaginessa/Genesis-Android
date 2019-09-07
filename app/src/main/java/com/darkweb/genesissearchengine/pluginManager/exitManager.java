package com.darkweb.genesissearchengine.pluginManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.keys;

public class exitManager extends Service {

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public exitManager(){
    }


    public exitManager(AppCompatActivity app_context, eventObserver.eventListener event){
        Log.i("jassad","sd");
        this.app_context = app_context;
        this.event = event;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pluginController.getInstance().proxyManager(false);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        pluginController.getInstance().proxyManager(false);
    }
}
