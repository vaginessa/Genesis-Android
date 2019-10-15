package com.darkweb.genesissearchengine.pluginManager;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.service.TorService;
import org.torproject.android.service.util.Prefs;

import static org.torproject.android.service.TorServiceConstants.ACTION_START;

class orbotManager
{

    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    /*Initialization*/
    private static orbotManager ourInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return ourInstance;
    }
    private orbotManager()
    {
    }

    public void initialize(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
        initializeProxy();
        initialize();
    }

    TorService service = new TorService();

    public void initialize(){
        Prefs.setContext(app_context.getApplicationContext());
        Intent intent = new Intent(app_context.getApplicationContext(), service.getClass());
        intent.setAction(ACTION_START);
        app_context.startService(intent);
    }

    public void reInit(){
        //service.stopTor();
        Prefs.setContext(app_context.getApplicationContext());
        Intent intent = new Intent(app_context.getApplicationContext(), service.getClass());
        intent.setAction(ACTION_START);
        app_context.startService(intent);
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    void setProxy(boolean tor_status,boolean is_genesis){
        if(is_genesis){
            PrefsHelper.setPref(keys.proxy_type, 0);
            PrefsHelper.setPref(keys.proxy_socks,null);
            PrefsHelper.setPref(keys.proxy_socks_port, null);
            PrefsHelper.setPref(keys.proxy_socks_version,null);
            PrefsHelper.setPref(keys.proxy_socks_remote_dns,null);
        }
        else if(tor_status){
            PrefsHelper.setPref(keys.proxy_type, 1);
            PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks);
            PrefsHelper.setPref(keys.proxy_socks_port, 9050);
            PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version);
            PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns);
        }
    }

    private void initializeProxy()
    {
        PrefsHelper.setPref(keys.proxy_type, 0);
        PrefsHelper.setPref(keys.proxy_socks,null);
        PrefsHelper.setPref(keys.proxy_socks_port, null);
        PrefsHelper.setPref(keys.proxy_socks_version,null);
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,null);

        PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);

        PrefsHelper.setPref("browser.cache.disk.enable",true);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",10000);
    }

    boolean logs_started = false;
    String getLogs()
    {
        String logs = status.tor_logs_status;

        if(!logs.contains("Bootstrapped") && !logs_started){
            logs = "Starting Bootstrap";
            logs_started = true;
        }
        else {
            logs = logs.replace("(","").replace(":","").replace("NOTICE","").replace(")","");
        }


        if(logs!=null && !logs.equals(strings.emptyStr))
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
        return status.isTorInitialized;
    }

}
