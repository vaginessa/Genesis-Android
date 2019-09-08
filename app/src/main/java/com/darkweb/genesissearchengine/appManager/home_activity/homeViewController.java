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
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

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
    private ImageView splashlogo;
    private TextView loadingText;
    private AdView banner_ads = null;
    private boolean request_failed = false;
    private Handler updateUIHandler = null;
    private boolean has_application_started = false;


    homeViewController()
    {
    }

    void initialization(eventObserver.eventListener event,AppCompatActivity context, FrameLayout webviewContainer, TextView loadingText, ProgressBar progressBar, AutoCompleteTextView searchbar, ConstraintLayout splashScreen, ConstraintLayout requestFailure, FloatingActionButton floatingButton, ImageView loading, ImageView splashlogo, AdView banner_ads,ArrayList<String> suggestions){
        this.context = context;
        this.progressBar = progressBar;
        this.searchbar = searchbar;
        this.splashScreen = splashScreen;
        this.requestFailure = requestFailure;
        this.floatingButton = floatingButton;
        this.loading = loading;
        this.splashlogo = splashlogo;
        this.loadingText = loadingText;
        this.webviewContainer = webviewContainer;
        this.banner_ads = banner_ads;
        this.event = event;
        initSplashScreen();
        initializeSuggestionView(suggestions);
    }

    public void initializeSuggestionView(ArrayList<String> suggestions)
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
        boolean hasSoftKey = helperMethod.hasSoftKeys(context.getWindowManager());
        int height = helperMethod.screenHeight(hasSoftKey,context);

        splashlogo.getLayoutParams().height = height;
        loading.setAnimation(helperMethod.getRotationAnimation());
        loading.setLayoutParams(helperMethod.getCenterScreenPoint(loading.getLayoutParams()));
        searchbar.setEnabled(false);
        View root = searchbar.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
    }

    /*Helper UI Methods*/

    void onPageFinished(){
        searchbar.setEnabled(true);
        progressBar.bringToFront();
        request_failed = false;
        Log.i("FUCKNO","FUCK");
        splashScreen.animate().setDuration(200).alpha(0f).withEndAction((() -> applicationStarted()));
        onDisableInternetError();
    }

    public void onDisableInternetError(){
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
        Log.i("FUCKYES","FUCK");
        splashScreen.animate().setDuration(200).alpha(0f).withEndAction((() -> applicationStarted()));
    }

    void disableSplashScreen(Callable<String> logs){

        if(splashScreen.getAlpha()==1){
            new Thread(){
                public void run(){
                    while (!status.isTorInitialized && !status.search_status.equals(constants.backendGenesis)){
                        try
                        {
                            sleep(1000);
                            logs.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    updateLogs("Starting | Genesis Search");
                    event.invokeObserver(Collections.singletonList(status.search_status), enums.home_eventType.on_url_load);
                }
            }.start();
        }
    }

    private void startPostTask(int m_id)
    {
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
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

        Log.i("Progress___",value+"");
        if(value==0)
        {
            progressReVerify(value,loading_status);
        }
        else if(splashScreen.getVisibility() == View.GONE)
        {
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
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            if(!loading_status){
                if(value==0 || value==100){
                    progressBar.setProgress(100);
                    progressBar.animate().alpha(0).withEndAction((() -> progressBar.setVisibility(View.GONE)));
                }
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