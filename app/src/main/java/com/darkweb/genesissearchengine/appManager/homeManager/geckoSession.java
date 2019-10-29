package com.darkweb.genesissearchengine.appManager.homeManager;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.downloadFileService;
import com.darkweb.genesissearchengine.helperManager.errorHandler;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebRequestError;
import java.util.Arrays;

public class geckoSession extends GeckoSession implements GeckoSession.ProgressDelegate, GeckoSession.HistoryDelegate,GeckoSession.NavigationDelegate,GeckoSession.ContentDelegate
{
    private eventObserver.eventListener event;

    private int mSessionID;
    private boolean mCanGoBack = false;
    private boolean mCanGoForward = false;
    private boolean mFullScreen = false;
    private int mProgress = 0;
    private String mCurrentTitle = strings.EMPTY_STR;
    private String mCurrentURL = strings.EMPTY_STR;
    private AppCompatActivity mContext;
    private geckoDownloadManager mDownloadManager;

    geckoSession(eventObserver.eventListener event,int mSessionID,AppCompatActivity mContext){

        this.mContext = mContext;
        this.mSessionID = mSessionID;
        setProgressDelegate(this);
        setHistoryDelegate(this);
        setNavigationDelegate(this);
        setContentDelegate(this);
        mDownloadManager = new geckoDownloadManager();

        this.event = event;
    }

    /*Progress Delegate*/

    @Override
    public void onPageStart(@NonNull GeckoSession var1, @NonNull String var2) {
        mProgress = 5;
    }

    @Override
    public void onProgressChange(@NonNull GeckoSession session, int progress)
    {
        if(!mFullScreen){
            mProgress = progress;
            event.invokeObserver(Arrays.asList(mProgress,mSessionID), enums.etype.progress_update);
        }
    }

    /*History Delegate*/
    @Override
    public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
        if(var4==3 || var4==5 || var4==1){
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_url_load);
            event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_request_completed);
        }
        return null;
    }

    /*Navigation Delegate*/
    public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {
        mCurrentURL = var2;
        if (var2 != null && !var2.equals("about:blank"))
        {
            event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_page_loaded);
        }
    }

    public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {
        if(var1.target==2){
            event.invokeObserver(Arrays.asList(var1.uri,mSessionID), enums.etype.open_new_tab);
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }
        else {
            event.invokeObserver(Arrays.asList(mProgress,mSessionID), enums.etype.start_proxy);
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }
    }

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoBack = var2;
    }

    @Override
    public void onCanGoForward(@NonNull GeckoSession session, boolean var2)
    {
        mCanGoForward = var2;
    }

    public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, WebRequestError var3) {
        errorHandler handler = new errorHandler();
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_load_error);
        return GeckoResult.fromValue("data:text/html," + handler.createErrorPage(var3.category, var3.code,mContext,var2));
    }

    /*Content Delegate*/
    @Override
    public void onExternalResponse(@NonNull GeckoSession session, @NonNull GeckoSession.WebResponseInfo response) {
        try {
            event.invokeObserver(Arrays.asList(response,mSessionID), enums.etype.on_handle_external_intent);
        } catch (ActivityNotFoundException e) {
            mDownloadManager.downloadFile(response,this,mContext,event);
            stop();
        }
    }

    @UiThread
    public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
        if(var2.equals(strings.EMPTY_STR)){
            mCurrentTitle = URLUtil.guessFileName(mCurrentURL, null, null);
        }else {
            mCurrentTitle = var2;
        }
    }

    @Override
    public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
        mFullScreen = var2;
        event.invokeObserver(Arrays.asList(var2,mSessionID), enums.etype.on_full_screen);
    }

    public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {

        if(var4.type==1){
            if(var4.linkUri!=null){
                event.invokeObserver(Arrays.asList(var4.srcUri,mSessionID,var4.linkUri), enums.etype.on_long_press_with_link);
            }
            else {
                event.invokeObserver(Arrays.asList(var4.srcUri,mSessionID), enums.etype.on_long_press);
            }
        }
        else if(var4.type==0){
            event.invokeObserver(Arrays.asList(var4.linkUri,mSessionID), enums.etype.on_long_press_url);
        }
    }

    /*Download Manager*/

    void downloadRequestedFile()
    {
        mContext.startService(downloadFileService.getDownloadService(mContext, mDownloadManager.getDownloadURL()+"__"+mDownloadManager.getDownloadFile(), Environment.DIRECTORY_DOWNLOADS));
    }

    void downloadRequestedFile(Uri downloadURL,String downloadFile)
    {
        mContext.startService(downloadFileService.getDownloadService(mContext, downloadURL+"__"+downloadFile, Environment.DIRECTORY_DOWNLOADS));
    }

    /*Helper Methods*/

    public String getCurrentURL(){
        return mCurrentURL;
    }

    public String getTitle(){
        return mCurrentTitle;
    }

    public int getProgress(){
        return mProgress;
    }

    public void setTitle(String title){
        mCurrentTitle = title;
    }

    boolean canGoBack(){
        return mCanGoBack;
    }

    boolean canGoForward(){
        return mCanGoForward;
    }

    public int getSessionID(){
        return mSessionID;
    }

    void exitScreen(){
        this.exitFullScreen();
    }

    boolean getFullScreenStatus(){
        return mFullScreen;
    }

    public void closeSession(){
        event.invokeObserver(Arrays.asList(null,mSessionID), enums.etype.on_close_sesson);
    }

}
