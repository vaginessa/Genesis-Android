package com.darkweb.genesissearchengine.appManager.home_activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.localNotification;
import com.darkweb.genesissearchengine.pluginManager.messageManager;
import com.darkweb.genesissearchengine.pluginManager.orbotManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;

import org.mozilla.geckoview.*;

import java.util.LinkedList;

import static com.google.ads.AdRequest.LOGTAG;

class geckoClients
{
    public GeckoSession session1 = null;
    private GeckoRuntime runtime1 = null;
    private final Handler internetErrorHandler = new Handler();

    private boolean wasURLSaved = false;
    private boolean isRunning = false;
    private boolean isContentLoading = false;
    private String  navigatedURL = "";
    private boolean isFirstTimeLoad = true;

    private boolean loadingCompeleted = false;
    private boolean wasBackPressed = false;
    private boolean isUrlSavable = true;

    void loadGeckoURL(String url,GeckoView geckoView,boolean isUrlSavable,boolean reinit)
    {
        boolean init_status = pluginController.getInstance().OrbotManagerInit(true);

        if (init_status)
        {
            if(reinit)
            {
                initialize(geckoView);
            }

            wasURLSaved = false;
            this.isUrlSavable = isUrlSavable;
            navigatedURL = "";
            loadingCompeleted = false;
            session1.loadUri(url);
            homeModel.getInstance().getHomeInstance().onRequestTriggered(true,url);
            homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(4);
            isFirstTimeLoad = true;
            wasBackPressed = false;
            isContentLoading = false;
            isRunning = false;
        }
        else
        {
            pluginController.getInstance().MessageManagerHandler(homeModel.getInstance().getHomeInstance(),url,enums.popup_type.start_orbot);
        }
    }

    public void saveCache(String url)
    {
    }

    void initialize(GeckoView geckoView)
    {
        session1 = new GeckoSession();
        runtime1 = GeckoRuntime.getDefault(homeModel.getInstance().getAppContext());
        runtime1.getSettings().setJavaScriptEnabled(status.java_status);
        session1.open(runtime1);
        geckoView.releaseSession();
        geckoView.setSession(session1);
        session1.setProgressDelegate(new progressDelegate());
        session1.setNavigationDelegate(new navigationDelegate());
        geckoView.setVisibility(View.VISIBLE);
        geckoView.setAlpha(1);
        session1.setContentDelegate(new ExampleContentDelegate());
    }

    class navigationDelegate implements GeckoSession.NavigationDelegate
    {
        @Override
        public void onLocationChange(GeckoSession session, String url)
        {
            navigatedURL = url;
            if(isUrlSavable && homeModel.getInstance().getNavigation().size()>0 && !url.equals("about:blank"))
            {
                homeModel.getInstance().addHistory(navigatedURL);
                homeModel.getInstance().addNavigation(navigatedURL,enums.navigationType.onion);
                wasURLSaved = true;
            }
        }

        @Override
        public GeckoResult<GeckoSession> onNewSession(GeckoSession session, String url)
        {
            Log.i("FUCK2",url);
            session1.loadUri(url);
            return null;
        }

    }

    class progressDelegate implements GeckoSession.ProgressDelegate
    {
        @Override
        public void onPageStart(GeckoSession session, String url)
        {
            if(url.contains(".onion"))
            {
                pluginController.getInstance().setProxy(true);
                ///geckoclient.saveCache(url);
                //geckoclient.loadGeckoURL(url,geckoView,isUrlSavable,webView.getVisibility()==View.VISIBLE || isInternetErrorOpened());
            }
            else
            {
                pluginController.getInstance().setProxy(false);
                //webviewclient.saveCache(url,isUrlSavable);
                //webView.loadUrl(url);
                //onRequestTriggered(isHiddenWeb,url);
            }

            wasURLSaved = false;
            wasBackPressed = false;
            isRunning = true;
            loadingCompeleted = false;

            homeModel.getInstance().getHomeInstance().onUpdateSearchBarView(url);
            isContentLoading = !navigatedURL.equals(url);

            navigatedURL = url;
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success)
        {
            internetErrorHandler.removeCallbacksAndMessages(null);

            internetErrorHandler.postDelayed(() ->
            {
                if(loadingCompeleted)
                {
                    if(isFirstTimeLoad)
                    {
                        homeModel.getInstance().getHomeInstance().hideSplashScreen();
                    }
                    if(!success && !isContentLoading && !wasBackPressed)
                    {
                        homeModel.getInstance().getHomeInstance().onPageFinished(true);
                        homeModel.getInstance().getHomeInstance().onInternetErrorView();

                        if(!wasURLSaved && homeModel.getInstance().getNavigation().size()>0 && !homeModel.getInstance().getNavigation().get(homeModel.getInstance().getNavigation().size()-1).getURL().equals(url))
                        {
                            homeModel.getInstance().addHistory(navigatedURL);
                            homeModel.getInstance().addNavigation(navigatedURL,enums.navigationType.onion);
                        }
                    }
                    else if(success)
                    {
                        if(helperMethod.getHost(navigatedURL).contains(".onion"))
                        {
                            homeModel.getInstance().getHomeInstance().onShowAd(enums.adID.hidden_onion_start);
                        }

                        homeModel.getInstance().getHomeInstance().onDisableInternetError();
                        homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                        homeModel.getInstance().getHomeInstance().onPageFinished(true);
                    }

                    isUrlSavable = true;
                    isFirstTimeLoad = false;

                }
            }, 500);
        }



        @Override
        public void onProgressChange(GeckoSession session, int progress)
        {
            if(progress>=100)
            {
                loadingCompeleted = true;
                isContentLoading = false;
            }
            else if(progress>=5)
            {
                homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(progress);
            }
            else
            {
                homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(4);
            }
        }

        @Override
        public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo)
        {
        }

    }

    void onHiddenGoBack(GeckoView geckoView)
    {
        isRunning = false;
        loadingCompeleted = false;
        isUrlSavable = false;

        wasBackPressed = true;
        session1.stop();

        if(homeModel.getInstance().getHomeInstance().isInternetErrorOpened())
        {
            initialize(geckoView);
        }

        session1.loadUri(homeModel.getInstance().getNavigation().get(homeModel.getInstance().getNavigation().size()-1).getURL());
    }

    void stopHiddenView(GeckoView geckoView,boolean releaseView,boolean backPressed)
    {
        if(session1!=null)
        {
            isRunning = false;
            loadingCompeleted = false;
            wasBackPressed = backPressed;

            session1.stop();
            if(!releaseView)
            {
                //session1.close();
            }
        }
    }

    public void releaseSession(GeckoView geckoView)
    {
        geckoView.releaseSession();
    }

    void setRootEngine(String url)
    {
    }

    boolean isGeckoViewRunning()
    {
        return isRunning;
    }

    void onReloadHiddenView(GeckoView geckoView,String url)
    {
        if(!helperMethod.getHost(url).contains("genesis"))
        {
            isRunning = false;
            loadingCompeleted = false;
            isUrlSavable = false;

            wasBackPressed = true;
            session1.stop();
            session1.close();
            geckoView.releaseSession();

            navigatedURL = "";
            loadingCompeleted = false;
            wasBackPressed = false;
            isContentLoading = false;
            isRunning = false;


            initialize(geckoView);
            session1.loadUri(url);
        }
    }
































    private class ExampleContentDelegate implements GeckoSession.ContentDelegate {


        @Override
        public void onFocusRequest(final GeckoSession session) {
            Log.i(LOGTAG, "Content requesting focus");
        }

        @Override
        public void onContextMenu(final GeckoSession session,
                                  int screenX, int screenY,
                                  final ContextElement element) {
            Log.d(LOGTAG, "onContextMenu screenX=" + screenX +
                    " screenY=" + screenY +
                    " type=" + element.type +
                    " linkUri=" + element.linkUri +
                    " title=" + element.title +
                    " alt=" + element.altText +
                    " srcUri=" + element.srcUri);
        }

        @Override
        public void onExternalResponse(GeckoSession session, GeckoSession.WebResponseInfo response) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndTypeAndNormalize(Uri.parse(response.uri), response.contentType);
                homeModel.getInstance().getHomeInstance().startActivity(intent);
            } catch (ActivityNotFoundException e) {

                wasBackPressed = true;
                homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(0);
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

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private LinkedList<GeckoSession.WebResponseInfo> mPendingDownloads = new LinkedList<>();
    private void downloadFile(GeckoSession.WebResponseInfo response, String userAgent) {
        if (ContextCompat.checkSelfPermission(homeModel.getInstance().getHomeInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mPendingDownloads.add(response);
            ActivityCompat.requestPermissions(homeModel.getInstance().getHomeInstance(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }


        url = Uri.parse(response.uri);
        downloadFile = response.filename != null ? response.filename : url.getLastPathSegment();

        pluginController.getInstance().MessageManagerHandler(homeModel.getInstance().getHomeInstance(),downloadFile,enums.popup_type.download_file);
    }

    String downloadFile = "";
    Uri url;

    void downloadFile()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pluginController.getInstance().createNotification("Downloading | " + downloadFile,"Starting Download");
        }
        homeModel.getInstance().getHomeInstance().startService(DownloadFileService.getDownloadService(homeModel.getInstance().getHomeInstance(), url.toString()+"__"+downloadFile, Environment.DIRECTORY_DOWNLOADS));
    }

    private class ExampleContentBlockingDelegate
            implements ContentBlocking.Delegate {
        private int mBlockedAds = 0;
        private int mBlockedAnalytics = 0;
        private int mBlockedSocial = 0;
        private int mBlockedContent = 0;
        private int mBlockedTest = 0;

        private void clearCounters() {
            mBlockedAds = 0;
            mBlockedAnalytics = 0;
            mBlockedSocial = 0;
            mBlockedContent = 0;
            mBlockedTest = 0;
        }

        private void logCounters() {
            Log.d(LOGTAG, "Trackers blocked: " + mBlockedAds + " ads, " +
                    mBlockedAnalytics + " analytics, " +
                    mBlockedSocial + " social, " +
                    mBlockedContent + " content, " +
                    mBlockedTest + " test");
        }

        @Override
        public void onContentBlocked(final GeckoSession session,
                                     final ContentBlocking.BlockEvent event) {
            Log.d(LOGTAG, "onContentBlocked" +
                    " AT: " + event.getAntiTrackingCategory() +
                    " SB: " + event.getSafeBrowsingCategory() +
                    " CB: " + event.getCookieBehaviorCategory() +
                    " URI: " + event.uri);
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.TEST) != 0) {
                mBlockedTest++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.AD) != 0) {
                mBlockedAds++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.ANALYTIC) != 0) {
                mBlockedAnalytics++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.SOCIAL) != 0) {
                mBlockedSocial++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.CONTENT) != 0) {
                mBlockedContent++;
            }
        }

        @Override
        public void onContentLoaded(final GeckoSession session,
                                    final ContentBlocking.BlockEvent event) {
            Log.d(LOGTAG, "onContentLoaded" +
                    " AT: " + event.getAntiTrackingCategory() +
                    " SB: " + event.getSafeBrowsingCategory() +
                    " CB: " + event.getCookieBehaviorCategory() +
                    " URI: " + event.uri);
        }
    }


    private final class ExampleTelemetryDelegate
            implements RuntimeTelemetry.Delegate {
        @Override
        public void onTelemetryReceived(final @NonNull RuntimeTelemetry.Metric metric) {
            Log.d(LOGTAG, "onTelemetryReceived " + metric);
        }
    }




}
