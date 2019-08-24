package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.constants;
import com.google.android.gms.ads.*;

public class adManager
{

    /*Private Variables*/

    private AdView banner_ads = null;
    private pluginController plugin_controller;
    private AppCompatActivity app_context;

    /*Initializations*/

    private static final adManager ourInstance = new adManager();
    public static adManager getInstance() {
        return ourInstance;
    }

    private adManager() {
        plugin_controller = pluginController.getInstance();
        app_context = plugin_controller.getAppContext();
    }

    public void initialize(AdView banner_ads){
        this.banner_ads = banner_ads;
        MobileAds.initialize(app_context, constants.admobKey);
        initBannerAds();
    }

    private void initBannerAds(){
        AdRequest request = new AdRequest.Builder().addTestDevice(constants.testKey).build();
        banner_ads.loadAd(request);
        admobListeners();
    }

    /*Helper Methods*/

    private void admobListeners(){
            banner_ads.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                plugin_controller.onBannerAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
                plugin_controller.onBannerAdLoaded();
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }
}
