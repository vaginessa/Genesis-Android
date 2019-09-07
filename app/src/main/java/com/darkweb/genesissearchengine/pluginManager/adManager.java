package com.darkweb.genesissearchengine.pluginManager;

import android.util.Log;

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
                Log.i("Success___","Success");
                event.invokeObserver(null,null);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("Failure___",""+errorCode);
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
        AdRequest request = new AdRequest.Builder().addTestDevice("41B9A0495CE25FCA44B3186D6B8268F0").build();
        banner_ads.loadAd(request);
        admobListeners();
    }

}
