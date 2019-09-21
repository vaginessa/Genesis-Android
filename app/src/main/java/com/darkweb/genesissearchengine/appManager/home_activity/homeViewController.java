package com.darkweb.genesissearchengine.appManager.home_activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;

import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperMethod;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.function.Function;

class homeViewController
{
    /*ViewControllers*/
    private AppCompatActivity context;
    eventObserver.eventListener event;

    /*ViewControllers*/
    private FrameLayout webviewContainer;
    private ProgressBar progressBar;
    private AutoCompleteTextView searchbar;
    private ConstraintLayout splashScreen;
    private ConstraintLayout requestFailure;
    private FloatingActionButton floatingButton;
    private ImageView loading;
    private TextView loadingText;
    private AdView banner_ads = null;
    private boolean request_failed = false;
    private Handler updateUIHandler = null;
    private boolean has_application_started = false;
    private ImageView engineLogo;
    private ImageButton gateway_splash;
    private LinearLayout top_bar;
    private GeckoView gecko_view;
    private ImageView backsplash;

    private Handler progress_handler = null;

    homeViewController()
    {
    }

    void initialization(eventObserver.eventListener event,AppCompatActivity context, FrameLayout webviewContainer, TextView loadingText, ProgressBar progressBar, AutoCompleteTextView searchbar, ConstraintLayout splashScreen, ConstraintLayout requestFailure, FloatingActionButton floatingButton, ImageView loading, AdView banner_ads,ArrayList<String> suggestions,ImageView engineLogo,ImageButton gateway_splash,LinearLayout top_bar,GeckoView gecko_view,ImageView backsplash){
        this.context = context;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.splashScreen = splashScreen;
        this.requestFailure = requestFailure;
        this.floatingButton = floatingButton;
        this.loading = loading;
        this.loadingText = loadingText;
        this.webviewContainer = webviewContainer;
        this.banner_ads = banner_ads;
        this.event = event;
        this.engineLogo = engineLogo;
        this.gateway_splash = gateway_splash;
        this.top_bar = top_bar;
        this.gecko_view = gecko_view;
        this.backsplash = backsplash;

        initSplashScreen();
        initializeSuggestionView(suggestions);
        initLock();
        initSearchImage();
        createUpdateUiHandler();
    }

    private void initSearchImage(){
        if(status.search_status.equals(constants.backendGenesis))
        {
            engineLogo.setImageResource(R.drawable.duck_logo);
        }
        else if(status.search_status.equals(constants.backendDuckDuckGo))
        {
            engineLogo.setImageResource(R.drawable.google_logo);
        }
        else
        {
            engineLogo.setImageResource(R.drawable.genesis_logo);
        }
    }

    private void initPostUI(boolean isSplash){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(isSplash){
                window.setStatusBarColor(context.getResources().getColor(R.color.ease_blue));
            }
            else if(!has_application_started){
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    window.setStatusBarColor(context.getResources().getColor(R.color.blue_dark));
                }
                else {
                    animateStatusBarEndColor();
                }
            }
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void animateStatusBarEndColor() {
        AnimatedColor oneToTwo = new AnimatedColor(ContextCompat.getColor(context, R.color.ease_blue), ContextCompat.getColor(context, R.color.black_blue));
        AnimatedColor twoToThree = new AnimatedColor(ContextCompat.getColor(context, R.color.black_blue), ContextCompat.getColor(context, R.color.black_blue_dark));
        AnimatedColor ThreeToFour = new AnimatedColor(ContextCompat.getColor(context, R.color.black_blue_dark), ContextCompat.getColor(context, R.color.white));

        ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1f).setDuration(68);
        animator.addUpdateListener(animation ->
        {
            float v = (float) animation.getAnimatedValue();
            context.getWindow().setStatusBarColor(oneToTwo.with(v));
            context.getWindow().setStatusBarColor(oneToTwo.with(v));
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final ValueAnimator animator2 = ObjectAnimator.ofFloat(0f, 1f).setDuration(69);
                animator2.addUpdateListener(animation1 ->
                {
                    float v = (float) animation1.getAnimatedValue();
                    context.getWindow().setStatusBarColor(twoToThree.with(v));
                });
                animator2.start();

                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final ValueAnimator animator3 = ObjectAnimator.ofFloat(0f, 1f).setDuration(68);
                        animator3.addUpdateListener(animation1 ->
                        {
                            float v = (float) animation1.getAnimatedValue();
                            context.getWindow().setStatusBarColor(ThreeToFour.with(v));

                        });
                        animator3.start();
                    }
                });
                animator2.start();

            }
        });
        animator.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    private void initLock(){
        Drawable img = context .getResources().getDrawable( R.drawable.icon_lock);
        searchbar.measure(0, 0);
        img.setBounds( 0, (int)(searchbar.getMeasuredHeight()*0.00), (int)(searchbar.getMeasuredHeight()*1.10), (int)(searchbar.getMeasuredHeight()*0.69) );
        searchbar.setCompoundDrawables( img, null, null, null );
    }

    void initializeSuggestionView(ArrayList<String> suggestions)
    {
        autoCompleteAdapter suggestionAdapter = new autoCompleteAdapter(context, R.layout.hint_view, R.id.hintCompletionHeader, suggestions);

        int width = Math.round(helperMethod.screenWidth());
        searchbar.setThreshold(2);
        searchbar.setAdapter(suggestionAdapter);
        searchbar.setDropDownVerticalOffset(22);
        searchbar.setDropDownWidth(Math.round(width*0.95f));
        searchbar.setDropDownHorizontalOffset(Math.round(width*0.114f)*-1);

        Drawable drawable;
        Resources res = context.getResources();
        try {
            drawable = Drawable.createFromXml(res, res.getXml(R.xml.rouned_corner));
            searchbar.setDropDownBackgroundDrawable(drawable);
        } catch (Exception ex) {
        }
    }

    private void initSplashScreen()
    {

        //context.getWindow().clearFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT);
        loading.setAnimation(helperMethod.getRotationAnimation());
        loading.setLayoutParams(helperMethod.getCenterScreenPoint(loading.getLayoutParams()));
        searchbar.setEnabled(false);

        initPostUI(true);

        backsplash.getLayoutParams().height = getScreenHeight() - getStatusBarHeight()*2;
        loading.setAnimation(helperMethod.getRotationAnimation());
        loading.setLayoutParams(helperMethod.getCenterScreenPoint(loading.getLayoutParams()));
        searchbar.setEnabled(false);

        View root = searchbar.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_purple));

    }

    private int getScreenHeight() {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.y;
    }

    /*Helper UI Methods*/

    void onPageFinished(){
        searchbar.setEnabled(true);
        progressBar.bringToFront();
        request_failed = false;

        splashScreenDisable();

        splashScreen.animate().setDuration(200).alpha(0f).withEndAction((() -> applicationStarted()));
        onDisableInternetError();

    }

    private void splashScreenDisable(){
        top_bar.setAlpha(1);
        gecko_view.setAlpha(1);
        initPostUI(false);
    }

    void onDisableInternetError(){
        requestFailure.animate().alpha(0f).setDuration(150).withEndAction((() -> requestFailure.setVisibility(View.GONE)));
    }

    void onLoadError(){
        onInternetError();
    }

    private void onInternetError()
    {
        requestFailure.setVisibility(View.VISIBLE);
        requestFailure.animate().alpha(1f).setDuration(150);
        onProgressBarUpdate(0,false);
        clearSelections();

        splashScreenDisable();
        splashScreen.animate().setDuration(200).alpha(0f).withEndAction((() -> applicationStarted()));
    }

    boolean is_loading = false;
    Callable<String> logs = null;
    Thread splash_thread = null;

    void reset(){
        if(splash_thread!=null){
            splash_thread.interrupt();
        }
    }

    void disableSplashScreen(Callable<String> logs){


        Log.i("FUCK ME 2 : ","asd : " + is_loading);

        if(is_loading)
        {
            return;
        }

        is_loading = true;
        this.logs = logs;

        if(splashScreen.getAlpha()==1){
            splash_thread = new Thread(){
                public void run(){

                    AppCompatActivity temp_context = context;
                    while (!status.isTorInitialized && (!status.search_status.equals(constants.backendGenesis) || !status.isBootstrapped)){
                        try
                        {
                            sleep(1000);
                            event.invokeObserver(Collections.singletonList(status.search_status), enums.home_eventType.recheck_orbot);
                            if(temp_context.isDestroyed()){
                                return;
                            }
                            startPostTask(messages.UPDATE_LOADING_TEXT);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    startPostTask(messages.ON_URL_LOAD);
                }
            };
            splash_thread.start();
        }
    }

    private void startPostTask(int m_id)
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
                if(msg.what == messages.ON_URL_LOAD)
                {
                    event.invokeObserver(Collections.singletonList(status.search_status), enums.home_eventType.on_url_load);
                }
                if(msg.what == messages.UPDATE_LOADING_TEXT)
                {
                    if(logs!=null)
                    {
                        try
                        {
                            logs.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void applicationStarted(){
        if(!has_application_started)
        {
            splashScreen.setVisibility(View.GONE);
            event.invokeObserver(null, enums.home_eventType.on_init_ads);
            has_application_started = true;
        }
    }

    void clearSelections(){
        searchbar.clearFocus();
        helperMethod.hideKeyboard(context);
    }

    void setBannerAdMargin()
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_102dp = (int) (48 * scale + 0.52f);

        webviewContainer.setPadding(0,padding_102dp,0,0);

        banner_ads.setAlpha(0f);
        banner_ads.animate().setDuration(1000).alpha(1f);
    }

    void onProgressBarUpdate(int value,boolean loading_status){

        if(value==0)
        {
            progressReVerify(value,loading_status);
        }
        else if(splashScreen.getVisibility() == View.GONE)
        {
            status.isAppStarted = true;

            if(progressBar.getVisibility()==View.INVISIBLE)
            {
                progressBar.setProgress(10);
            }
            else
            {
                if(value==100){
                    progressReVerify(value,loading_status);
                }else {
                    progressBar.setProgress(value);
                }
            }
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAlpha(1);
        }
        else {
            progressBar.setProgress(0);
        }
    }

    private void progressReVerify(int value,boolean loading_status){
        Log.i("Progress___1",value+""+loading_status);
        if(progress_handler!=null){
            progress_handler.removeCallbacksAndMessages(null);
        }
        progress_handler = new Handler();
        progress_handler.postDelayed(() ->
        {
            Log.i("Progress___2",value+":"+loading_status);
            if(!loading_status || value==100){
                progressBar.setProgress(100);
                progressBar.animate().alpha(0).withEndAction((() -> progressBar.setVisibility(View.GONE)));
            }
        }, 100);
    }

    void onUrlLoad(String url){
        updateSearchBar(url);
    }

    void openMenu(View view)
    {
        view.bringToFront();
        LinearLayout parentView = (LinearLayout)view.getParent();

        PopupMenu popup = new PopupMenu(context, parentView, Gravity.RIGHT);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);
        popup.setOnMenuItemClickListener(item ->
        {
            event.invokeObserver(Collections.singletonList(item.getItemId()), enums.home_eventType.onMenuSelected);
            return true;
        });

        MenuItem item = popup.getMenu().findItem(R.id.menu2);

        if(!status.gateway)
        {
            item.setTitle("Tor Banned | Enable Gateway");
        }
        else
        {
            item.setTitle("Disable Gateway | Improve Speed");
        }

        popup.show();
        view.bringToFront();
    }

    /*Helper Methods*/

    void updateSearchBar(String url)
    {
        if (searchbar == null)
        {
            return;
        }

        searchbar.setText(url);

        searchbar.setText(helperMethod.urlDesigner(searchbar.getText().toString()));
    }

    void updateLogs(String log){
        loadingText.setText(log);
    }
}