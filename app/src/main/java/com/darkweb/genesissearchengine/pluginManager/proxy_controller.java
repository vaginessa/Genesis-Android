package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.SessionConfig;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.callbacks.VpnStateListener;
import com.anchorfree.hydrasdk.dns.DnsRule;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.anchorfree.reporting.TrackingConstants;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import java.util.LinkedList;
import java.util.List;

public class proxy_controller {

    /*INITIALIZATIONS*/

    private static final proxy_controller ourInstance = new proxy_controller();
    public static proxy_controller getInstance() {
        return ourInstance;
    }

    /*LOCAL VARIABLE DECLARATIONS*/

    private boolean isLoading = false;
    private static final String CHANNEL_ID = "vpn";
    private static VPNState currentVpnState = VPNState.IDLE;

    /*HELPER METHODS*/

    public void autoStart()
    {
        startVPN();
    }

    public void disconnectConnection() {

        HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(HydraException e) {
            }
        });
    }

    /*First Time Installations*/

    private SessionConfig createConnectionRequest()
    {
        List<String> bypassDomains = new LinkedList<>();

        bypassDomains.add("*facebook.com");
        bypassDomains.add("*wtfismyip.com");

        SessionConfig.Builder builder = new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains));

        SessionConfig build_res = builder.build();
        return build_res;
    }

    private void connect() {
        AuthMethod authMethod = AuthMethod.anonymous();
        HydraSdk.login(authMethod, new Callback<User>()
        {
            @Override
            public void success(User user)
            {
                if(status.gateway == true)
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

    public void startVPNConnection()
    {
        HydraSdk.startVPN(createConnectionRequest(), new Callback<ServerCredentials>()
        {
            @Override
            public void success(ServerCredentials serverCredentials)
            {
                loadBoogle();
            }

            @Override
            public void failure(@NonNull HydraException e)
            {
                loadBoogle();
            }
        });
    }

    public void loadBoogle()
    {
        if(status.search_status.equals("Duck Duck Go"))
        {
            home_model.getInstance().getHomeInstance().initBoogle();
        }
    }

    private void startVPN() {
        initHydraSdk();
        connect();
    }

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
                .title(home_model.getInstance().getHomeInstance().getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();

        HydraSdk.init(home_model.getInstance().getHomeInstance(), clientInfo, notificationConfig, config);
    }

    private SharedPreferences getPrefs() {
        return home_model.getInstance().getHomeInstance().getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }
}
