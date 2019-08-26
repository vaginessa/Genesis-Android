package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.darkweb.genesissearchengine.*;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.preferenceController;
import com.darkweb.genesissearchengine.pluginManager.*;

import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.mozilla.geckoview.GeckoView;


public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*View Webviews*/
    private WebView webView;
    private GeckoView geckoView = null;
    private FrameLayout webviewContainer;

    /*View Objects*/
    private ProgressBar progressBar;
    private ConstraintLayout requestFailure;
    private ConstraintLayout splashScreen;
    private AutoCompleteTextView searchbar;
    private FloatingActionButton floatingButton;
    private ImageView loadingIcon;
    private ImageView splashlogo;
    private TextView loadingText;
    private AdView banner_ads = null;

    /*Redirection Objects*/
    private geckoClients geckoclient = null;
    private webviewClient webviewclient = null;
    private home_ehandler eventhandler = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(helperMethod.isBuildValid())
        {
            setContentView(R.layout.home_view);
            initializeAppModel();
            status.initStatus();
            initializeConnections();
            initializeWebView();
            initializeLocalEventHandlers();
            initExitService();
            viewController.getInstance().initialization(webView,webviewContainer,loadingText,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,splashlogo,banner_ads  );
            geckoclient.initialize(geckoView);
            homeModel.getInstance().initialization();
            if(!status.gateway)
            {
                initBoogle();
            }
            else if(!status.search_status.equals(enums.searchEngine.HiddenWeb.toString()))
            {
                viewController.getInstance().disableSplashScreen();
            }
        }
        else
        {
            initializeAppModel();
            setContentView(R.layout.invalid_setup_view);
            pluginController.getInstance().MessageManagerHandler(Build.SUPPORTED_ABIS[0],enums.popup_type.abi_error);
        }

    }

    public void initExitService()
    {
        startService(new Intent(getBaseContext(), exitManager.class));
    }

    public void lowMemoryError()
    {
        viewController.getInstance().lowMemoryError();
    }

    @Override
    public void onTrimMemory(int level)
    {
        Log.i("CURRENT_LEVEL:" , level+"");
        if(isAppPaused && (level==80 || level==15))
        {
           preferenceController.getInstance().setBool(keys.low_memory,true);
           finish();
        }
    }

    boolean isAppPaused = false;
    @Override
    public void onResume()
    {
        lowMemoryError();
        isAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        isAppPaused = true;
        super.onPause();
    }

    public void initBoogle()
    {
        onloadURL(constants.backendGenesis,false,false,false);
    }

    public Boolean initSearchEngine()
    {
        if(status.search_status.equals(enums.searchEngine.Bing.toString()))
        {
            webView.stopLoading();
            onloadURL(constants.backendBing,true,false,true);
            if(homeModel.getInstance().getNavigation().size()!=1)
            {
                homeModel.getInstance().addNavigation(constants.backendBing,enums.navigationType.onion);
            }
            if(homeModel.getInstance().getNavigation().size()>0)
            {
                homeModel.getInstance().getNavigation().set(0,new navigation_model(constants.backendBing,enums.navigationType.onion));
            }
            return false;
        }
        else if(status.search_status.equals(enums.searchEngine.Google.toString()))
        {
            webView.stopLoading();
            onloadURL(constants.backendGoogle,true,false,true);
            if(homeModel.getInstance().getNavigation().size()!=1)
            {
                homeModel.getInstance().addNavigation(constants.backendGoogle,enums.navigationType.onion);
            }
            if(homeModel.getInstance().getNavigation().size()>0)
            {
                homeModel.getInstance().getNavigation().set(0,new navigation_model(constants.backendGoogle,enums.navigationType.onion));
            }
            return false;
        }
        else
        {
            onloadURL(constants.backendGenesis,false,false,true);
            if(homeModel.getInstance().getNavigation().size()!=1)
            {
                homeModel.getInstance().addNavigation(constants.backendGenesis,enums.navigationType.base);
            }
            if(homeModel.getInstance().getNavigation().size()>0)
            {
                homeModel.getInstance().getNavigation().set(0,new navigation_model(constants.backendGenesis,enums.navigationType.base));
            }
            return true;
        }
    }

    public void initializeAppModel()
    {
        homeModel.getInstance().setAppContext(this);
        homeModel.getInstance().setAppInstance(this);
    }

    public void initializeConnections()
    {
        webView = findViewById(R.id.pageLoader1);
        geckoView = findViewById(R.id.webLoader);

        progressBar = findViewById(R.id.progressBar);
        requestFailure = findViewById(R.id.requestFailure);
        splashScreen = findViewById(R.id.splashScreen);
        searchbar = findViewById(R.id.search);
        floatingButton = findViewById(R.id.floatingActionButton3);
        loadingIcon = findViewById(R.id.imageView_loading_back);
        splashlogo = findViewById(R.id.backsplash);
        loadingText = findViewById(R.id.loadingText);
        webviewContainer = findViewById(R.id.webviewContainer);
        banner_ads = findViewById(R.id.adView);

        webviewclient = new webviewClient();
        geckoclient = new geckoClients();
        eventhandler = new home_ehandler();
    }

    public void initializeWebView()
    {
        setWebViewSettings(webView);
        webviewclient.loadWebViewClient(webView);
    }

    public void setWebViewSettings(WebView view)
    {
        view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        view.getSettings().setJavaScriptEnabled(status.java_status);
        view.getSettings().setUseWideViewPort(true);
    }

    /*------------------------------------------------------- Event Handler ----------------------------------------------------*/


    private void initializeLocalEventHandlers() {
        searchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            return eventhandler.onEditorClicked(v,actionId,event);
        });
    }

    public void onReloadButtonPressed(View view)
    {
       eventhandler.onReloadButtonPressed(view);
    }

    @Override
    public void onBackPressed()
    {
        eventhandler.onBackPressed();
    }

    public void enableGateway(View view)
    {
        eventhandler.switchGateway();
    }

    public void onFloatingButtonPressed(View view)
    {
        eventhandler.onFloatingButtonPressed();
    }

    public void onHomeButtonPressed(View view)
    {
        eventhandler.onHomeButtonPressed();
    }

    public void onMenuButtonPressed(View view)
    {
        eventhandler.onMenuButtonPressed(view);
    }

    public void onSwitchSearch(View view)
    {
        eventhandler.switchSearchEngine(view);
    }

    /*-------------------------------------------------------Helper Method In UI Redirection----------------------------------------------------*/

    public void onloadURL(String url,boolean isHiddenWeb,boolean isUrlSavable,boolean isRepeatAllowed) {
        if(isHiddenWeb)
        {
            geckoclient.saveCache(url);
            geckoclient.loadGeckoURL(url,geckoView,isUrlSavable,webView.getVisibility()==View.VISIBLE || isInternetErrorOpened());
        }
        else if(!homeModel.getInstance().isUrlRepeatable(url,webView.getUrl()) || isRepeatAllowed || webView.getVisibility() == View.GONE)
        {
            webviewclient.saveCache(url);
            webView.loadUrl(url);
            onRequestTriggered(isHiddenWeb,url);
        }
    }

    public void onRequestTriggered(boolean isHiddenWeb,String url) {
        viewController.getInstance().onRequestTriggered(isHiddenWeb,url);
    }

    public void onClearSearchBarCursorView()
    {
        viewController.getInstance().onClearSearchBarCursor();
    }

    public void onUpdateSearchBarView(String url)
    {
        viewController.getInstance().onUpdateSearchBar(url);
    }

    public void onInternetErrorView() {
        viewController.getInstance().onInternetError();
        viewController.getInstance().disableFloatingView();
    }

    public boolean onDisableInternetError()
    {
       return viewController.getInstance().onDisableInternetError();
    }

    public void onProgressBarUpdateView(int progress) {
        viewController.getInstance().onProgressBarUpdate(progress);
    }

    public void onBackPressedView()
    {
        viewController.getInstance().onBackPressed();
    }

    public void onPageFinished(boolean isHidden)
    {
        viewController.getInstance().onPageFinished(isHidden);
    }

    public void onUpdateView(boolean status)
    {
        viewController.getInstance().onUpdateView(status);
    }

    public void onReload()
    {
        viewController.getInstance().onReload();
    }

    public void onShowAd(enums.adID id)
    {

        //adManager.getInstance().showAd(id);
    }

    public void openMenu(View view) {

        viewController.getInstance().openMenu(view);
    }

    public void reInitializeSuggestion()   {
        viewController.getInstance().reInitializeSuggestion();
    }

    public void hideSplashScreen(){
        viewController.getInstance().hideSplashScreen();
    }

    /*-------------------------------------------------------Helper Method Out UI Redirection----------------------------------------------------*/

    public String getSearchBarUrl()
    {
         return viewController.getInstance().getSearchBarUrl();
    }

    public void onReInitGeckoView() {
        geckoclient.initialize(geckoView);
        if(webView.getVisibility() != View.VISIBLE)
        {
            geckoclient.onReloadHiddenView(geckoView,searchbar.getText().toString());
        }
    }

    public void onHiddenGoBack()
    {
        geckoclient.onHiddenGoBack(geckoView);
    }

    public void releaseSession()
    {
        geckoclient.releaseSession(geckoView);
    }

    public void stopHiddenView(boolean releaseView,boolean backPressed) {
        geckoclient.stopHiddenView(geckoView,releaseView,backPressed);
        //geckoclient.removeHistory();
    }

    public void onReloadHiddenView()
    {
        geckoclient.onReloadHiddenView(geckoView,searchbar.getText().toString());
    }

    public boolean isGeckoViewRunning()
    {
       return geckoclient.isGeckoViewRunning();
    }

    public boolean isInternetErrorOpened()
    {
        return requestFailure.getAlpha()==1;
    }

    public void downloadFile()
    {
        geckoclient.downloadFile();
    }

    public void onBannerAdLoaded()
    {
        viewController.getInstance().onBannerAdLoaded();
    }

    /*-------------------------------------------------------Menu Handler----------------------------------------------------*/

    public boolean onMenuOptionSelected(MenuItem item) {

        eventhandler.onMenuPressed(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public AdView getBannerAd()
    {
        return banner_ads;
    }


}

