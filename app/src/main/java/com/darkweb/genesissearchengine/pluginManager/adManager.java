package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.constants;
import com.google.android.gms.ads.*;

class adManager
{

    /*Private Variables*/

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;
    private AdView banner_ads = null;

    /*Initializations*/

    adManager(AppCompatActivity app_context, eventObserver.eventListener event, AdView banner_ads) {
        this.app_context = app_context;
        this.event = event;
        initialize(banner_ads);
    }

    private void initialize(AdView banner_ads){
        this.banner_ads = banner_ads;
        MobileAds.initialize(app_context, constants.admobKey);
        banner_ads.setAlpha(0f);
    }

    /*Local Helper Methods*/

    private void admobListeners(){
            banner_ads.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                event.invokeObserver(null,null);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
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

    /*External Helper Methods*/

    void initializeBannerAds(){
        AdRequest request = new AdRequest.Builder().addTestDevice(constants.testKey).build();
        banner_ads.loadAd(request);
        admobListeners();
    }

}
