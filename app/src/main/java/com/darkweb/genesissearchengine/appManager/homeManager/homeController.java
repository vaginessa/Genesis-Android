package com.darkweb.genesissearchengine.appManager.homeManager;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.appManager.orbotManager.orbotController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.List;
import java.util.concurrent.Callable;


public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController mHomeViewController;
    private homeModel mHomeModel;
    private geckoClients mGeckoClient = null;

    /*View Webviews*/
    private GeckoView mGeckoView = null;
    private FrameLayout mWebViewContainer;

    /*View Objects*/
    private ProgressBar mProgressBar;
    private ConstraintLayout mRequestFailure;
    private ConstraintLayout mSplashScreen;
    private AutoCompleteTextView mSearchbar;
    private FloatingActionButton mFloatingButton;
    private ImageView mLoadingIcon;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private ImageView mEngineLogo;
    private ImageButton mGatewaySplash;
    private ImageButton mSwitchEngineBack;
    private LinearLayout mTopBar;
    private ImageView mBackSplash;
    private Button mConnectButton;

    /*Redirection Objects*/
    private boolean mPageClosed = false;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            finish();
        }
        if(helperMethod.isBuildValid())
        {
            setContentView(R.layout.home_view);

            databaseController.getInstance().initialize(this);
            dataController.getInstance().initialize(this);
            activityContextManager.getInstance().setHomeController(this);
            pluginController.getInstance().initializeAllProxies(this);

            status.initStatus();
            dataController.getInstance().initializeListData();

            initializePermission();
            initializeAppModel();
            initializeConnections();
            pluginController.getInstance().initialize();
            initializeGeckoView();
            initializeLocalEventHandlers();
        }
        else
        {
            initializeAppModel();
            setContentView(R.layout.invalid_setup_view);
        }


    }

    public void initializeAppModel()
    {
        mHomeViewController = new homeViewController();
        mHomeModel = new homeModel();
    }

    public void initializeConnections()
    {
        mGeckoView = findViewById(R.id.webLoader);

        mProgressBar = findViewById(R.id.progressBar);
        mRequestFailure = findViewById(R.id.requestFailure);
        mSplashScreen = findViewById(R.id.splashScreen);
        mSearchbar = findViewById(R.id.search);
        mFloatingButton = findViewById(R.id.floatingActionButton3);
        mLoadingIcon = findViewById(R.id.imageView_loading_back);
        mLoadingText = findViewById(R.id.loadingText);
        mWebViewContainer = findViewById(R.id.webviewContainer);
        mBannerAds = findViewById(R.id.adView);
        mEngineLogo = findViewById(R.id.switchEngine);
        mGatewaySplash = findViewById(R.id.gateway_splash);
        mTopBar = findViewById(R.id.topbar);
        mBackSplash = findViewById(R.id.backsplash);
        mConnectButton = findViewById(R.id.Connect);
        mSwitchEngineBack = findViewById(R.id.switchEngineBack);


        mGeckoClient = new geckoClients();
        boolean is_engine_switched = dataController.getInstance().getBool(keys.ENGINE_SWITCHED,false);

        mHomeViewController.initialization(new homeViewCallback(),this, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mRequestFailure, mFloatingButton, mLoadingIcon, mBannerAds,dataController.getInstance().getSuggestions(), mEngineLogo, mGatewaySplash, mTopBar, mGeckoView, mBackSplash,is_engine_switched, mConnectButton, mSwitchEngineBack);
    }

    public void initializePermission(){
        helperMethod.checkPermissions(this);
    }

    public void initializeGeckoView(){

        mGeckoClient.initialize(mGeckoView,this,new geckoViewCallback(),status.sSearchStatus,this);
        mGeckoClient.setTitle(strings.EMPTY_STR);
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    public void onLoadFont(){
        mGeckoClient.onUpdateFont();
        mHomeViewController.onReDraw();
    }

    public void onLoadProxy(View view){
        if(pluginController.getInstance().isInitialized() && !mPageClosed){
            helperMethod.openActivity(orbotController.class, constants.LIST_HISTORY, homeController.this,true);
        }
    }

    public void onloadSettings(){
        mGeckoClient.onUpdateSettings();
    }

    public void onLoadURL(String url){
        mGeckoClient.setCurrentURL(url);
        mHomeViewController.onClearSelections(true);
        mGeckoClient.loadURL(url);
    }

    public void onLoadTab(GeckoSession sessionLoaded, String title, String url,boolean saveCurrentTab,int progress){

        if(saveCurrentTab){
            onSaveCurrentTab(mGeckoView.getSession());
        }

        mGeckoClient.setCurrentURL(url);
        mGeckoClient.setTitle(title);

        mHomeViewController.onProgressBarUpdate(progress,true );
        mHomeViewController.onInternetErrorUpdate(false);
        mHomeViewController.onUpdateSearchBar(url);
        mGeckoView.releaseSession();
        mGeckoView.setSession(sessionLoaded);
        mGeckoClient.loadSession(sessionLoaded);
        mHomeViewController.onProgressBarUpdate(0,false);
    }

    /*-------------------------------------------------------USER EVENTS-------------------------------------------------------*/

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        mSearchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
            {
                helperMethod.hideKeyboard(homeController.this);

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    pluginController.getInstance().logEvent(strings.SEARCH_INVOKED);
                    onSearchBarInvoked(v);
                    mGeckoView.clearFocus();
                }, 500);
            }
            return true;
        });

        mGatewaySplash.setOnTouchListener((v, event) ->
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                mGatewaySplash.setElevation(9);
            else if (event.getAction() == MotionEvent.ACTION_UP)
                mGatewaySplash.setElevation(2);
                return false;
        });

        mSearchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                status.sIsAppStarted = true;
                pluginController.getInstance().onResetMessage();
            }else {
                mHomeViewController.updateSearchPosition();
            }
        });

        mGeckoView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                status.sIsAppStarted = true;
                pluginController.getInstance().onResetMessage();
            }
        });

        pluginController.getInstance().logEvent(strings.APP_STARTED);
    }

    void onSearchBarInvoked(View view){
        String url = ((EditText)view).getText().toString();
        url = mHomeModel.urlComplete(url);
        mHomeViewController.onUpdateSearchBar(url);
        onLoadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String val = ((TextView)view).getText().toString();
        val = mHomeModel.urlComplete(val);
        mHomeViewController.onUpdateSearchBar(val);
        mSearchbar.clearFocus();
    }

    public void onHomeButton(View view){
        pluginController.getInstance().logEvent(strings.HOME_INVOKED);
        onLoadURL(mHomeModel.getSearchEngine());
    }

    public void onOpenMenuItem(View view){
        pluginController.getInstance().logEvent(strings.MENU_INVOKED);
        status.sIsAppStarted = true;
        mHomeViewController.onClearSelections(false);
        mHomeViewController.onOpenMenu(view);
        pluginController.getInstance().onResetMessage();
    }

    @Override
    public void onBackPressed(){
        pluginController.getInstance().logEvent(strings.ON_BACK);
        mGeckoView.clearFocus();
        if(mRequestFailure.getVisibility()==View.VISIBLE){
            mHomeViewController.onInternetErrorUpdate(false);
            mHomeViewController.onUpdateSearchBar(mGeckoClient.getCurrentURL());
            mGeckoClient.updateProxy(mGeckoClient.getCurrentURL());
        }
        else {
            if(!mGeckoClient.getFullScreenStatus()){
                mGeckoClient.onBackPressed();
                mHomeViewController.onClearSelections(true);
            }
            else {
                mGeckoClient.exitFullScreen();
            }
        }
    }

    public void onSwitchSearch(View view){
        mHomeViewController.stopSearchButtonAnimation();
        dataController.getInstance().setBool(keys.ENGINE_SWITCHED,true);
        pluginController.getInstance().logEvent(strings.SEARCH_SWITCH);

        if(status.sSearchStatus.equals(constants.BACKEND_GOOGLE_URL))
        {
            status.sSearchStatus = constants.BACKEND_GENESIS_URL;
            mHomeViewController.onUpdateLogo();
            mGeckoClient.setRequestedUrl(constants.BACKEND_GENESIS_URL);
            dataController.getInstance().setString(keys.SEARCH_ENGINE,constants.BACKEND_GENESIS_URL);
            onHomeButton(null);
        }
        else if(status.sSearchStatus.equals(constants.BACKEND_GENESIS_URL))
        {
            status.sSearchStatus = constants.BACKEND_DUCK_DUCK_GO_URL;
            if(pluginController.getInstance().isOrbotRunning())
            {
                mHomeViewController.onUpdateLogo();
                dataController.getInstance().setString(keys.SEARCH_ENGINE,constants.BACKEND_DUCK_DUCK_GO_URL);
                mGeckoClient.setRequestedUrl(constants.BACKEND_DUCK_DUCK_GO_URL);
                onHomeButton(null);
            }
            else {
                mGeckoClient.setRequestedUrl(constants.BACKEND_DUCK_DUCK_GO_URL);
                pluginController.getInstance().MessageManagerHandler(homeController.this,constants.BACKEND_DUCK_DUCK_GO_URL,enums.etype.start_orbot);
            }
        }
        else
        {
            status.sSearchStatus = constants.BACKEND_GOOGLE_URL;
            if(pluginController.getInstance().isOrbotRunning())
            {
                mHomeViewController.onUpdateLogo();
                dataController.getInstance().setString(keys.SEARCH_ENGINE,constants.BACKEND_GOOGLE_URL);
                mGeckoClient.setRequestedUrl(constants.BACKEND_GOOGLE_URL);
                onHomeButton(null);
            }
            else {
                pluginController.getInstance().MessageManagerHandler(homeController.this,constants.BACKEND_GOOGLE_URL,enums.etype.start_orbot);
                mGeckoClient.setRequestedUrl(constants.BACKEND_GOOGLE_URL);
            }
        }
    }

    /*Activity States*/

    public void onReload(View view){
        mGeckoClient.loadURL(mGeckoClient.getRequestedURL());
        mHomeViewController.onUpdateLogo();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginController.getInstance().onResetMessage();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            mGeckoClient.onFileCallbackResult(resultCode,data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        helperMethod.hideKeyboard(this);
        pluginController.getInstance().onResetMessage();
    }


    /*-------------------------------------------------------External Callback Methods-------------------------------------------------------*/

    public void onSuggestionUpdate(){
        mHomeViewController.initializeSuggestionView(dataController.getInstance().getSuggestions());
    }

    public void onStartApplication(View view){
        pluginController.getInstance().initializeOrbot();
        onInvokeProxyLoading();
        mHomeViewController.initHomePage();
    }

    public void onDownloadFile(){
        mGeckoClient.downloadFile();
    }

    public void onManualDownload(String url){
        mGeckoClient.manual_download(url);
    }

    public AdView getBannerAd()
    {
        return mBannerAds;
    }

    public void onInvokeProxyLoading(){

         Callable<String> callable = () -> {
            mHomeViewController.onUpdateLogs(pluginController.getInstance().orbotLogs());
            return strings.EMPTY_STR;
        };

        mHomeViewController.initProxyLoading(callable);
    }

    public void onOpenLinkNewTab(String url){
        onSaveCurrentTab(mGeckoView.getSession());
        initializeGeckoView();
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(false);
        mHomeViewController.onUpdateSearchBar(url);
        mGeckoClient.loadURL(url);
    }

    public void onSaveCurrentTab(GeckoSession session){
        String url = mGeckoClient.getCurrentURL();
        dataController.getInstance().addTab(url, mGeckoClient.getTitle(),session,mProgressBar.getProgress());
    }

    public void onCloseCurrentTab(GeckoSession session){
        dataController.getInstance().closeTab(session);
        tabRowModel model = dataController.getInstance().getCurrentTab();
        if(model!=null){
            onLoadTab(model.getmSession(),model.getmHeader(),model.getmDescription(),false,model.getProgress());
        }
        else {
            initializeGeckoView();
            mHomeViewController.progressBarReset();
            mHomeViewController.onNewTab(true);
        }
        session.stop();
        session.close();
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
                mHomeViewController.onClearSelections(true);
                if(e_type.equals(enums.etype.onMenuSelected)){
                int menuId = (int)data.get(0);
                if (menuId == R.id.menu1) {
                    helperMethod.openActivity(historyController.class, constants.LIST_HISTORY, homeController.this,true);
                }
                else if (menuId == R.id.menu10) {
                    onSaveCurrentTab(mGeckoView.getSession());
                    initializeGeckoView();
                    mHomeViewController.progressBarReset();
                    mHomeViewController.onNewTab(true);
                }
                else if (menuId == R.id.menu12) {
                    onCloseCurrentTab(mGeckoView.getSession());
                }
                else if (menuId == R.id.menu11) {
                    helperMethod.openActivity(tabController.class,constants.LIST_BOOKMARK, homeController.this,true);
                }
                else if (menuId == R.id.menu9) {
                    onLoadURL("https://whatismycountry.com/");
                }
                else if (menuId == R.id.menu3) {
                    helperMethod.openActivity(settingController.class,constants.LIST_HISTORY, homeController.this,true);
                }
                else if (menuId == R.id.menu4)
                {
                    pluginController.getInstance().MessageManagerHandler(homeController.this, mSearchbar.getText().toString(),enums.etype.bookmark);
                }
                else if (menuId == R.id.menu5)
                {
                    helperMethod.openActivity(bookmarkController.class,constants.LIST_BOOKMARK, homeController.this,true);
                }
                else if (menuId == R.id.menu6)
                {
                    pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.etype.report_url);
                }
                else if (menuId == R.id.menu7)
                {
                    helperMethod.rateApp(homeController.this);
                }
                else if (menuId == R.id.menu8)
                {
                    helperMethod.shareApp(homeController.this);
                }
                else if (menuId == R.id.menu0)
                {
                    helperMethod.openDownloadFolder(homeController.this);
                }
           }
           else if(e_type.equals(enums.etype.on_init_ads))
           {
               pluginController.getInstance().initializeBannerAds();
           }
           else if(e_type.equals(enums.etype.on_url_load)){
               mHomeViewController.onUpdateLogs("Starting | Genesis Search");
               onLoadURL(data.get(0).toString());
           }
           else if(e_type.equals(enums.etype.recheck_orbot)){
               pluginController.getInstance().isOrbotRunning();
           }

        }
    }

    public class geckoViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
            if(e_type.equals(enums.etype.progress_update)){
                mHomeViewController.onProgressBarUpdate((int)data.get(0), mGeckoClient.isSessionRunning());
            }
            else if(e_type.equals(enums.etype.on_url_load)){
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.back_list_empty)){
                helperMethod.onMinimizeApp(homeController.this);
            }
            else if(e_type.equals(enums.etype.start_proxy)){
                pluginController.getInstance().setProxy((Boolean)data.get(0),(Boolean)data.get(1));
            }
            else if(e_type.equals(enums.etype.on_request_completed)){
                dataController.getInstance().addHistory(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.on_page_loaded)){
                pluginController.getInstance().logEvent(strings.PAGE_OPENED_SUCCESS);
                dataController.getInstance().setBool(keys.IS_BOOTSTRAPPED,true);
                mHomeViewController.onPageFinished();
                if(status.sIsWelcomeEnabled && !status.sIsAppStarted){
                    final Handler handler = new Handler();
                    helperMethod.hideKeyboard(homeController.this);
                    Runnable runnable = () ->
                    {
                        if(!status.sIsAppStarted){
                            pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), strings.EMPTY_STR, enums.etype.welcome);
                        }
                        status.sIsAppStarted = true;
                    };
                    handler.postDelayed(runnable, 1300);
                }
            }
            else if(e_type.equals(enums.etype.rate_application)){
                dataController.getInstance().setBool(keys.IS_APP_RATED,true);
                pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), strings.EMPTY_STR, enums.etype.rate_app);
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                dataController.getInstance().setBool(keys.IS_BOOTSTRAPPED,true);
                pluginController.getInstance().logEvent(strings.URL_ERROR);
                mHomeViewController.onInternetErrorUpdate(true);
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.proxy_error)){
                helperMethod.hideKeyboard(homeController.this);
                mGeckoView.clearFocus();
                pluginController.getInstance().logEvent(strings.URL_ERROR_NOT_LOADED);
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.etype.start_orbot);
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.etype.download_file);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);
            }
            else if(e_type.equals(enums.etype.on_long_press)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.etype.download_file_long_press);
            }
            else if(e_type.equals(enums.etype.on_long_press_url)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.etype.on_long_press_url);
            }
            else if(e_type.equals(enums.etype.open_new_tab)){
                onOpenLinkNewTab(data.get(0).toString());
            }
        }
    }


}

