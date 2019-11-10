package com.darkweb.genesissearchengine.appManager.homeManager;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
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

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.Arrays;
import java.util.Collections;
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
    private com.darkweb.genesissearchengine.widget.AnimatedProgressBar mProgressBar;
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
    private Button mNewTab;

    /*Redirection Objects*/
    private boolean mPageClosed = false;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/

    private GeckoSession mSession = null;
    private GeckoRuntime mRuntime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        //status.clearFailureHistory(this);
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

    public void onClose(){
        mGeckoClient.onClose();
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
        mNewTab = findViewById(R.id.newButtonInvoke);

        mGeckoClient = new geckoClients();
        boolean is_engine_switched = dataController.getInstance().getBool(keys.ENGINE_SWITCHED,false);

        mHomeViewController.initialization(new homeViewCallback(),this,mNewTab, mWebViewContainer, mLoadingText, mProgressBar, mSearchbar, mSplashScreen, mFloatingButton, mLoadingIcon, mBannerAds,dataController.getInstance().getSuggestions(), mEngineLogo, mGatewaySplash, mTopBar, mGeckoView, mBackSplash,is_engine_switched, mConnectButton, mSwitchEngineBack);
    }

    public void initializePermission(){
        helperMethod.checkPermissions(this);
    }

    public void initializeGeckoView(){
        mGeckoClient.initialize(mGeckoView,this,new geckoViewCallback(),status.sSearchStatus,this);
        onSaveCurrentTab(mGeckoClient.getSession());
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

    public void onUpdateJavascript(){
        mGeckoView.clearFocus();
        mGeckoClient.updateJavascript();
    }

    public void onUpdateCookies(){
        mGeckoView.clearFocus();
        mGeckoClient.updateCookies();
    }

    public void onLoadURL(String url){
        mHomeViewController.onClearSelections(true);
        mGeckoClient.loadURL(url.replace("genesis.onion","boogle.store"));
    }

    public void onLoadTab(geckoSession mTempSession,boolean isSessionClosed){
        if(!isSessionClosed){
            dataController.getInstance().closeTab(mTempSession);
            onSaveCurrentTab(mTempSession);
        }
        mGeckoView.releaseSession();
        mGeckoClient.initSession(mTempSession);
        mHomeViewController.onUpdateSearchBar(mTempSession.getCurrentURL());

        if(mTempSession.getProgress()>0 && mTempSession.getProgress()<100){
            mHomeViewController.onProgressBarUpdate(mTempSession.getProgress());
        }else {
            mHomeViewController.progressBarReset();
        }
        mGeckoView.setSession(mTempSession);
    }

    /*-------------------------------------------------------USER EVENTS-------------------------------------------------------*/

    private BroadcastReceiver downloadStatus = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            DownloadManager dMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Cursor c= dMgr.query(new DownloadManager.Query().setFilterById(id));

            if(c.moveToFirst()){
                String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                onNotificationInvoked(URLUtil.guessFileName(url, null, null), enums.etype.download_folder);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadStatus);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLocalEventHandlers() {

        registerReceiver(downloadStatus,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

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
        String validated_url = mHomeModel.urlComplete(url);
        if(validated_url!=null){
            url = validated_url;
        }
        mHomeViewController.onUpdateSearchBar(url);
        onLoadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String val = ((TextView)view).getText().toString();
        mHomeViewController.onUpdateSearchBar(val);
        mSearchbar.clearFocus();
        onLoadURL(mSearchbar.getText().toString());
    }

    public void onHomeButton(View view){
        pluginController.getInstance().logEvent(strings.HOME_INVOKED);
        onLoadURL(mHomeModel.getSearchEngine());
        mHomeViewController.onUpdateLogo();
    }

    public void onNewTab(){
        initializeGeckoView();
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(true);
    }

    public void onOpenTabView(View view){
        helperMethod.openActivity(tabController.class, constants.LIST_HISTORY, homeController.this,true);
        //onNotificationInvoked("askjdhkjasdhkjashdkjasdhajssuper_man.jpg",enums.etype.download_folder);
    }

    public void onNotificationInvoked(String message,enums.etype e_type){
        mHomeViewController.downloadNotification(message,e_type);
    }

    public void onOpenMenuItem(View view){
        pluginController.getInstance().logEvent(strings.MENU_INVOKED);
        status.sIsAppStarted = true;
        pluginController.getInstance().onResetMessage();
        mHomeViewController.onOpenMenu(view,mGeckoClient.canGoBack(),mGeckoClient.canGoForward(),!(mProgressBar.getAlpha()<=0 || mProgressBar.getVisibility() ==View.INVISIBLE));
        mGeckoView.clearFocus();
        if(mGeckoView.hasFocus()){
            helperMethod.hideKeyboard(this);
        }
    }

    @Override
    public void onBackPressed(){
        pluginController.getInstance().logEvent(strings.ON_BACK);
        mGeckoView.clearFocus();
        if(!mGeckoClient.getFullScreenStatus()){
            mGeckoClient.onBackPressed(true);
            mHomeViewController.onClearSelections(true);
        }
        else {
            mGeckoClient.onExitFullScreen();
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
            //mGeckoClient.setRequestedUrl(constants.BACKEND_GENESIS_URL);
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
                //mGeckoClient.setRequestedUrl(constants.BACKEND_DUCK_DUCK_GO_URL);
                onHomeButton(null);
            }
            else {
                //mGeckoClient.setRequestedUrl(constants.BACKEND_DUCK_DUCK_GO_URL);
                pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(constants.BACKEND_DUCK_DUCK_GO_URL),enums.etype.start_orbot);
            }
        }
        else
        {
            status.sSearchStatus = constants.BACKEND_GOOGLE_URL;
            if(pluginController.getInstance().isOrbotRunning())
            {
                mHomeViewController.onUpdateLogo();
                dataController.getInstance().setString(keys.SEARCH_ENGINE,constants.BACKEND_GOOGLE_URL);
                //mGeckoClient.setRequestedUrl(constants.BACKEND_GOOGLE_URL);
                onHomeButton(null);
            }
            else {
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(constants.BACKEND_GOOGLE_URL),enums.etype.start_orbot);
                //mGeckoClient.setRequestedUrl(constants.BACKEND_GOOGLE_URL);
            }
        }
    }

    /*Activity States*/

    public void onReload(View view){
        mHomeViewController.onUpdateLogo();
    }

    public void onClearSession(){
        mGeckoClient.onClearSession();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        pluginController.getInstance().onResetMessage();
        mHomeViewController.closeMenu();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mHomeViewController.setOrientation(true);
                if(!mGeckoClient.getFullScreenStatus())
                {
                    mHomeViewController.onSetBannerAdMargin(false, true);
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                mHomeViewController.setOrientation(false);
                if(!mGeckoClient.getFullScreenStatus())
                {
                    mHomeViewController.onSetBannerAdMargin(true,pluginController.getInstance().isAdvertLoaded());
                }
            }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            //mGeckoClient.onFileCallbackResult(resultCode,data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        helperMethod.hideKeyboard(this);
        pluginController.getInstance().onResetMessage();
    }

    public void onSetBannerAdMargin(){
        mHomeViewController.onSetBannerAdMargin(true,pluginController.getInstance().isAdvertLoaded());
    }

    /*-------------------------------------------------------External Callback Methods-------------------------------------------------------*/

    public void onSuggestionUpdate(){
        mHomeViewController.initializeSuggestionView(dataController.getInstance().getSuggestions());
    }

    public void onStartApplication(View view){
        pluginController.getInstance().initializeOrbot(this);
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
        initializeGeckoView();
        mHomeViewController.progressBarReset();
        mHomeViewController.onNewTab(false);
        mHomeViewController.onUpdateSearchBar(url);
        mGeckoClient.loadURL(url);
    }

    public void onSaveCurrentTab(geckoSession session){
        dataController.getInstance().addTab(session);
        mHomeViewController.initTab(dataController.getInstance().getTotalTabs());
    }

    public void onCloseCurrentTab(geckoSession session){
        dataController.getInstance().closeTab(session);
        tabRowModel model = dataController.getInstance().getCurrentTab();
        if(model!=null){
            onLoadTab(model.getSession(),true);
        }
        else {
            onNewTab();
        }
        session.stop();
        session.close();
        initTabCount();
    }

    public void initTabCount(){
        mHomeViewController.initTab(dataController.getInstance().getTotalTabs());
    }

    /*-------------------------------------------------------CALLBACKS-------------------------------------------------------*/


    public void onOpenDownloadFolder(View view){
        helperMethod.openDownloadFolder(homeController.this);
    }

    public void onMenuItemInvoked(View view){
        int menuId = view.getId();
        if (menuId == R.id.menu11) {
            onNewTab();
        }
        else if (menuId == R.id.menu10) {
            onCloseCurrentTab(mGeckoClient.getSession());
        }
        else if (menuId == R.id.menu9) {
            helperMethod.openActivity(tabController.class, constants.LIST_HISTORY, homeController.this,true);
        }
        else if (menuId == R.id.menu8) {
            onOpenDownloadFolder(null);
        }
        else if (menuId == R.id.menu7) {
            helperMethod.openActivity(historyController.class, constants.LIST_HISTORY, homeController.this,true);
        }
        else if (menuId == R.id.menu6)
        {
            helperMethod.openActivity(settingController.class,constants.LIST_HISTORY, homeController.this,true);
        }
        else if (menuId == R.id.menu5)
        {
            pluginController.getInstance().MessageManagerHandler(homeController.this, Collections.singletonList(mSearchbar.getText().toString()),enums.etype.bookmark);
        }
        else if (menuId == R.id.menu4)
        {
            helperMethod.openActivity(bookmarkController.class,constants.LIST_BOOKMARK, homeController.this,true);
        }
        else if (menuId == R.id.menu3)
        {
            pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.etype.report_url);
        }
        else if (menuId == R.id.menu2)
        {
            helperMethod.rateApp(homeController.this);
        }
        else if (menuId == R.id.menu1)
        {
            helperMethod.shareApp(homeController.this);
        }
        if (menuId == R.id.menu20) {
            mGeckoClient.onStop();
        }
        if (menuId == R.id.menu21) {
            mGeckoClient.onReload();
        }
        if (menuId == R.id.menu22) {
            mGeckoClient.onForwardPressed();
        }
        if (menuId == R.id.menu23) {
            mGeckoClient.onBackPressed(false);
        }
        if (menuId == R.id.menu24) {
            onHomeButton(view);
        }
        mHomeViewController.closeMenu();
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.etype e_type)
        {
           if(e_type.equals(enums.etype.download_folder))
           {
               onOpenDownloadFolder(null);
           }
           if(e_type.equals(enums.etype.on_init_ads))
           {
               mHomeViewController.onSetBannerAdMargin((boolean)data.get(0),pluginController.getInstance().isAdvertLoaded());
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
                mHomeViewController.onProgressBarUpdate((int)data.get(0));
            }
            else if(e_type.equals(enums.etype.on_url_load)){
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.back_list_empty)){
                if(dataController.getInstance().getTotalTabs()>1){
                    onCloseCurrentTab(mGeckoClient.getSession());
                }else {
                    helperMethod.onMinimizeApp(homeController.this);
                }
            }
            else if(e_type.equals(enums.etype.start_proxy)){
                pluginController.getInstance().setProxy(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.on_request_completed)){
                dataController.getInstance().addHistory(data.get(0).toString());
                helperMethod.hideKeyboard(homeController.this);
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
                            pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.EMPTY_STR), enums.etype.welcome);
                        }
                        status.sIsAppStarted = true;
                    };
                    handler.postDelayed(runnable, 1300);
                }else {
                    pluginController.getInstance().initializeBannerAds();
                }
            }
            else if(e_type.equals(enums.etype.rate_application)){
                dataController.getInstance().setBool(keys.IS_APP_RATED,true);
                pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), Collections.singletonList(strings.EMPTY_STR), enums.etype.rate_app);
            }
            else if(e_type.equals(enums.etype.on_load_error)){
                mHomeViewController.onPageFinished();
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.search_update)){
                mHomeViewController.onUpdateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(data.get(0).toString()),enums.etype.download_file);
            }
            else if(e_type.equals(enums.etype.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                mHomeViewController.onFullScreenUpdate(status);
            }
            else if(e_type.equals(enums.etype.on_long_press_with_link)){
                 pluginController.getInstance().MessageManagerHandler(homeController.this, Arrays.asList(data.get(2).toString(),data.get(0).toString()),enums.etype.on_long_press_with_link);
            }
            else if(e_type.equals(enums.etype.on_long_press)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(data.get(0).toString()),enums.etype.download_file_long_press);
            }
            else if(e_type.equals(enums.etype.on_long_press_url)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,Collections.singletonList(data.get(0).toString()),enums.etype.on_long_press_url);
            }
            else if(e_type.equals(enums.etype.open_new_tab)){
                onOpenLinkNewTab(data.get(0).toString());
            }
            else if(e_type.equals(enums.etype.on_close_sesson)){
                onCloseCurrentTab(mGeckoClient.getSession());
            }
            else if(e_type.equals(enums.etype.on_playstore_load)){
                helperMethod.openPlayStore(data.get(0).toString().split("__")[1],homeController.this);
            }
        }
    }
}

