package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.io.IOException;
import java.net.URL;
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

    /*Redirection Objects*/
    private geckoClients geckoclient = null;

    /*-------------------------------------------------------INITIALIZATION-------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(helperMethod.isBuildValid())
        {
            setContentView(R.layout.home_view);

            databaseController.getInstance().initialize(this);
            dataController.getInstance().initialize(this);
            activityContextManager.getInstance().setHomeController(this);

            status.initStatus();
            dataController.getInstance().initializeListData();

            initializeAppModel();
            initializeConnections();
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
        splashlogo = findViewById(R.id.backsplash);
        loadingText = findViewById(R.id.loadingText);
        webviewContainer = findViewById(R.id.webviewContainer);
        banner_ads = findViewById(R.id.adView);

        geckoclient = new geckoClients();

        home_view_controller.initialization(new homeViewCallback(),this,webviewContainer,loadingText,progressBar,searchbar,splashScreen,requestFailure,floatingButton, loadingIcon,splashlogo,banner_ads,dataController.getInstance().getSuggestions());
    }

    public void initializeGeckoView(){
        geckoclient.initialize(geckoView,this,new geckoViewCallback(),status.search_status,this);
        pluginController.getInstance().setProxy(true);

        Callable<String> callable = () -> {
            home_view_controller.updateLogs(pluginController.getInstance().orbotLogs());
            return strings.emptyStr;
        };


        if(status.isBootstrapped){
            if(status.gateway){
                home_view_controller.updateLogs("Loading | Starting Gateway");
                initializeProxy();
            }
            else {
                home_view_controller.disableSplashScreen(callable);
            }
        }
        else {
            pluginController.getInstance().MessageManagerHandler(homeController.this,null,enums.popup_type.tor_banned);
        }
    }

    public void initializeProxy(){
        pluginController.getInstance().proxyManager(true);
    }
    /*Shared Controller Events*/

    public void reloadJavaScript(){
        geckoclient.onUpdateJavascript();
    }

    public void loadURL(String url){
        home_view_controller.clearSelections();
        geckoclient.loadURL(url);
    }

    /*User Events*/

    private void initializeLocalEventHandlers() {
        searchbar.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_NEXT)
            {
                onSearchBarInvoked(v);
            }
            return true;
        });
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
        loadURL(home_model.getSearchEngine());
    }

    public void onOpenMenuItem(View view)
    {
        home_view_controller.clearSelections();
        home_view_controller.openMenu(view);
    }

    @Override
    public void onBackPressed()
    {
        geckoView.clearFocus();
        if(requestFailure.getVisibility()==View.VISIBLE){
            home_view_controller.onDisableInternetError();
        }
        else {
            geckoclient.onBackPressed();
            home_view_controller.clearSelections();
        }
    }

    public void onSwitchSearch(View view)
    {
        if(status.search_status.equals(constants.backendGoogle))
        {
            ((ImageButton) view).setImageResource(R.drawable.duck_logo);
            status.search_status = constants.backendGenesis;
            dataController.getInstance().setString(keys.search_engine,constants.backendGenesis);
            onHomeButton(null);
        }
        else if(status.search_status.equals(constants.backendGenesis))
        {
            if(pluginController.getInstance().OrbotManagerInit(true))
            {
                ((ImageButton) view).setImageResource(R.drawable.google_logo);
                status.search_status = constants.backendDuckDuckGo;
                dataController.getInstance().setString(keys.search_engine,constants.backendDuckDuckGo);
                onHomeButton(null);
            }
            else {
                pluginController.getInstance().MessageManagerHandler(homeController.this,searchbar.getText().toString(),enums.popup_type.start_orbot);
            }
        }
        else
        {
            if(pluginController.getInstance().OrbotManagerInit(true))
            {
                ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
                status.search_status = constants.backendGoogle;
                dataController.getInstance().setString(keys.search_engine,constants.backendGoogle);
                onHomeButton(null);
            }
            else {
                pluginController.getInstance().MessageManagerHandler(homeController.this,searchbar.getText().toString(),enums.popup_type.start_orbot);
            }
        }
    }

    public void onReload(View view)
    {
        geckoclient.loadURL(searchbar.getText().toString());
    }

    @Override
    public void onTrimMemory(int level)
    {
        if(status.isAppPaused && (level==80 || level==15))
        {
            dataController.getInstance().setBool(keys.low_memory,true);
            finish();
        }
    }

    @Override
    public void onResume()
    {
        status.isAppPaused = false;
        super.onResume();
    }

    @Override
    public void onPause()
    {
        status.isAppPaused = true;
        super.onPause();
    }

    public void onSuggestionUpdate(){
        home_view_controller.initializeSuggestionView(dataController.getInstance().getSuggestions());
    }

    public void onDownloadFile(){
        geckoclient.downloadFile();
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

    public void startGateway(){
        home_view_controller.updateLogs("Loading | Starting Gateway");
        pluginController.getInstance().proxyManager(true);
    }

    public void disableSplash(){

        Callable<String> callable = () -> {
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
                    helperMethod.openActivity(historyController.class, constants.list_history, homeController.this);
                }
                else if (menuId == R.id.menu2) {
                    pluginController.getInstance().proxyManager(!status.gateway);
                    status.gateway = !status.gateway;
                    dataController.getInstance().setBool(keys.gateway,status.gateway);
                }
                else if (menuId == R.id.menu9) {
                    loadURL("https://whatismycountry.com/");
                }
                else if (menuId == R.id.menu3) {
                    helperMethod.openActivity(settingController.class,constants.list_history, homeController.this);
                }
                else if (menuId == R.id.menu4)
                {
                    pluginController.getInstance().MessageManagerHandler(homeController.this,searchbar.getText().toString(),enums.popup_type.bookmark);
                }
                else if (menuId == R.id.menu5)
                {
                    helperMethod.openActivity(bookmarkController.class,constants.list_bookmark, homeController.this);
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
               loadURL(data.get(0).toString());
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
                pluginController.getInstance().setProxy((Boolean)data.get(0));
            }
            else if(e_type.equals(enums.home_eventType.on_request_completed)){
                dataController.getInstance().addHistory(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.on_page_loaded)){
                dataController.getInstance().setBool(keys.is_bootstrapped,true);
                home_view_controller.onPageFinished();
            }
            else if(e_type.equals(enums.home_eventType.on_load_error)){
                dataController.getInstance().setBool(keys.is_bootstrapped,true);
                home_view_controller.onLoadError();
                dataController.getInstance().addHistory(data.get(0).toString());
                home_view_controller.updateSearchBar(data.get(0).toString());
            }
            else if(e_type.equals(enums.home_eventType.proxy_error)){
                pluginController.getInstance().setProxy((Boolean)data.get(0));
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.popup_type.start_orbot);
            }
            else if(e_type.equals(enums.home_eventType.download_file_popup)){
                pluginController.getInstance().MessageManagerHandler(homeController.this,data.get(0).toString(),enums.popup_type.download_file);
            }
        }
    }
}

