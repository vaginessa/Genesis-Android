package com.darkweb.genesissearchengine.appManager.home_activity;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.*;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class homeController extends AppCompatActivity implements ComponentCallbacks2
{
    /*Model Declaration*/
    private homeViewController home_view_controller;
    private homeModel home_model;

    /*View Webviews*/
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
    private ImageView engineLogo;
    private ImageButton gateway_splash;
    private LinearLayout top_bar;
    private ImageView backsplash;

    /*Redirection Objects*/
    private geckoClients geckoclient = null;

    private boolean page_closed = false;

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
        home_view_controller = new homeViewController();
        home_model = new homeModel();
    }

    public void initializeConnections()
    {
        geckoView = findViewById(R.id.webLoader);

        progressBar = findViewById(R.id.progressBar);
        requestFailure = findViewById(R.id.requestFailure);
        splashScreen = findViewById(R.id.splashScreen);
        searchbar = findViewById(R.id.search);
        floatingButton = findViewById(R.id.floatingActionButton3);
        loadingIcon = findViewById(R.id.imageView_loading_back);
        loadingText = findViewById(R.id.loadingText);
        webviewContainer = findViewById(R.id.webviewContainer);
        banner_ads = findViewById(R.id.adView);
        engineLogo = findViewById(R.id.switchEngine);
        gateway_splash = findViewById(R.id.gateway_splash);
        top_bar = findViewById(R.id.topbar);
        backsplash = findViewById(R.id.backsplash);

        geckoclient = new geckoClients();

        boolean is_engine_switched = dataController.getInstance().getBool(keys.engine_switched,false);

        home_view_controller.initialization(new homeViewCallback(),this,webviewContainer,loadingText,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,banner_ads,dataController.getInstance().getSuggestions(),engineLogo,gateway_splash,top_bar,geckoView,backsplash,is_engine_switched);
    }

    public void initializePermission(){
        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
        }
    }

    public void initializeGeckoView(){
        geckoclient.initialize(geckoView,this,new geckoViewCallback(),status.search_status,this);
        //pluginController.getInstance().setProxy(true,status.search_status==constants.backendGenesis);

        Callable<String> callable = () -> {
            String log = pluginController.getInstance().orbotLogs();

            home_view_controller.updateLogs(pluginController.getInstance().orbotLogs());
            return strings.emptyStr;
        };


    }



    public void onProxyEnabled(View view){
        if(pluginController.getInstance().isInitialized() && !page_closed){
            helperMethod.openActivity(orbot_view_settings.class, constants.list_history, homeController.this,true);
            //pluginController.getInstance().MessageManagerHandler(homeController.this,"-1",enums.popup_type.tor_banned);
        }
    }

    public void initializeProxy(){
        pluginController.getInstance().proxyManagerInvoke(true);
    }
    /*Shared Controller Events*/

    public void reloadJavaScript(){
        geckoclient.onUpdateSettings();
    }

    public void onUpdateFont(){
        geckoclient.onUpdateFont();
        home_view_controller.onReDraw();
    }

    public void loadURL(String url){

        home_view_controller.clearSelections();
        geckoclient.loadURL(url);
    }

    /*User Events*/

    private void initializeLocalEventHandlers() {

        searchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE)
            {
                helperMethod.hideKeyboard(homeController.this);

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                {
                    pluginController.getInstance().logEvent(strings.search_invoked,"");
                    onSearchBarInvoked(v);
                    geckoView.clearFocus();
                }, 500);
            }
            return true;
        });

        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                status.isAppStarted = true;
                pluginController.getInstance().onMessageReset();
            }
        });

        geckoView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                status.isAppStarted = true;
                pluginController.getInstance().onMessageReset();
            }
        });

        pluginController.getInstance().logEvent(strings.app_started,"");
    }

    void onSearchBarInvoked(View view)
    {
        String url = ((EditText)view).getText().toString();
        url = home_model.url_complete(url);
        home_view_controller.updateSearchBar(url);
        loadURL(url);
    }

    public void onSuggestionInvoked(View view){
        String val = ((TextView)view).getText().toString();
        val = home_model.url_complete(val);
        home_view_controller.updateSearchBar(val);
        searchbar.clearFocus();
    }

    public void onHomeButton(View view)
    {
        pluginController.getInstance().logEvent(strings.home_invoked,"");
        loadURL(home_model.getSearchEngine());
    }

    public void onOpenMenuItem(View view)
    {
        pluginController.getInstance().logEvent(strings.menu_invoked,"");
        status.isAppStarted = true;
        home_view_controller.clearSelections();
        home_view_controller.openMenu(view);
        pluginController.getInstance().onMessageReset();
    }

    @Override
    public void onBackPressed()
    {
        pluginController.getInstance().logEvent(strings.on_back,"");
        geckoView.clearFocus();
        if(requestFailure.getVisibility()==View.VISIBLE){
            home_view_controller.onDisableInternetError();
            home_view_controller.updateSearchBar(geckoclient.getCurrentURL());
            geckoclient.updateProxy(geckoclient.getCurrentURL());
        }
        else {
            if(!geckoclient.getFullScreenStatus()){
                geckoclient.onBackPressed();
                home_view_controller.clearSelections();
            }
            else {
                geckoclient.exitFullScreen();
            }
        }
    }

    public void onSwitchSearch(View view)
    {
        home_view_controller.stopEngineAnimation();
        dataController.getInstance().setBool(keys.engine_switched,true);
        pluginController.getInstance().logEvent(strings.search_switch,"");

        if(status.search_status.equals(constants.backendGoogle))
        {
            status.search_status = constants.backendGenesis;
            home_view_controller.onUpdateLogo();
            geckoclient.setCurrentURL(constants.backendGenesis);
            geckoclient.setRequestedUrl(constants.backendGenesis);
            dataController.getInstance().setString(keys.search_engine,constants.backendGenesis);
            onHomeButton(null);
        }
        else if(status.search_status.equals(constants.backendGenesis))
        {
            status.search_status = constants.backendDuckDuckGo;
            if(pluginController.getInstance().OrbotManagerInit())
            {
                home_view_controller.onUpdateLogo();
                dataController.getInstance().setString(keys.search_engine,constants.backendDuckDuckGo);
                geckoclient.setRequestedUrl(constants.backendDuckDuckGo);
                onHomeButton(null);
            }
            else {
                geckoclient.setCurrentURL(constants.backendDuckDuckGo);
                geckoclient.setRequestedUrl(constants.backendDuckDuckGo);
                pluginController.getInstance().MessageManagerHandler(homeController.this,constants.backendDuckDuckGo,enums.popup_type.start_orbot);
            }
        }
        else
        {
            status.search_status = constants.backendGoogle;
            if(pluginController.getInstance().OrbotManagerInit())
            {
                home_view_controller.onUpdateLogo();
                dataController.getInstance().setString(keys.search_engine,constants.backendGoogle);
                geckoclient.setRequestedUrl(constants.backendGoogle);
                onHomeButton(null);
            }
            else {
                geckoclient.setCurrentURL(constants.backendDuckDuckGo);
                pluginController.getInstance().MessageManagerHandler(homeController.this,constants.backendGoogle,enums.popup_type.start_orbot);
                geckoclient.setRequestedUrl(constants.backendGoogle);
            }
        }
    }

    public void onReload(View view)
    {
        geckoclient.loadURL(geckoclient.getRequestedURL());
        home_view_controller.onUpdateLogo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginController.getInstance().onMessageReset();
    }

    @Override
    public void onTrimMemory(int level)
    {
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1){
            geckoclient.onFileCallbackResult(resultCode,data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        pluginController.getInstance().onMessageReset();
    }

    public void onSuggestionUpdate(){
        home_view_controller.initializeSuggestionView(dataController.getInstance().getSuggestions());
    }

    public void onStartApplication(View view){
        disableSplash();
    }

    public void onDownloadFile(){
        geckoclient.downloadFile();
    }

    public void manualDownload(String url){
        geckoclient.manual_download(url);
    }

    public void onBannerAdLoaded(){
        home_view_controller.setBannerAdMargin();
    }

    /*Callback Events*/

    public String orbotLogs(){
        return pluginController.getInstance().orbotLogs();
    }

    public AdView getBannerAd()
    {
        return banner_ads;
    }

    public void startGateway(boolean cur_status){

        page_closed = true;
        pluginController.getInstance().logEvent(strings.proxy_changed,"");
        new Thread(){
            public void run(){
                try
                {
                    sleep(1000);
                    status.gateway = cur_status;
                    dataController.getInstance().setBool(keys.gateway,cur_status);
                    pluginController.getInstance().reset();
                    finish();
                    overridePendingTransition(0, 0);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void disableSplash(){

         Callable<String> callable = () -> {

            String log = pluginController.getInstance().orbotLogs();

            home_view_controller.updateLogs(pluginController.getInstance().orbotLogs());
            return strings.emptyStr;
        };

        home_view_controller.disableSplashScreen(callable);
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.home_eventType e_type)
        {
                if(e_type.equals(enums.home_eventType.onMenuSelected)){
                int menuId = (int)data.get(0);
                if (menuId == R.id.menu1) {
                    helperMethod.openActivity(historyController.class, constants.list_history, homeController.this,true);
                }
                else if (menuId == R.id.menu2) {
                    helperMethod.openActivity(orbot_view_settings.class, constants.list_history, homeController.this,true);
                }
                else if (menuId == R.id.menu9) {
                    loadURL("https://whatismycountry.com/");
                }
                else if (menuId == R.id.menu3) {
                    helperMethod.openActivity(settingController.class,constants.list_history, homeController.this,true);
                }
                else if (menuId == R.id.menu4)
                {
                    pluginController.getInstance().MessageManagerHandler(homeController.this,searchbar.getText().toString(),enums.popup_type.bookmark);
                }
                else if (menuId == R.id.menu5)
                {
                    helperMethod.openActivity(bookmarkController.class,constants.list_bookmark, homeController.this,true);
                }
                else if (menuId == R.id.menu6)
                {
                    pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.popup_type.report_url);
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
           else if(e_type.equals(enums.home_eventType.on_init_ads))
           {
               pluginController.getInstance().initializeBannerAds();
           }
           else if(e_type.equals(enums.home_eventType.on_url_load)){
               home_view_controller.updateLogs("Starting | Genesis Search");
               loadURL(data.get(0).toString());
           }
           else if(e_type.equals(enums.home_eventType.recheck_orbot)){
               pluginController.getInstance().OrbotManagerInit();
           }

        }
    }
    public class geckoViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> data, enums.home_eventType e_type)
        {
            if(e_type.equals(enums.home_eventType.progress_update)){
                home_view_controller.onProgressBarUpdate((int)data.get(0),geckoclient.isSessionRunning());
            }
            else if(e_type.equals(enums.home_eventType.on_url_load)){
                home_view_controller.onUrlLoad(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.back_list_empty)){
                helperMethod.onMinimizeApp(homeController.this);
            }
            else if(e_type.equals(enums.home_eventType.start_proxy)){
                pluginController.getInstance().setProxy((Boolean)data.get(0),(Boolean)data.get(1));
            }
            else if(e_type.equals(enums.home_eventType.on_request_completed)){
                dataController.getInstance().addHistory(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.on_page_loaded)){
                pluginController.getInstance().logEvent(strings.page_opened_success,"");
                dataController.getInstance().setBool(keys.is_bootstrapped,true);
                home_view_controller.onPageFinished();
                if(status.isWelcomeEnabled && !status.isAppStarted){
                    final Handler handler = new Handler();
                    helperMethod.hideKeyboard(homeController.this);
                    Runnable runnable = () ->
                    {
                        if(!status.isAppStarted){
                            pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), strings.emptyStr, enums.popup_type.welcome);
                        }
                        status.isAppStarted = true;
                        //pluginController.getInstance().onMessageReset();
                    };
                    handler.postDelayed(runnable, 1300);
                }
            }
            else if(e_type.equals(enums.home_eventType.rate_application)){
                dataController.getInstance().setBool(keys.isAppRated,true);
                pluginController.getInstance().MessageManagerHandler(activityContextManager.getInstance().getHomeController(), strings.emptyStr, enums.popup_type.rate_app);
            }
            else if(e_type.equals(enums.home_eventType.on_load_error)){
                dataController.getInstance().setBool(keys.is_bootstrapped,true);
                pluginController.getInstance().logEvent(strings.url_error,"");
                home_view_controller.onLoadError();
                dataController.getInstance().addHistory(data.get(0).toString());
                home_view_controller.updateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.search_update)){
                home_view_controller.updateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.proxy_error)){
                helperMethod.hideKeyboard(homeController.this);
                geckoView.clearFocus();
                pluginController.getInstance().logEvent(strings.url_error_not_loaded,"");
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.popup_type.start_orbot);
            }
            else if(e_type.equals(enums.home_eventType.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.popup_type.download_file);
            }
            else if(e_type.equals(enums.home_eventType.redraw)){
                home_view_controller.reset();
            }
            else if(e_type.equals(enums.home_eventType.on_full_screen)){
                boolean status = (Boolean)data.get(0);
                home_view_controller.onFullScreenStatus(status);
            }
            else if(e_type.equals(enums.home_eventType.on_long_press)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.popup_type.download_file_long_press);
            }
        }
    }
}

