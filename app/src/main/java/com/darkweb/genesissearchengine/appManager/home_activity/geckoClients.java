package com.darkweb.genesissearchengine.appManager.home_activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;

import org.mozilla.geckoview.*;

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
    private String current_url = strings.emptyStr;
    private String requested_url = strings.emptyStr;

    private Uri downloadURL;
    private String downloadFile = "";
    private BasicGeckoViewPrompt prompt;


    void initialize(GeckoView geckoView, AppCompatActivity Context,eventObserver.eventListener event,String searchEngine,AppCompatActivity context)
    {
        this.context = context;
        this.event = event;
        prompt = new BasicGeckoViewPrompt(context);
        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(Context);
        runtime1.getSettings().setJavaScriptEnabled(status.java_status);
        session1.open(runtime1);
        geckoView.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        session1.setNavigationDelegate(new navigationDelegate());
        session1.setHistoryDelegate(new historyDelegate());
        session1.setContentDelegate(new ContentDelegate());
        session1.setPromptDelegate(prompt);
    }

    void loadURL(String url){
        session1.loadUri(url);
    }

    boolean isSessionRunning(){
        return !on_page_loaded;
    }

    String currentURLState()
    {
        return requested_url;
    }

    void onBackPressed(){
        if(canGoBack){
            if(url_list.size()>0){
                url_list.remove(url_list.size()-1);
                updateProxy(url_list.get(url_list.size()-1));
            }
            session1.goBack();
            onGoBack = true;
        }
        else {
            event.invokeObserver(null, enums.home_eventType.back_list_empty);
        }
    }

    void onUpdateJavascript(){
        runtime1.getSettings().setJavaScriptEnabled(status.java_status);
        session1.reload();
    }

    /*Delegate Handler*/

    class progressDelegate implements GeckoSession.ProgressDelegate
    {
        @Override
        public void onPageStart(GeckoSession session, String url)
        {
            if(onGoBack){
                event.invokeObserver(Collections.singletonList(url), enums.home_eventType.on_url_load);
            }

            on_page_loaded = false;
            onGoBack = false;
            on_page_error = false;

            event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success)
        {
            if(on_page_loaded){
                if(!on_page_error){
                    event.invokeObserver(null, enums.home_eventType.on_page_loaded);
                    event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);
                }
                if(success){
                    event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);
                    event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);
                }
            }
        }

        @Override
        public void onProgressChange(GeckoSession session, int progress)
        {
            if(progress>10){
                event.invokeObserver(Collections.singletonList(progress), enums.home_eventType.progress_update);
                on_page_loaded = true;
            }
            else {
                event.invokeObserver(Collections.singletonList(10), enums.home_eventType.progress_update);
            }
        }
    }

    class historyDelegate implements GeckoSession.HistoryDelegate
    {
        public GeckoResult<Boolean> onVisited(@NonNull GeckoSession var1, @NonNull String var2, @Nullable String var3, int var4) {
            if(var4==3 || var4==5 || var4==1){
                event.invokeObserver(Collections.singletonList(var2), enums.home_eventType.on_request_completed);
                event.invokeObserver(Collections.singletonList(var2), enums.home_eventType.on_url_load);
                url_list.add(current_url);
            }
            return null;
        }

        public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull GeckoSession.HistoryDelegate.HistoryList var2) {

        }
    }

    String getRequestedURL(){
        return requested_url;
    }

    String getCurrentURL(){
        return current_url;
    }

    void setCurrentURL(String url){
    }

    private boolean updateProxy(String url){
        if (!helperMethod.getHost(url).contains("boogle.store")) {
            if(!status.isTorInitialized){
                event.invokeObserver(Collections.singletonList(current_url), enums.home_eventType.proxy_error);
                return false;
            }
            else {
                event.invokeObserver(Collections.singletonList(true), enums.home_eventType.start_proxy);
            }
        }
        else {
            if(url.startsWith("https://boogle.store/advert__"))
            {
                String uri = url;
                helperMethod.openPlayStore(uri.split("__")[1],context);
                return false;
            }
            event.invokeObserver(Collections.singletonList(false), enums.home_eventType.start_proxy);
        }
        return true;
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        public void onLocationChange(@NonNull GeckoSession var1, @Nullable String var2) {
            current_url = var2;
        }

        public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession var2, @NonNull GeckoSession.NavigationDelegate.LoadRequest var1) {
            if(var1.target==2){
                loadURL(var1.uri);
                return GeckoResult.fromValue(AllowOrDeny.DENY);
            }

            boolean status = updateProxy(var1.uri);
            requested_url = var1.uri;

            if (!status) {
                return GeckoResult.fromValue(AllowOrDeny.DENY);
            }

            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }

        @Override
        public void onCanGoBack(@NonNull GeckoSession session, boolean canGoBack)
        {
            geckoClients.this.canGoBack = canGoBack;
        }

        public GeckoResult<String> onLoadError(@NonNull GeckoSession var1, @Nullable String var2, @NonNull WebRequestError var3) {

            event.invokeObserver(Collections.singletonList(var2), enums.home_eventType.on_load_error);
            on_page_error = true;
            return null;
        }
    }

    private GeckoResult<GeckoSession.PromptDelegate.PromptResponse> mFileResponse;
    private GeckoSession.PromptDelegate.FilePrompt mFilePrompt;

    private class ContentDelegate implements GeckoSession.ContentDelegate {

        @Override
        public void onExternalResponse(GeckoSession session, GeckoSession.WebResponseInfo response) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndTypeAndNormalize(Uri.parse(response.uri), response.contentType);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                downloadFile(response);
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
        event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);

        event.invokeObserver(Arrays.asList(downloadFile,downloadURL), enums.home_eventType.download_file_popup);
    }

    void downloadFile()
    {
        showProgressNotification(1123,"Download In Progress","Downloading | " + downloadFile);
        event.invokeObserver(Collections.singletonList(0), enums.home_eventType.progress_update);
        context.startService(downloadFileService.getDownloadService(context, downloadURL+"__"+downloadFile, Environment.DIRECTORY_DOWNLOADS));
    }

    private static void showProgressNotification(int notificationId, String title, String message)
    {
        //localNotification notification = new localNotification(context,null);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    void onFileCallbackResult(final int resultCode, final Intent data) {
        prompt.onFileCallbackResult(resultCode,data);
    }


}
