package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.service.TorService;
import org.torproject.android.service.orbot_local_constants;
import org.torproject.android.service.util.Prefs;

import static org.torproject.android.service.TorServiceConstants.ACTION_START;

class orbotManager
{

    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private boolean mLogsStarted = false;
    private Intent mServiceIntent = null;

    /*Initialization*/
    private static orbotManager sOurInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return sOurInstance;
    }

    public void initialize(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;
        initialize();
    }

    void startOrbot(Context context){
        Prefs.setContext(context);
        Prefs.putBridgesEnabled(status.sGateway);
        mServiceIntent = new Intent(mAppContext, TorService.class);
        mServiceIntent.setAction(ACTION_START);
        mAppContext.startService(mServiceIntent);
        initializeProxy();
    }

    public void initialize(){
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    void onClose(){
        if(mServiceIntent!=null){
            mAppContext.stopService(mServiceIntent);
            mServiceIntent = null;
        }
    }

    void setProxy(String url){
        if(url.contains("boogle.store")){
            PrefsHelper.setPref(keys.PROXY_TYPE, 0);
            PrefsHelper.setPref(keys.PROXY_SOCKS,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,null);
        }
        else {
            PrefsHelper.setPref(keys.PROXY_TYPE, 1);
            PrefsHelper.setPref(keys.PROXY_SOCKS,constants.PROXY_SOCKS);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, 9050);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.PROXY_SOCKS_VERSION);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.PROXY_SOCKS_REMOTE_DNS);
        }
    }

    private void initializeProxy()
    {
        PrefsHelper.setPref(keys.PROXY_TYPE, 0);
        PrefsHelper.setPref(keys.PROXY_SOCKS,null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,null);

        PrefsHelper.setPref(keys.PROXY_TYPE, 1);
        PrefsHelper.setPref(keys.PROXY_SOCKS,constants.PROXY_SOCKS);
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, 9050);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.PROXY_SOCKS_VERSION);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.PROXY_SOCKS_REMOTE_DNS);

        PrefsHelper.setPref(keys.PROXY_CACHE,constants.PROXY_CACHE);
        PrefsHelper.setPref(keys.PROXY_MEMORY,constants.PROXY_MEMORY);
        PrefsHelper.setPref(keys.PROXY_USER_AGENT_OVERRIDE, constants.PROXY_USER_AGENT_OVERRIDE);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_ENABLED,constants.PROXY_DO_NOT_TRACK_HEADER_ENABLED);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_VALUE,constants.PROXY_DO_NOT_TRACK_HEADER_VALUE);

        PrefsHelper.setPref("browser.cache.disk.enable",true);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",10000);

        setPrivacyPrefs();
    }

    private void setProxyPrefs ()
    {
        PrefsHelper.setPref("network.proxy.type",1); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks","localhost"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_port",9050); //manual proxy settings
        PrefsHelper.setPref("network.proxy.http","localhost"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.http_port",8118); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_version",5); //manual proxy settings

    }

    void updateCookiesStatus(){
        //PrefsHelper.setPref("privacy.clearOnShutdown.cookies",!status.sCookieStatus);
        //PrefsHelper.setPref("network.cookie.cookieBehavior", status.sCookieStatus ? 1 : 0);
    }

    private void setPrivacyPrefs ()
    {
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref(keys.PROXY_USER_AGENT_OVERRIDE, constants.PROXY_USER_AGENT_OVERRIDE);
        PrefsHelper.setPref("browser.cache.disk.capacity",0);
        PrefsHelper.setPref("privacy.clearOnShutdown.cache",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.downloads",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.formdata",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.history",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.offlineApps",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.passwords",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.sessions",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.siteSettings",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
        //PrefsHelper.setPref("privacy.clearOnShutdown.cookies",!status.sCookieStatus);
        //PrefsHelper.setPref("network.cookie.cookieBehavior", status.sCookieStatus ? 1 : 0);
        PrefsHelper.setPref("network.http.sendRefererHeader", 0);
        PrefsHelper.setPref("security.OCSP.require", true);
        PrefsHelper.setPref("security.checkloaduri",true);
        PrefsHelper.setPref("security.mixed_content.block_display_content", true);
        PrefsHelper.setPref("media.peerconnection.enabled",false); //webrtc disabled
    }

    private void setCipherSuites ()
    {
        PrefsHelper.setPref("security.ssl3.ecdh_ecdsa_rc4_128_sha",false);
        PrefsHelper.setPref("security.ssl3.ecdh_rsa_rc4_128_sha",false);
        PrefsHelper.setPref("security.ssl3.ecdhe_ecdsa_rc4_128_sha",false);
        PrefsHelper.setPref("security.ssl3.ecdhe_rsa_rc4_128_sha",false);
        PrefsHelper.setPref("security.ssl3.rsa_rc4_128_md5",false);
        PrefsHelper.setPref("security.ssl3.rsa_rc4_128_sha",false);
    }


    private void setJavascriptEnabled ()
    {
        PrefsHelper.setPref("javascript.enabled", status.sJavaStatus);
    }

    String getLogs()
    {
        String logs = orbot_local_constants.tor_logs_status;

        if(!logs.contains("Bootstrapped") && !mLogsStarted){
            logs = "Initializing Bootstrap";
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
        return orbot_local_constants.sIsTorInitialized;
    }

}
