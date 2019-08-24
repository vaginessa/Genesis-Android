package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import org.mozilla.gecko.PrefsHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class orbotManager
{

    /*Private Variables*/

    private static boolean isLoading = false;
    private int threadCounter = 100;
    private static OnionProxyManager onionProxyManager = null;
    private Handler updateUIHandler = null;

    private pluginController plugin_controller;
    private AppCompatActivity app_context;

    /*Initialization*/

    private static final orbotManager ourInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return ourInstance;
    }
    private orbotManager(){
        createUpdateUiHandler();
        plugin_controller = pluginController.getInstance();
        app_context = plugin_controller.getAppContext();
    }

    /*Orbot Initialization*/

    public boolean initOrbot(String url){
        try
        {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            reCheckProxyStatus task = new reCheckProxyStatus();
            Future<Boolean> future = executor.submit(task);
            future.get();
        }
        catch (Exception ex)
        {
            return false;
        }

        if(!status.isTorInitialized)
        {
            messageManager.getInstance().setData(url);
            messageManager.getInstance().createMessage(enums.popup_type.start_orbot);
            return false;
        }
        else
        {
            return true;
        }
    }

    static public class reCheckProxyStatus implements Callable<Boolean>
    {
        @Override
        public Boolean call()
        {
            try
            {
                if(!isLoading && !onionProxyManager.isNetworkEnabled())
                {
                    status.isTorInitialized = false;
                }
            }
            catch (Exception ignored)
            {
            }
            return Boolean.FALSE;
        }
    }

    public void reinitOrbot()
    {
        new Thread()
        {
            @SuppressWarnings("InfiniteLoopStatement")
            public void run()
            {
                while (true)
                {
                    try
                    {
                        if(onionProxyManager!=null)
                        {
                            if(onionProxyManager.isRunning() && onionProxyManager.isNetworkEnabled())
                            {
                                threadCounter = 5000;
                            }
                            else
                            {
                                status.isTorInitialized = false;
                            }
                        }
                        if(!isLoading && !status.isTorInitialized)
                        {
                            if(helperMethod.isNetworkAvailable(app_context))
                            {
                                if(onionProxyManager == null)
                                {
                                    onionProxyManager = new AndroidOnionProxyManager(app_context, strings.torfolder);
                                }
                                isLoading = false;
                                status.isTorInitialized = false;
                                initializeTorClient();
                            }
                        }
                        else
                        {
                            sleep(threadCounter);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private void initializeTorClient()
    {
        status.isTorInitialized = false;
        if(!isLoading)
        {
            new Thread()
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            isLoading = true;

                            int totalSecondsPerTorStartup = 4 * 60;
                            int totalTriesPerTorStartup = 5;

                            boolean ok = onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup);

                            if (!ok)
                            {
                                continue;
                            }

                            plugin_controller.setPort(onionProxyManager.getIPv4LocalHostSocksPort());
                            startPostTask();
                            isLoading = false;
                            break;

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }.start();
        }
    }

    /*Helper Methods*/

    public String getLogs()
    {
        if(onionProxyManager!=null)
        {
            String Logs = onionProxyManager.getLastLog();
            if(Logs.equals(""))
            {
                return "Loading Please Wait";
            }
            Logs=Logs.replace("FAILED","Securing");
            return Logs;
        }
        return "Loading Please Wait";
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    private void startPostTask(){
        Message message = new Message();
        message.what = messages.REINIT_HIDDEN;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler(){
        updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg)
            {
                initializeProxy();
            }
        };
    }

    private void initializeProxy()
    {
        status.isTorInitialized = true;
        PrefsHelper.setPref(keys.proxy_type, constants.proxy_type);
        PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks);
        PrefsHelper.setPref(keys.proxy_socks_port, plugin_controller.getPort());
        PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version);
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns);
        PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);
    }


}
