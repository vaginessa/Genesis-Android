package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.SessionConfig;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.dns.DnsRule;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.anchorfree.reporting.TrackingConstants;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import java.util.LinkedList;
import java.util.List;

class proxyManager
{
    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    private boolean is_running = false;
    private static final String channel_id = constants.channel_id;
    private static boolean proxy_started = false;
    private boolean is_loading = false;

    /*Initializations*/

    private static proxyManager ourInstance = new proxyManager();
    public static proxyManager getInstance()
    {
        return ourInstance;
    }

    private proxyManager(){
    }

    public void initialize(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
    }

    /*Initialize Hydra*/

    private void initHydraSdk() {


        SharedPreferences prefs = getPrefs();
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();

        HydraSdk.setLoggingLevel(Log.VERBOSE);

        HydraSDKConfig config = HydraSDKConfig.newBuilder()
                .observeNetworkChanges(true) //sdk will handle network changes and start/stop vpn
                .captivePortal(true) //sdk will handle if user is behind captive portal wifi
                .moveToIdleOnPause(false)//sdk will report PAUSED state
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(app_context.getResources().getString(R.string.app_name))
                .channelId(channel_id)
                .build();

        HydraSdk.init(app_context, clientInfo, notificationConfig, config);
        proxy_started = true;
    }

    private SharedPreferences getPrefs() {
        return app_context.getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    /*Hydra States*/

    private void connect() {
        AuthMethod authMethod = AuthMethod.anonymous();
        HydraSdk.login(authMethod, new Callback<User>()
        {
            @Override
            public void success(@NonNull User user)
            {
                if(status.gateway)
                {
                    startVPNConnection();
                }
             }

            @Override
            public void failure(@NonNull HydraException e)
            {
                loadBoogle();
            }
        });
    }


    private void startVPNConnection()
    {
        HydraSdk.startVPN(createConnectionRequest(), new Callback<ServerCredentials>()
        {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials)
            {
                is_running = true;
                loadBoogle();
            }

            @Override
            public void failure(@NonNull HydraException e)
            {
                loadBoogle();
            }
        });
    }

    /*Start Application*/

    private void loadBoogle()
    {
        is_running = true;
        //is_loading = false;
        event.invokeObserver(null, enums.eventType.disable_splash);
    }

    /*Helper Methods*/

    private SessionConfig createConnectionRequest()
    {
        List<String> bypassDomains = new LinkedList<>();

        bypassDomains.add(constants.bypassDomains_1);
        bypassDomains.add(constants.bypassDomains_2);

        SessionConfig.Builder builder = new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains));

        return builder.build();
    }

    /*External Helper Methods*/

    void startVPN() {
        if(!is_loading){
            is_loading = true;
            initHydraSdk();
        }
        connect();
    }
    void reset()
    {
        if(proxy_started){
            HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                @Override
                public void complete() {
                }

                @Override
                public void error(@NonNull HydraException e) {
                }
            });
        }
    }

    void disconnectConnection() {

        if(is_running){
            HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                @Override
                public void complete() {
                }

                @Override
                public void error(@NonNull HydraException e) {
                }
            });
        }
    }

    boolean isProxyRunning(){
        return is_running;
    }

}
