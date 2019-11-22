package com.darkweb.genesissearchengine.appManager.homeManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import org.mozilla.geckoview.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.enums.etype.on_handle_external_intent;
import static org.mozilla.geckoview.StorageController.ClearFlags.ALL;

class geckoClients
{
    /*Gecko Variables*/

    private geckoSession mSession = null;
    private GeckoRuntime mRuntime = null;
    private int mSessionID=0;

    private eventObserver.eventListener event;
    private AppCompatActivity context;

    int e=0;
    void initialize(GeckoView geckoView, AppCompatActivity Context,eventObserver.eventListener event,String searchEngine,AppCompatActivity context)
    {

        this.context = context;
        this.event = event;
        mSessionID += 1;

        mSession = new geckoSession(new geckoViewClientCallback(),mSessionID,context);
        runtimeSettings(context);
        mSession.open(mRuntime);
        mSession.getSettings().setUseTrackingProtection(true);
        mSession.getSettings().setAllowJavascript(status.sJavaStatus);
        mSession.setTitle("New Tab");
        geckoView.releaseSession();
        geckoView.setSession(mSession);
        onUpdateFont();

    }

    private void runtimeSettings(AppCompatActivity context){
        if(mRuntime==null){
            mRuntime = GeckoRuntime.getDefault(context);
            mRuntime.getSettings().getContentBlocking().setCookieBehavior(getCookiesBehaviour());
            mRuntime.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        }
    }

    private int getCookiesBehaviour(){
        return status.sCookieStatus;
    }

    void updateCookies(){
        mRuntime.getSettings().getContentBlocking().setCookieBehavior(status.sCookieStatus);
        onReload();
    }

    void onClose(){
        if(mRuntime!=null){
            //mRuntime.shutdown();
        }
     }

    void initSession(geckoSession mSession){
        this.mSession = mSession;
        mSessionID = mSession.getSessionID();
    }

    geckoSession getSession(){
        return mSession;
    }

    void updateJavascript(){
        mSession.getSettings().setAllowJavascript(status.sJavaStatus);
        if(status.sJavaStatus){
           mSession.reload();
        }
    }

    void onUploadRequest(int resultCode,Intent data){
        mSession.onFileUploadRequest(resultCode,data);
    }

    void setLoading(boolean status){
        mSession.setLoading(status);
    }

    void loadURL(String url){
        mSession.loadUri(url);
    }

    void onClearSession(){
        mRuntime.getStorageController().clearData(ALL);
    }

    void onBackPressed(boolean isFinishAllowed){
        if(mSession.canGoBack()){
            mSession.goBackSession();
        }
        else if(isFinishAllowed){
            event.invokeObserver(null, enums.etype.back_list_empty);
        }
    }

    boolean canGoBack(){
        return mSession.canGoBack();
    }

    boolean canGoForward(){
        return mSession.canGoForward();
    }

    boolean isLoading(){
        return mSession.isLoading();
    }

    boolean getFullScreenStatus(){
        return mSession.getFullScreenStatus();
    }

    void onExitFullScreen(){
        mSession.exitScreen();
    }

    void onForwardPressed(){
        if(mSession.canGoForward()){
            mSession.goForwardSession();
        }
    }

    void onStop(){
        mSession.stop();
    }

    void onReload(){
        mSession.reload();
    }

    void manual_download(String url, AppCompatActivity context){
        Uri downloadURL = Uri.parse(url);
        File f = new File(url);
        String downloadFile = f.getName() != null ? f.getName() : downloadURL.getLastPathSegment();

        /*EXTERNAL STORAGE REQUEST*/
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile(downloadURL,downloadFile);
        }
    }

    void downloadFile()
    {
        if(helperMethod.checkPermissions(context)){
            mSession.downloadRequestedFile();
        }
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

    public class geckoViewClientCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if (mSessionID == (int)data.get(1))
            {
                if (e_type.equals(on_handle_external_intent))
                {
                    GeckoSession.WebResponseInfo responseInfo = (GeckoSession.WebResponseInfo)data.get(0);
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
