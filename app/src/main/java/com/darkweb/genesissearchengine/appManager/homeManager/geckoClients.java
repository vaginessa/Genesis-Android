package com.darkweb.genesissearchengine.appManager.homeManager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.downloadFileService;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import org.mozilla.geckoview.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

class geckoClients
{
    /*Gecko Variables*/

    private GeckoSession session1 = null;
    private GeckoRuntime runtime1 = null;
    private eventObserver.eventListener event;
    private AppCompatActivity context;

    /*Private Variables*/
    private LinkedList<GeckoSession.WebResponseInfo> mPendingDownloads = new LinkedList<>();
    private ArrayList<String> url_list = new ArrayList<>();
    private boolean canGoBack = false;
    private boolean onGoBack = false;
    private boolean on_page_loaded = false;
    private boolean on_page_error = false;
    private String current_url = strings.EMPTY_STR;
    private String requested_url = strings.EMPTY_STR;
    private String current_title = strings.EMPTY_STR;
    private int rate_us_counter = 0;
    private boolean maximize_status = false;

    private Uri downloadURL;
    private String downloadFile = "";
    private geckoPromptView prompt;
    private GeckoView geckoView;


    void initialize(GeckoView geckoView, AppCompatActivity Context,eventObserver.eventListener event,String searchEngine,AppCompatActivity context)
    {
        this.context = context;
        this.event = event;
        this.geckoView = geckoView;
        prompt = new geckoPromptView(context);
        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(Context);

        runtime1.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        if(!status.sFontAdjustable){
            float font = status.sFontSize;
            font = (status.sFontSize -100)/4+100;
            if(!runtime1.getSettings().getAutomaticFontSizeAdjustment()){
                runtime1.getSettings().setFontSizeFactor(font/100);
            }
        }

        geckoView.releaseSession();
        session1.open(runtime1);
        geckoView.setSession(session1);
        session1.getSettings().setUseTrackingProtection(true);
        session1.setProgressDelegate(new progressDelegate());
        session1.setNavigationDelegate(new navigationDelegate());
        session1.setHistoryDelegate(new historyDelegate());
        session1.setContentDelegate(new ContentDelegate());
        session1.setPromptDelegate(prompt);
    }

    public String getTitle(){
        return current_title;
    }

    public void setTitle(String title){
        current_title = title;
    }

    void loadURL(String url){
        session1.loadUri(url);
    }

    void loadSession(GeckoSession session){
        session1=session;
    }

    boolean isSessionRunning(){
        return !on_page_loaded;
    }

    void setRequestedUrl(String url){
        requested_url = url;
    }

    void onBackPressed(){
        if(canGoBack){
            if(url_list.size()>1){
                url_list.remove(url_list.size()-1);
                updateProxy("bbc");
                requested_url = url_list.get(url_list.size()-1);
            }
            session1.goBack();
            onGoBack = true;
        }
        else {
            event.invokeObserver(null, enums.etype.back_list_empty);
        }
    }

    void onUpdateSettings(){
        session1.reload();
    }

    void onUpdateFont(){
        float font = status.sFontSize;
        font = (status.sFontSize -100)/4+100;
        runtime1.getSettings().setAutomaticFontSizeAdjustment(status.sFontAdjustable);
        if(!runtime1.getSettings().getAutomaticFontSizeAdjustment()){
            runtime1.getSettings().setFontSizeFactor(font/100);
        }
    }

    /*Delegate Handler*/

    class progressDelegate implements GeckoSession.ProgressDelegate
    {
        @Override
        public void onPageStart(GeckoSession session, String url)
        {
            if(geckoView.getSession().equals(session)){
                current_title = "";
                if(onGoBack){
                    event.invokeObserver(Collections.singletonList(url), enums.etype.on_url_load);
                }

                if(on_page_loaded){
                    event.invokeObserver(Collections.singletonList(url), enums.etype.search_update);
                }

                on_page_loaded = false;
                on_page_error = false;

                event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);
            }
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success)
        {
            if(geckoView.getSession().equals(session)){
                if(on_page_loaded){
                    if(success){
                        on_page_loaded = false;
                        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);
                        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);

                        if(!status.sIsAppRated && current_url.contains(".onion")){
                            rate_us_counter+=1;
                            if(rate_us_counter>5){
                                event.invokeObserver(Collections.singletonList(true), enums.etype.rate_application);
                                status.sIsAppRated = true;
                            }
                        }
                        event.invokeObserver(null, enums.etype.on_page_loaded);
                        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);
                    }
                    if(!on_page_error){
                        event.invokeObserver(Collections.singletonList(100), enums.etype.progress_update);
                    }
                }
            }
        }

        @Override
        public void onProgressChange(GeckoSession session, int progress)
        {
            if (geckoView.getSession().equals(session))
            {
                int status = 0;
                if (progress > 10)
                {
                    status = progress;
                    on_page_loaded = true;
                } else
                {
                    status = 10;
                }
                if (maximize_status)
                {
                    event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);
                } else
                {
                    event.invokeObserver(Collections.singletonList(status), enums.etype.progress_update);
                }
            }
        }
    }

    class historyDelegate implements GeckoSession.HistoryDelegate
    {
        public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
            if(var4==3 || var4==5 || var4==1){
                if(geckoView.getSession().equals(var1)){
                    event.invokeObserver(Collections.singletonList(var2), enums.etype.on_url_load);
                }
                event.invokeObserver(Collections.singletonList(var2), enums.etype.on_request_completed);
            }
            return null;
        }

        public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull GeckoSession.HistoryDelegate.HistoryList var2) {

        }
    }

    boolean getFullScreenStatus(){
        return maximize_status;
    }

    void exitFullScreen(){
        session1.exitFullScreen();
    }

    String getRequestedURL(){
        return requested_url;
    }

    String getCurrentURL(){
        return current_url;
    }

    void setCurrentURL(String url){
        current_url = url;
    }

    boolean updateProxy(String url){
        if (!helperMethod.getHost(url).contains("boogle.store")) {
            if(!status.sIsTorInitialized){
                event.invokeObserver(Collections.singletonList(current_url), enums.etype.proxy_error);
                return false;
            }
            else{
                event.invokeObserver(Arrays.asList(true,false), enums.etype.start_proxy);
            }
        }
        else {
            if(url.startsWith("https://boogle.store/advert__"))
            {
                String uri = url;
                helperMethod.openPlayStore(uri.split("__")[1],context);
                return false;
            }
            event.invokeObserver(Arrays.asList(false,true), enums.etype.start_proxy);
        }
        return true;
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {
            if(geckoView.getSession().equals(var1))
            {
                current_url = var2;
                if(!onGoBack){
                    url_list.add(current_url);
                    onGoBack = false;
                }
            }
        }

        public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {
            if(geckoView.getSession().equals(var2))
            {
                if(var1.target==2){
                    event.invokeObserver(Collections.singletonList(var1.uri), enums.etype.open_new_tab);
                    return GeckoResult.fromValue(AllowOrDeny.DENY);
                }

                boolean status = updateProxy(var1.uri);
                requested_url = var1.uri;

                if (!status) {
                    return GeckoResult.fromValue(AllowOrDeny.DENY);
                }

                return GeckoResult.fromValue(AllowOrDeny.ALLOW);
            }
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }

        @Override
        public void onCanGoBack(@NonNull GeckoSession session, boolean canGoBack)
        {
            geckoClients.this.canGoBack = canGoBack;
        }

        public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, @NonNull WebRequestError var3) {
            if(geckoView.getSession().equals(var1))
            {
                if(requested_url==null){
                    requested_url = strings.EMPTY_STR;
                }

                event.invokeObserver(Collections.singletonList(requested_url), enums.etype.on_load_error);
                on_page_error = true;
            }
            event.invokeObserver(Collections.singletonList(requested_url), enums.etype.on_request_completed);
            return null;
        }
    }

    private GeckoResult<GeckoSession.PromptDelegate.PromptResponse> mFileResponse;
    private GeckoSession.PromptDelegate.FilePrompt mFilePrompt;

    private class ContentDelegate implements GeckoSession.ContentDelegate {

        @Override
        public void onExternalResponse(GeckoSession session, GeckoSession.WebResponseInfo response) {
            if(geckoView.getSession().equals(session))
            {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndTypeAndNormalize(Uri.parse(response.uri), response.contentType);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    downloadFile(response);
                }
            }
        }

        @UiThread
        public void onTitleChange(@NonNull GeckoSession var1, @Nullable String var2) {
            if(geckoView.getSession().equals(var1))
            {
                current_title = var2;
            }else {
                Log.i("WHY3 :",var2);
            }
        }


        @Override
        public void onFullScreen(@NonNull GeckoSession var1, boolean var2) {
            if(geckoView.getSession().equals(var1))
            {
                Log.i("Fuck off","");
                event.invokeObserver(Collections.singletonList(var2), enums.etype.on_full_screen);
                maximize_status = var2;
            }
        }

        public void onContextMenu(@NonNull GeckoSession var1, int var2, int var3, @NonNull GeckoSession.ContentDelegate.ContextElement var4) {
            if(var4.type==1){
                event.invokeObserver(Collections.singletonList(var4.srcUri), enums.etype.on_long_press);
            }
            else if(var4.type==0){
                event.invokeObserver(Collections.singletonList(var4.linkUri), enums.etype.on_long_press_url);
            }
        }

    }

    private void downloadFile(GeckoSession.WebResponseInfo response) {
        session1
                .getUserAgent()
                .accept(userAgent -> downloadFile(response, userAgent),
                        exception -> {
                            throw new IllegalStateException("Could not get UserAgent string.");
                        });
    }

    private void downloadFile(GeckoSession.WebResponseInfo response, String userAgent) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mPendingDownloads.add(response);
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);
            return;
        }


        downloadURL = Uri.parse(response.uri);
        downloadFile = response.filename != null ? response.filename : downloadURL.getLastPathSegment();

        session1.stop();
        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);

        event.invokeObserver(Arrays.asList(downloadFile,downloadURL), enums.etype.download_file_popup);
    }

    void manual_download(String url){
        downloadURL = Uri.parse(url);

        File f = new File(url);

        downloadFile = f.getName() != null ? f.getName() : downloadURL.getLastPathSegment();

        session1.stop();
        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);

        event.invokeObserver(Arrays.asList(downloadFile,downloadURL), enums.etype.download_file_popup);
        downloadFile();
    }

    void downloadFile()
    {
        event.invokeObserver(Collections.singletonList(0), enums.etype.progress_update);
        context.startService(downloadFileService.getDownloadService(context, downloadURL+"__"+downloadFile, Environment.DIRECTORY_DOWNLOADS));
    }

    void onFileCallbackResult(final int resultCode, final Intent data) {
        prompt.onFileCallbackResult(resultCode,data);
    }


}
