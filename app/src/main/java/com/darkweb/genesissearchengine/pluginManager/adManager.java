package com.darkweb.genesissearchengine.pluginManager;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.google.android.gms.ads.*;

class adManager
{

    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private eventObserver.eventListener mEvent;
    private AdView mBannerAds = null;

    /*Initializations*/

    adManager(AppCompatActivity app_context, eventObserver.eventListener event, AdView banner_ads) {
        this.mAppContext = app_context;
        this.mEvent = event;
        initialize(banner_ads);
    }

    private void initialize(AdView banner_ads){
        this.mBannerAds = banner_ads;
        MobileAds.initialize(mAppContext, constants.ADMOB_KEY);
        banner_ads.setAlpha(0f);
    }

    /*Local Helper Methods*/

    private void admobListeners(){
            mBannerAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("Success___","Success");
                mEvent.invokeObserver(null,null);
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
        mBannerAds.loadAd(request);
        admobListeners();
    }

}
