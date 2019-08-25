package com.darkweb.genesissearchengine.appManager.home_activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuCompat;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.preferenceController;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.messageManager;
import com.darkweb.genesissearchengine.pluginManager.orbotManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class viewController
{
    /*ViewControllers*/
    private WebView webView;
    private FrameLayout webviewContainer;
    private ProgressBar progressBar;
    private AutoCompleteTextView searchbar;
    private ConstraintLayout splashScreen;
    private ConstraintLayout requestFailure;
    private FloatingActionButton floatingButton;
    private ImageView loading;
    private ImageView splashlogo;
    private TextView loadingText;

    /*Private Variables*/
    private boolean pageLoadedSuccessfully = true;
    private boolean isSplashLoading = false;
    private Handler updateUIHandler = null;

    /*ProgressBar Delayed Updater*/
    Handler progressBarHandler = null;

    /*Initializations*/
    private static final viewController ourInstance = new viewController();

    public static viewController getInstance()
    {
        return ourInstance;
    }

    private viewController()
    {
    }

    void initialization(WebView webView1,FrameLayout webviewContainer,TextView loadingText, ProgressBar progressBar, AutoCompleteTextView searchbar, ConstraintLayout splashScreen, ConstraintLayout requestFailure, FloatingActionButton floatingButton, ImageView loading, ImageView splashlogo)
    {
        this.webView = webView1;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.splashScreen = splashScreen;
        this.requestFailure = requestFailure;
        this.floatingButton = floatingButton;
        this.loading = loading;
        this.splashlogo = splashlogo;
        this.loadingText = loadingText;
        this.webviewContainer = webviewContainer;

        homeModel.getInstance().getHomeInstance().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        checkSSLTextColor();
        initSplashScreen();
        initLock();
        initViews();
        createUpdateUiHandler();
        initializeSuggestionView();
        updateSearchEngineLogo();
    }

    private void initializeSuggestionView()
    {
        AutoCompleteAdapter suggestionAdapter = new AutoCompleteAdapter(homeModel.getInstance().getHomeInstance(), R.layout.hint_view, R.id.hintCompletionHeader, homeModel.getInstance().getSuggestions());

        int width = Math.round(helperMethod.screenWidth());
        searchbar.setThreshold(2);
        searchbar.setAdapter(suggestionAdapter);
        searchbar.setDropDownVerticalOffset(22);
        searchbar.setDropDownWidth(Math.round(width*0.95f));
        searchbar.setDropDownHorizontalOffset(Math.round(width*0.114f)*-1);

        Drawable drawable;
        Resources res = homeModel.getInstance().getHomeInstance().getResources();
        try {
            drawable = Drawable.createFromXml(res, res.getXml(R.xml.rouned_corner));
            searchbar.setDropDownBackgroundDrawable(drawable);
        } catch (Exception ex) {
            Log.i("sdfsdf", ex.getMessage());
        }

    }

    void reInitializeSuggestion()
    {
        initializeSuggestionView();
    }

    private boolean isHiddenView()
    {
        return homeModel.getInstance().getHomeInstance().isGeckoViewRunning();
    }

    private void initViews()
    {
        floatingButton.setVisibility(View.INVISIBLE);
    }

    private void initLock()
    {
        Drawable img = homeModel.getInstance().getHomeInstance().getResources().getDrawable( R.drawable.icon_lock);
        searchbar.measure(0, 0);
        img.setBounds( 0, (int)(searchbar.getMeasuredHeight()*0.00), (int)(searchbar.getMeasuredHeight()*1.10), (int)(searchbar.getMeasuredHeight()*0.69) );
        searchbar.setCompoundDrawables( img, null, null, null );
    }

    void onRequestTriggered(boolean isHiddenWeb, String url)
    {
        onProgressBarUpdate(4);
        helperMethod.hideKeyboard(homeModel.getInstance().getHomeInstance());
        pageLoadedSuccessfully = true;
        onUpdateSearchBar(url);
        checkSSLTextColor();
        onClearSearchBarCursor();

        searchbar.setFocusableInTouchMode(false);
        searchbar.setFocusable(false);
        searchbar.setFocusableInTouchMode(true);
        searchbar.setFocusable(true);
    }

    /*Helper Methods*/
    void onInternetError()
    {
        disableSplashScreen();
        requestFailure.setVisibility(View.VISIBLE);
        webView.setAlpha(0);
        requestFailure.animate().alpha(1f).setDuration(150);
        pageLoadedSuccessfully = false;
        onClearSearchBarCursor();
        onProgressBarUpdate(0);
        disableFloatingView();
        homeModel.getInstance().getHomeInstance().releaseSession();
    }

    public void disableSplashScreen()
    {
        if(!isSplashLoading)
        {
            isSplashLoading = true;
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        boolean isFirstInstall = preferenceController.getInstance().getBool(keys.hasOrbotInstalled,true);
                        boolean isHidden = (status.search_status.equals(enums.searchEngine.Google.toString()) || status.search_status.equals(enums.searchEngine.Bing.toString()));
                        while (!status.isTorInitialized && (isFirstInstall || status.search_status.equals(enums.searchEngine.Google.toString()) || status.search_status.equals(enums.searchEngine.Bing.toString())))
                        {
                            startPostTask(messages.UPDATE_LOADING_TEXT);
                            sleep(100);
                        }
                        if(isHidden)
                        {
                            if(!isFirstInstall)
                            {
                                startPostTask(messages.LOAD_COMPLETED);
                            }
                            else
                            {
                                startPostTask(messages.INSTALL_COMPLETED);
                            }
                        }
                        preferenceController.getInstance().setBool(keys.hasOrbotInstalled,false);
                        if(!status.gateway || pluginController.getInstance().proxyStatus() || !status.search_status.equals("Hidden Web"))
                        {
                            startPostTask(messages.DISABLE_SPLASH_SCREEN);
                        }
                        else
                        {
                            isSplashLoading = false;
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.i("Fizza",ex.getMessage());
                    }
                }
            }.start();
        }
    }

    public void startPostTask(int m_id)
    {
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler()
    {
                updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.INSTALL_COMPLETED)
                {
                    loadingText.setText("Installed Successfully | Starting Search");
                }
                else if(msg.what == messages.LOAD_COMPLETED)
                {
                    loadingText.setText("Loading Successfully | Starting Search");
                }
                else if(msg.what == messages.UPDATE_LOADING_TEXT)
                {
                    loadingText.setText(pluginController.getInstance().orbotLogs());
                }
                else if(msg.what == messages.DISABLE_SPLASH_SCREEN)
                {

                    if(!status.search_status.equals("Hidden Web"))
                    {
                        boolean e_status = homeModel.getInstance().getHomeInstance().initSearchEngine();

                        if(e_status)
                        {
                            hideSplashScreen();
                        }
                    }
                    else
                    {
                        hideSplashScreen();
                    }
                }
                else if(msg.what == messages.BANNER_ADS_LOADED)
                {
                    setBannerAdMargin();
                }
            }
        };
    }

    void hideSplashScreen()
    {
        if(splashScreen.getVisibility()!=View.GONE)
        {
            onWelcomeMessageCheck();
        }
        pluginController.getInstance().initializeBannerAds();
        status.isApplicationLoaded = true;
        splashScreen.animate().alpha(0.0f).setDuration(200).setListener(null).withEndAction((() -> splashScreen.setVisibility(View.GONE)));
    }

    boolean onDisableInternetError()
    {
        if(requestFailure.getAlpha()==1)
        {
            requestFailure.animate().alpha(0f).setDuration(150).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));
            return true;
        }
        else
        {
            return false;
        }
    }

    @SuppressLint("RestrictedApi")
    void onPageFinished(boolean status)
    {
        helperMethod.hideKeyboard(homeModel.getInstance().getHomeInstance());
        progressBar.setProgress(100);

        if(!status)
        {
            if(pageLoadedSuccessfully)
            {
                requestFailure.animate().alpha(0f).setStartDelay(200).setDuration(200).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));
                onUpdateView(true);
            }
            //onUpdateSearchBar(webView.getUrl());
            disableSplashScreen();
            floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));

            homeModel.getInstance().getHomeInstance().stopHiddenView(false,false);
        }
        else
        {
            onUpdateView(false);
            floatingButton.animate().alpha(1).withEndAction((() -> floatingButton.setVisibility(View.VISIBLE)));
        }
    }

    void checkSSLTextColor()
    {
        if (searchbar == null)
        {
            return;
        }

        if (searchbar.getText().toString().contains("https://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 123, 43)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        } else if (searchbar.getText().toString().contains("http://"))
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 128, 43)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        } else
        {
            SpannableString ss = new SpannableString(searchbar.getText());
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, searchbar.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchbar.setText(ss);
        }
    }

    void onClearSearchBarCursor()
    {
        searchbar.clearFocus();
    }

    void disableFloatingView()
    {
        floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));

    }

    void onUpdateSearchBar(String url)
    {
        if(!url.equals("about:blank"))
        {
            searchbar.setText(url.replace(constants.backendUrlHost,constants.frontEndUrlHost_v1));
            checkSSLTextColor();
        }

    }

    private void initSplashScreen()
    {
        boolean hasSoftKey = helperMethod.hasSoftKeys(homeModel.getInstance().getHomeInstance().getWindowManager());
        int height = helperMethod.screenHeight(hasSoftKey, homeModel.getInstance().getHomeInstance());

        splashlogo.getLayoutParams().height = height;
        loading.setAnimation(helperMethod.getRotationAnimation());
        loading.setLayoutParams(helperMethod.getCenterScreenPoint(loading.getLayoutParams()));
    }

    void onProgressBarUpdate(int progress)
    {
        if(progress==0)
        {
            progressBar.animate().alpha(0).withEndAction((() -> progressBar.setProgress(progress)));
        }
        else if(splashScreen.getVisibility() == View.GONE)
        {
            if(progressBar.getVisibility()==View.INVISIBLE)
            {
                progressBar.setProgress(4);
            }
            else
            {
                progressBar.setProgress(progress);
            }
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAlpha(1);
        }
    }

    void onBackPressed()
    {
        if(homeModel.getInstance().getNavigation().size()>0)
        {
            if(homeModel.getInstance().getNavigation().size()==1)
            {
                onProgressBarUpdate(0);
                helperMethod.onMinimizeApp(homeModel.getInstance().getHomeInstance());
                return;
            }
            else if(homeModel.getInstance().getNavigation().get(homeModel.getInstance().getNavigation().size()-2).type().equals(enums.navigationType.base))
            {
                homeModel.getInstance().getHomeInstance().stopHiddenView(true,true);
                if(webView.getVisibility()==View.VISIBLE)
                {
                    onProgressBarUpdate(4);
                    webView.goBack();
                }
                else
                {
                    onProgressBarUpdate(0);
                }

                /*CHANGED BUT NOT TESTED*/
                homeModel.getInstance().getNavigation().remove(homeModel.getInstance().getNavigation().size()-1);

                webView.bringToFront();
                webView.setAlpha(1);
                webView.setVisibility(View.VISIBLE);
                requestFailure.animate().alpha(0f).setDuration(200).withEndAction((() -> requestFailure.setVisibility(View.INVISIBLE)));
                onUpdateSearchBar(webView.getUrl());
                floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));
            }
            else
            {
                homeModel.getInstance().getHomeInstance().stopHiddenView(true,true);
                homeModel.getInstance().getNavigation().remove(homeModel.getInstance().getNavigation().size()-1);
                if(webView.getVisibility()==View.VISIBLE)
                {
                    homeModel.getInstance().getHomeInstance().onReInitGeckoView();
                    homeModel.getInstance().getHomeInstance().onReloadHiddenView();
                }
                else
                {
                    homeModel.getInstance().getHomeInstance().onHiddenGoBack();
                }
            }
        }
    }

    void onUpdateView(boolean status)
    {
        if(status)
        {
            floatingButton.animate().alpha(0).withEndAction((() -> floatingButton.setVisibility(View.GONE)));
            webView.setAlpha(1);
            webView.setVisibility(View.VISIBLE);
            webView.bringToFront();
            onProgressBarUpdate(0);
            onUpdateSearchBar(webView.getUrl());
        }
        else
        {
            webView.animate().alpha(0).setDuration(150).withEndAction((() -> webView.setVisibility(View.GONE)));
        }
    }

    private void onWelcomeMessageCheck()
    {
        if(!preferenceController.getInstance().getBool("FirstTimeLoaded",false))
        {
            pluginController.getInstance().MessageManagerHandler(null,enums.popup_type.welcome);
        }
    }

    public void onShowAds()
    {
        startPostTask(messages.SHOW_ADS);
    }

    void openMenu(View view)
    {
        LinearLayout parentView = (LinearLayout)view.getParent();

        PopupMenu popup = new PopupMenu(homeModel.getInstance().getHomeInstance(), parentView,Gravity.RIGHT);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);
        popup.setOnMenuItemClickListener(item ->
        {
            homeModel.getInstance().getHomeInstance().onMenuOptionSelected(item);
            return true;
        });

        MenuItem item = popup.getMenu().findItem(R.id.menu2);

        if(status.gateway == false)
        {
            item.setTitle("Tor Banned | Enable Gateway");
        }
        else
        {
            item.setTitle("Disable Gateway");
        }

        popup.show();
        view.bringToFront();
    }

    void onReload()
    {
        String url = searchbar.getText().toString();

        if(helperMethod.getHost(url).contains("genesis"))
        {
            onRequestTriggered(false,webView.getUrl());
            webView.reload();
        }
        else
        {
            homeModel.getInstance().getHomeInstance().onReloadHiddenView();
        }
    }

    String getSearchBarUrl()
    {
        return searchbar.getText().toString();
    }

    void lowMemoryError()
    {
        if(preferenceController.getInstance().getBool(keys.low_memory,false))
        {
            preferenceController.getInstance().setBool(keys.low_memory,false);
            helperMethod.showToast("App Closed Due To Low Memory", homeModel.getInstance().getHomeInstance());
        }
    }

    public void setBannerAdMargin()
    {
        final float scale = homeModel.getInstance().getHomeInstance().getResources().getDisplayMetrics().density;
        int padding_102dp = (int) (52 * scale + 0.52f);

        webviewContainer.setPadding(0,padding_102dp,0,0);
    }

    public void onBannerAdLoaded()
    {
        startPostTask(messages.BANNER_ADS_LOADED);
    }

    public void updateSearchEngineLogo()
    {
        homeModel.getInstance().getHomeInstance().findViewById(R.id.switchEngine);
        if(status.search_status.equals("Google") || status.search_status.equals("Bing"))
        {
            ImageButton button = homeModel.getInstance().getHomeInstance().findViewById(R.id.switchEngine);
            button.setImageResource(R.drawable.genesis_logo);
        }
        else
        {
            ImageButton button = homeModel.getInstance().getHomeInstance().findViewById(R.id.switchEngine);
            button.setImageResource(R.drawable.google_logo);
        }
    }
}