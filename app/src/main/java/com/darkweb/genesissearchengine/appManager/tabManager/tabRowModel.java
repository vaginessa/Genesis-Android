package com.darkweb.genesissearchengine.appManager.tabManager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.mozilla.geckoview.GeckoSession;

public class tabRowModel
{
    /*Private Variables*/

    private int mId;
    private String mHeader;
    private String mDescription;
    private GeckoSession mSession;
    private int mProgress=0;

    /*Local Variables*/

    private GeckoSession.ContentDelegate mSavedContentDelegate;
    private GeckoSession.NavigationDelegate mSavedNavigationDelegate;
    private GeckoSession.ProgressDelegate mSavedProgressDelegate;

    /*Initializations*/

    public tabRowModel(GeckoSession mSession, int mId, String mHeader, String mDescription,int mProgress) {
        this.mId = mId;
        this.mSession = mSession;
        this.mHeader = mHeader;
        this.mDescription = mDescription;
        this.mProgress = mProgress;
        sessionListener();
    }

    /*Delegate Update Listeners*/

    private void sessionListener(){
        mSavedContentDelegate = mSession.getContentDelegate();
        mSavedNavigationDelegate = mSession.getNavigationDelegate();
        mSavedProgressDelegate = mSession.getProgressDelegate();

        mSession.setContentDelegate(new ContentDelegate());
        mSession.setNavigationDelegate(new navigationDelegate());
        mSession.setProgressDelegate(new progressDelegate());
    }

    /*Variable Getters*/

    public GeckoSession getmSession() {
        mSession.setContentDelegate(mSavedContentDelegate);
        mSession.setNavigationDelegate(mSavedNavigationDelegate);
        mSession.setProgressDelegate(mSavedProgressDelegate);
        return mSession;
    }
    public String getmHeader(){
        return mHeader;
    }
    public int getProgress(){
        return mProgress;
    }
    public String getmDescription(){
        return mDescription;
    }
    int getmId() {
        return mId;
    }

    public void setmHeader(String mHeader){
        this.mHeader = mHeader;
    }
    public void setmDescription(String mDescription){
        this.mDescription = mDescription;
    }


    /*Content Delegate*/

    private class ContentDelegate implements GeckoSession.ContentDelegate
    {
        @Override
        public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
            Log.i("WHY1:",var2);
            mHeader = var2;
        }
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2)
        {
            Log.i("WHY2:",var2);
            mDescription = var2;
        }
    }
    class progressDelegate implements GeckoSession.ProgressDelegate
    {
        @Override
        public void onProgressChange(@NonNull GeckoSession session, int progress)
        {
            mProgress = progress;
        }
    }
}
