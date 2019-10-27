package com.darkweb.genesissearchengine.appManager.homeManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;

import org.mozilla.geckoview.*;

import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.enums.etype.on_handle_external_intent;

class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private int mSessionID=0;

    private eventObserver.eventListener event;
    private AppCompatActivity context;

    void initialize(GeckoView geckoView, AppCompatActivity Context,eventObserver.eventListener event,String searchEngine,AppCompatActivity context)
    {
        this.context = context;
        this.event = event;
        mSessionID += 1;
        mSession = new geckoSession(new geckoViewClientCallback(),mSessionID,context);
        mRuntime = GeckoRuntime.getDefault(Context);
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        mSession.open(mRuntime);
        mSession.getSettings().setUseTrackingProtection(true);
        mSession.setPromptDelegate(new geckoPromptView(context));

        mSession.setTitle("New Tab");
        geckoView.releaseSession();
        geckoView.setSession(mSession);
        onUpdateFont();
    }

    void initSession(geckoSession mSession){
        this.mSession = mSession;
        mSessionID = mSession.getSessionID();
    }

    geckoSession getSession(){
        return mSession;
    }

    void loadURL(String url){
        mSession.loadUri(url);
    }

    void onBackPressed(boolean isFinishAllowed){
        if(mSession.canGoBack()){
            mSession.goBack();
        }
        else if(isFinishAllowed){
            event.invokeObserver(null, enums.etype.back_list_empty);
        }
    }

    boolean getFullScreenStatus(){
        return mSession.getFullScreenStatus();
    }

    void exitFullScreen(){
        mSession.exitFullScreen();
    }

    void onForwardPressed(){
        if(mSession.canGoForward()){
            mSession.goForward();
        }
    }

    void onStop(){
        mSession.stop();
    }

    void onReload(){
        mSession.reload();
    }
    /*Session Updates*/

    void onUpdateSettings(){
        mSession.reload();
    }

    void onUpdateFont(){
        float font = (status.sFontSize -100)/4+100;
        mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        if(!mRuntime.getSettings().getAutomaticFontSizeAdjustment()){
            mRuntime.getSettings().setFontSizeFactor(font/100);
        }
    }

    public class geckoViewClientCallback implements eventObserver.eventListener
    {
        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if (mSessionID == (int)data.get(1))
            {
                Log.i("CSESSION",mSessionID+"___"+e_type);
                if (e_type.equals(on_handle_external_intent))
                {
                    GeckoSession.WebResponseInfo responseInfo = (GeckoSession.WebResponseInfo)data;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndTypeAndNormalize(Uri.parse(responseInfo.uri), responseInfo.contentType);
                    context.startActivity(intent);
                } else
                {
                    event.invokeObserver(data, e_type);
                }
            }
        }
    }
}
