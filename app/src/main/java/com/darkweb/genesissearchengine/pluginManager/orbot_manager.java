package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import org.mozilla.gecko.PrefsHelper;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class orbot_manager {

    /*Private Variables*/

    private static boolean isLoading = false;
    private int threadCounter = 100;
    private static OnionProxyManager onionProxyManager = null;
    private Handler updateUIHandler = null;

    /*Local Initialization*/

    private static final orbot_manager ourInstance = new orbot_manager();

    public static orbot_manager getInstance()
    {
        return ourInstance;
    }

    private orbot_manager()
    {
        createUpdateUiHandler();
    }

    /*Orbot Initialization*/

    public boolean initOrbot(String url)
    {
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
            fabricManager.getInstance().sendEvent("TOR NOT INITIALIZED : " + url);
            message_manager.getInstance().startingOrbotInfo(url);
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
            catch (Exception e)
            {
            }
            return Boolean.FALSE;
        }
    }

    public void reinitOrbot()
    {
        new Thread()
        {
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
                            if(helperMethod.isNetworkAvailable())
                            {
                                if(onionProxyManager == null)
                                {
                                    onionProxyManager = new AndroidOnionProxyManager(home_model.getInstance().getAppContext(), strings.torfolder);
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

                            home_model.getInstance().setPort(onionProxyManager.getIPv4LocalHostSocksPort());
                            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", onionProxyManager.getIPv4LocalHostSocksPort()));
                            startPostTask(messages.REINIT_HIDDEN);
                            isLoading = false;
                            break;

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
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

    public Proxy proxy;


    private void startPostTask(int m_id){
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler(){
        updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                initializeProxy();
            }
        };
    }

    public void initializeProxy()
    {
        status.isTorInitialized = true;
        PrefsHelper.setPref(keys.proxy_type, constants.proxy_type);
        PrefsHelper.setPref(keys.proxy_socks,constants.proxy_socks);
        PrefsHelper.setPref(keys.proxy_socks_port, home_model.getInstance().getPort());
        PrefsHelper.setPref(keys.proxy_socks_version,constants.proxy_socks_version);
        PrefsHelper.setPref(keys.proxy_socks_remote_dns,constants.proxy_socks_remote_dns);
        PrefsHelper.setPref(keys.proxy_cache,constants.proxy_cache);
        PrefsHelper.setPref(keys.proxy_memory,constants.proxy_memory);
        PrefsHelper.setPref(keys.proxy_useragent_override, constants.proxy_useragent_override);
        PrefsHelper.setPref(keys.proxy_donottrackheader_enabled,constants.proxy_donottrackheader_enabled);
        PrefsHelper.setPref(keys.proxy_donottrackheader_value,constants.proxy_donottrackheader_value);
    }


}
