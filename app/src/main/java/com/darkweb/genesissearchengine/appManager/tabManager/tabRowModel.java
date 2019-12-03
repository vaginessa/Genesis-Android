package com.darkweb.genesissearchengine.appManager.tabManager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;

import org.mozilla.geckoview.GeckoSession;

public class tabRowModel
{
    /*Private Variables*/

    private geckoSession mSession;
    private int mId;

    /*Initializations*/

    public tabRowModel(geckoSession mSession,int mId) {
        this.mSession = mSession;
        this.mId = mId;
    }

    public geckoSession getSession()
    {
        return mSession;
    }

    public int getmId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }

}
