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

    void setProxy(){
    }

    private void initializeProxy()
    {
        setProxyPrefs();
        setPrivacyPrefs();
        setCipherSuites();
        setJavascriptEnabled ();
    }

    private void setProxyPrefs ()
    {
        PrefsHelper.setPref("network.proxy.type",1); //manual proxy settings
        PrefsHelper.setPref("network.proxy.http","localhost"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.http_port",8118); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks","localhost"); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_port",9050); //manual proxy settings
        PrefsHelper.setPref("network.proxy.socks_version",5); //manual proxy settings
    }

    private void setPrivacyPrefs ()
    {
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref(keys.PROXY_USER_AGENT_OVERRIDE, constants.PROXY_USER_AGENT_OVERRIDE);
        PrefsHelper.setPref("browser.cache.disk.capacity",0);
        PrefsHelper.setPref("privacy.clearOnShutdown.cache",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.cookies",!status.sCookieStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.downloads",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.formdata",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.history",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.offlineApps",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.passwords",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.sessions",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.clearOnShutdown.siteSettings",status.sHistoryStatus);
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
        PrefsHelper.setPref("network.cookie.cookieBehavior", status.sCookieStatus);
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
