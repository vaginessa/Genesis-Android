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
    private AdView mBannerAds;
    private boolean bannerAdsLoading = false;
    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    adManager(AppCompatActivity app_context, eventObserver.eventListener event, AdView banner_ads) {
        this.mAppContext = app_context;
        this.mEvent = event;
        mBannerAds = banner_ads;
    }

    void loadAds(){
        if(!bannerAdsLoading){
            bannerAdsLoading = true;
            MobileAds.initialize(mAppContext, constants.ADMOB_KEY);
            mBannerAds.setAlpha(0f);
            initializeBannerAds();
        }
    }

    boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    /*Local Helper Methods*/

    private void admobListeners(){
            mBannerAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                bannerAdsLoaded = true;
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

    private void initializeBannerAds(){

        AdRequest request = new AdRequest.Builder().addTestDevice("635CEDE18D345A98A814121E983166E8").build();
        mBannerAds.loadAd(request);
        admobListeners();
    }

}
