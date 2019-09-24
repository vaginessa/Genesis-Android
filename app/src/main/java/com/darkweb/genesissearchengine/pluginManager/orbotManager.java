package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Preconditions;

import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyContext;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import com.msopentech.thali.toronionproxy.WriteObserver;

import org.mozilla.gecko.PrefsHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class orbotManager
{

    /*Private Variables*/
    private OnionProxyManager onionProxyManager = null;
    private OnionProxyContext onionProxyContext;

    private boolean isLoading = false;
    private int threadCounter = 100;
    private int onionProxyPort = 0;
    private boolean isTorInitialized = false;
    private boolean network_Error = false;

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
        initialize();
    }

    private void initialize(){
        autoValidator();
    }

    private void initContext(File workingDirectory){
        onionProxyContext = new OnionProxyContext(workingDirectory)
        {
            @Override
            public String getProcessId()
            {
                return null;
            }

            @Override
            public WriteObserver generateWriteObserver(File file)
            {
                return null;
            }

            @Override
            protected InputStream getAssetOrResourceByName(String fileName) throws IOException
            {
                return null;
            }
        };
    }
    /*Orbot Initialization*/

    private class getProxtStatus implements Callable<Boolean>
    {
        @Override
        public Boolean call()
        {
            try
            {
                if(!isLoading && !onionProxyManager.isNetworkEnabled())
                {
                    isTorInitialized = false;
                }
            }
            catch (Exception ignored)
            {
            }
            return Boolean.FALSE;
        }
    }

    private void autoValidator()
    {
        Thread validator_thread = new Thread() {
            @SuppressWarnings("InfiniteLoopStatement")
            public void run() {
                while (true) {
                    try {
                        if (onionProxyManager != null) {
                            if (onionProxyManager.isRunning() && onionProxyManager.isNetworkEnabled()) {
                                threadCounter = 5000;
                            } else {
                                isTorInitialized = false;
                            }
                        }
                        if (!isLoading && !isTorInitialized) {
                            if (helperMethod.isNetworkAvailable(app_context)) {
                                network_Error = false;
                                if (onionProxyManager == null) {
                                    onionProxyManager = new AndroidOnionProxyManager(app_context.getApplicationContext(), strings.torfolder);
                                }
                                isLoading = false;
                                isTorInitialized = false;
                                initializeTorClient();
                            } else {
                                network_Error = true;
                            }
                        } else {
                            sleep(threadCounter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        validator_thread.start();
    }

    private void initializeTorClient()
    {
        isTorInitialized = false;
        if(!isLoading)
        {
            isLoading = true;
            Thread init_thread = new Thread() {
                public void run() {
                    while (true) {
                        try {

                            int totalSecondsPerTorStartup = 4 * 60;
                            int totalTriesPerTorStartup = 5;

                            initContext(onionProxyManager.getWorkingDirectory());
                            initBridgeGateway();

                            boolean ok = onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup);

                            if (!ok) {
                                continue;
                            }

                            onionProxyPort = onionProxyManager.getIPv4LocalHostSocksPort();

                            initializeProxy();
                            isLoading = false;
                            break;

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            };
            init_thread.start();
        }
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    void setProxy(boolean status){
        if(status){
            PrefsHelper.setPref(keys.proxy_type, 1);
            PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks);
            PrefsHelper.setPref(keys.proxy_socks_port, onionProxyPort);
            PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version);
            PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns);
        }
        else {
            PrefsHelper.setPref(keys.proxy_type, 0);
            PrefsHelper.setPref(keys.proxy_socks,null);
            PrefsHelper.setPref(keys.proxy_socks_port, null);
            PrefsHelper.setPref(keys.proxy_socks_version,null);
            PrefsHelper.setPref(keys.proxy_socks_remote_dns,null);
        }
    }

    private void initializeProxy()
    {
        isTorInitialized = true;
        status.isTorInitialized = true;
        PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks);
        PrefsHelper.setPref(keys.proxy_socks_port, onionProxyPort);
        PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version);
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns);
        PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);

        PrefsHelper.setPref("browser.cache.disk.enable",true);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",10000);


    }

    private synchronized void initBridgeGateway() throws IOException, InterruptedException {

        //onionProxyContext.installFiles();

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(onionProxyContext.getTorrcFile(), true)))) {
            printWriter.println("CookieAuthFile " + onionProxyContext.getCookieFile().getAbsolutePath());
            // For some reason the GeoIP's location can only be given as a file
            // name, not a path and it has
            // to be in the data directory so we need to set both
            printWriter.println("DataDirectory " + onionProxyContext.getWorkingDirectory().getAbsolutePath());
            printWriter.println("GeoIPFile " + onionProxyContext.getGeoIpFile().getName());
            printWriter.println("GeoIPv6File " + onionProxyContext.getGeoIpv6File().getName());

            if (true) {
                List<String> bridges = BridgeProvider.getBridges();
                Preconditions.checkNotNull(bridges, "Bridges must not be null");
                Preconditions.checkArgument(!bridges.isEmpty(), "Bridges must not be empty");

                printWriter.println("UseBridges 1");
                for (String bridgeLine : bridges)
                    printWriter.println(bridgeLine);
            }
        }

    }

    /*External Helper Methods*/

    String getLogs()
    {
        if(network_Error){
            return "Internet Error | Failed To Connect";
        }
        else {
            if(onionProxyManager!=null)
            {
                String Logs = onionProxyManager.getLastLog();
                if(Logs.equals(""))
                {
                    return "Installing | Setting Configurations";
                }
                Logs="Installing | " + Logs.replace("FAILED","Securing");
                return Logs;
            }
            return "Loading Please Wait";
        }
    }

    boolean isOrbotRunning(){
        Log.i("TEST1","TEST1:"+status.isTorInitialized);
        return status.isTorInitialized;
    }

}
