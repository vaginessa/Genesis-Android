package com.darkweb.genesissearchengine.pluginManager;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.service.TorService;
import org.torproject.android.service.util.Prefs;

import static org.torproject.android.service.TorServiceConstants.ACTION_START;

class orbotManager
{

    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private boolean mLogsStarted = false;

    /*Initialization*/
    private static orbotManager sOurInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return sOurInstance;
    }

    public void initialize(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;
    }

    void initializeOrbot(){
        initializeProxy();
        initialize();
    }

    public void initialize(){
        Prefs.setContext(mAppContext.getApplicationContext());
        Intent intent = new Intent(mAppContext.getApplicationContext(), TorService.class);
        intent.setAction(ACTION_START);
        mAppContext.startService(intent);
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    void setProxy(boolean tor_status,boolean is_genesis){
        if(is_genesis){
            PrefsHelper.setPref(keys.PROXY_TYPE, 0);
            PrefsHelper.setPref(keys.PROXY_SOCKS,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,null);
        }
        else if(tor_status){
            PrefsHelper.setPref(keys.PROXY_TYPE, 1);
            PrefsHelper.setPref(keys.PROXY_SOCKS,constants.PROXY_SOCKS);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, constants.PROXY_SOCKS_PORT);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.PROXY_SOCKS_VERSION);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.PROXY_SOCKS_REMOTE_DNS);
        }
    }

    private void initializeProxy()
    {
        PrefsHelper.setPref(keys.PROXY_TYPE, 0);
        PrefsHelper.setPref(keys.PROXY_CACHE,constants.PROXY_CACHE);
        PrefsHelper.setPref(keys.PROXY_MEMORY,constants.PROXY_MEMORY);
        PrefsHelper.setPref(keys.PROXY_SOCKS_COOKIES,status.sCookieStatus);
        PrefsHelper.setPref(keys.PROXY_USER_AGENT_OVERRIDE, constants.PROXY_USER_AGENT_OVERRIDE);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_ENABLED,constants.PROXY_DO_NOT_TRACK_HEADER_ENABLED);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_VALUE,constants.PROXY_DO_NOT_TRACK_HEADER_VALUE);
        PrefsHelper.setPref(keys.PROXY_DISK_CAPACITY,constants.DISK_CAPACITY);
    }

    String getLogs()
    {
        String logs = status.sTorLogsStatus;

        if(!logs.contains("Bootstrapped") && !mLogsStarted){
            logs = "Starting Bootstrap";
            mLogsStarted = true;
        }
        else {
            logs = logs.replace("(","").replace(":","").replace("NOTICE","").replace(")","");
        }


        if(!logs.equals(strings.EMPTY_STR))
        {
            String Logs = logs;
            if(Logs.equals(""))
            {
                return "Installing | Setting Configurations";
            }
            Logs="Installing | " + Logs.replace("FAILED","Securing");
            return Logs;
        }
        return "Loading Please Wait";
    }

    boolean isOrbotRunning(){
        return status.sIsTorInitialized;
    }

}
