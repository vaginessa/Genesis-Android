package com.darkweb.genesissearchengine.appManager.homeManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.animatedColor;
import com.darkweb.genesissearchengine.helperManager.autoCompleteAdapter;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.mozilla.geckoview.GeckoView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

class homeViewController
{
    /*ViewControllers*/
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    /*ViewControllers*/
    private FrameLayout mWebviewContainer;
    private ProgressBar mProgressBar;
    private AutoCompleteTextView mSearchbar;
    private ConstraintLayout mSplashScreen;
    private ConstraintLayout mRequestFailure;
    private ImageView mLoading;
    private TextView mLoadingText;
    private AdView mBannerAds = null;
    private Handler mUpdateUIHandler = null;
    private ImageView mEngineLogo;
    private ImageButton mSwitchEngineBack;
    private ImageButton mGatewaySplash;
    private LinearLayout mTopBar;
    private GeckoView mGeckoView;
    private ImageView mBackSplash;
    private Button mConnectButton;

    /*Local Variables*/
    private ValueAnimator mEngineAnimator = null;
    private Handler mProgressHandler = null;
    private Callable<String> mLogs = null;

    void initialization(eventObserver.eventListener event,AppCompatActivity context, FrameLayout webviewContainer, TextView loadingText, ProgressBar progressBar, AutoCompleteTextView searchbar, ConstraintLayout splashScreen, ConstraintLayout requestFailure, FloatingActionButton floatingButton, ImageView loading, AdView banner_ads,ArrayList<String> suggestions,ImageView engineLogo,ImageButton gateway_splash,LinearLayout top_bar,GeckoView gecko_view,ImageView backsplash,boolean is_triggered,Button connect_button,ImageButton switch_engine_back){
        this.mContext = context;
        this.mProgressBar = progressBar;
        this.mSearchbar = searchbar;
        this.mSplashScreen = splashScreen;
        this.mRequestFailure = requestFailure;
        this.mLoading = loading;
        this.mLoadingText = loadingText;
        this.mWebviewContainer = webviewContainer;
        this.mBannerAds = banner_ads;
        this.mEvent = event;
        this.mEngineLogo = engineLogo;
        this.mGatewaySplash = gateway_splash;
        this.mTopBar = top_bar;
        this.mGeckoView = gecko_view;
        this.mBackSplash = backsplash;
        this.mConnectButton = connect_button;
        this.mSwitchEngineBack = switch_engine_back;

        initSplashScreen();
        initializeSuggestionView(suggestions);
        initLock();
        initSearchImage();
        createUpdateUiHandler();
        initSearchButtonAnimation(is_triggered);
        initTopBar();
    }

    private void initTopBar(){
        mWebviewContainer.setPadding(0,helperMethod.dpFromPx(mContext,234),0,0);
    }

    private void initSearchImage(){
        if(status.sSearchStatus.equals(constants.BACKEND_GENESIS_URL))
        {
            mEngineLogo.setImageResource(R.drawable.duck_logo);
        }
        else if(status.sSearchStatus.equals(constants.BACKEND_DUCK_DUCK_GO_URL))
        {
            mEngineLogo.setImageResource(R.drawable.google_logo);
        }
        else
        {
            mEngineLogo.setImageResource(R.drawable.genesis_logo);
        }
    }

    private void initSearchButtonAnimation(boolean is_triggered){
        if(!is_triggered){
            mEngineAnimator = ValueAnimator.ofFloat(0.3f, 1);
            mEngineAnimator.addUpdateListener(animation -> mSwitchEngineBack.setAlpha((Float) animation.getAnimatedValue()));
            mEngineAnimator.setDuration(1000);
            mEngineAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mEngineAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mEngineAnimator.start();
        }
    }

    void stopSearchButtonAnimation(){
        if(mEngineAnimator !=null){
            mEngineAnimator.end();
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            mEngineLogo.setBackgroundResource(outValue.resourceId);
            mEngineAnimator = null;
        }
    }

    private void initPostUI(boolean isSplash){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(isSplash){
                window.setStatusBarColor(mContext.getResources().getColor(R.color.ease_blue));
            }
            else{
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
                }
                else {
                    initStatusBarColor();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initStatusBarColor() {
        animatedColor oneToTwo = new animatedColor(ContextCompat.getColor(mContext, R.color.ease_blue), ContextCompat.getColor(mContext, R.color.black_blue));
        animatedColor twoToThree = new animatedColor(ContextCompat.getColor(mContext, R.color.black_blue), ContextCompat.getColor(mContext, R.color.black_blue_dark));
        animatedColor ThreeToFour = new animatedColor(ContextCompat.getColor(mContext, R.color.black_blue_dark), ContextCompat.getColor(mContext, R.color.white));

        ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1f).setDuration(68);
        animator.addUpdateListener(animation ->
        {
            float v = (float) animation.getAnimatedValue();
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
            mContext.getWindow().setStatusBarColor(oneToTwo.with(v));
            mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final ValueAnimator animator2 = ObjectAnimator.ofFloat(0f, 1f).setDuration(69);
                animator2.addUpdateListener(animation1 ->
                {
                    float v = (float) animation1.getAnimatedValue();
                    mContext.getWindow().setStatusBarColor(twoToThree.with(v));
                });
                animator2.start();

                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final ValueAnimator animator3 = ObjectAnimator.ofFloat(0f, 1f).setDuration(68);
                        animator3.addUpdateListener(animation1 ->
                        {
                            float v = (float) animation1.getAnimatedValue();
                            mContext.getWindow().setStatusBarColor(ThreeToFour.with(v));

                        });
                        animator3.start();
                    }
                });
                animator2.start();

            }
        });
        animator.start();

    }

    private void initLock(){
        Drawable img = mContext.getResources().getDrawable( R.drawable.icon_lock);
        mSearchbar.measure(0, 0);
        img.setBounds( 0, (int)(mSearchbar.getMeasuredHeight()*0.00), (int)(mSearchbar.getMeasuredHeight()*1.10), (int)(mSearchbar.getMeasuredHeight()*0.69) );
        mSearchbar.setCompoundDrawables( img, null, null, null );
    }

    void initializeSuggestionView(ArrayList<String> suggestions){
        autoCompleteAdapter suggestionAdapter = new autoCompleteAdapter(mContext, R.layout.hint_view, R.id.hintCompletionHeader, suggestions);

        int width = Math.round(helperMethod.screenWidth());
        mSearchbar.setThreshold(2);
        mSearchbar.setAdapter(suggestionAdapter);
        mSearchbar.setDropDownVerticalOffset(22);
        mSearchbar.setDropDownWidth(Math.round(width*0.95f));
        mSearchbar.setDropDownHorizontalOffset(Math.round(width*0.114f)*-1);

        Drawable drawable;
        Resources res = mContext.getResources();
        try {
            drawable = Drawable.createFromXml(res, res.getXml(R.xml.rouned_corner));
            mSearchbar.setDropDownBackgroundDrawable(drawable);
        } catch (Exception ignored) {
        }
    }

    private void initSplashLoading(){

        mLoading.setAnimation(helperMethod.getRotationAnimation());
        mLoading.setLayoutParams(helperMethod.getCenterScreenPoint(mLoading.getLayoutParams()));
        mLoading.setAnimation(helperMethod.getRotationAnimation());
        mLoading.setLayoutParams(helperMethod.getCenterScreenPoint(mLoading.getLayoutParams()));
        mLoadingText.setVisibility(View.VISIBLE);

        mConnectButton.setVisibility(View.GONE);
        mGatewaySplash.setVisibility(View.GONE);

    }

    void initHomePage(){
        mConnectButton.setClickable(false);
        mGatewaySplash.setClickable(false);

        mConnectButton.animate().setDuration(300).alpha(0f).withEndAction((() -> initSplashLoading()));
        mGatewaySplash.animate().setDuration(300).alpha(0f);
    }

    private void initSplashScreen(){
        mSearchbar.setEnabled(false);

        initPostUI(true);

        mBackSplash.getLayoutParams().height = helperMethod.getScreenHeight(mContext) - helperMethod.getStatusBarHeight(mContext)*2;
        mSearchbar.setEnabled(false);

        View root = mSearchbar.getRootView();
        root.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_purple));

    }

    void initProxyLoading(Callable<String> logs){
        this.mLogs = logs;

        if(mSplashScreen.getAlpha()==1){
            new Thread(){
                public void run(){

                    AppCompatActivity temp_context = mContext;
                    while (!status.sIsTorInitialized && (!status.sSearchStatus.equals(constants.BACKEND_GENESIS_URL) || status.sGateway)){
                        try
                        {
                            sleep(1000);
                            mEvent.invokeObserver(Collections.singletonList(status.sSearchStatus), enums.etype.recheck_orbot);
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
            }.start();
        }
    }

    /*-------------------------------------------------------PAGE UI Methods-------------------------------------------------------*/

    void onPageFinished(){
        mSearchbar.setEnabled(true);
        mProgressBar.bringToFront();

        if(mSplashScreen.getVisibility()!=View.GONE){
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);//Set Portrait
            splashScreenDisable();
        }
        splashScreenDisable();
        onInternetErrorUpdate(false);
    }
    void onInternetErrorUpdate(Boolean status){
        if(!status){
            mRequestFailure.animate().alpha(0f).setDuration(150).withEndAction((() -> mRequestFailure.setVisibility(View.GONE)));
        }
        else {
            mRequestFailure.setVisibility(View.VISIBLE);
            mRequestFailure.animate().alpha(1f).setDuration(150);
            onProgressBarUpdate(0,false);
            onClearSelections(true);

            splashScreenDisable();
        }
    }
    private void splashScreenDisable(){
        mTopBar.setAlpha(1);
        if(mSplashScreen.getVisibility()!=View.GONE)
        {
            mSplashScreen.animate().setDuration(195).alpha(0).withEndAction((() -> mSplashScreen.setVisibility(View.GONE)));
            mEvent.invokeObserver(null, enums.etype.on_init_ads);
            initPostUI(false);
        }
    }

    /*-------------------------------------------------------Helper Methods-------------------------------------------------------*/

    void onOpenMenu(View view){
        view.bringToFront();
        LinearLayout parentView = (LinearLayout)view.getParent();

        PopupMenu popup = new PopupMenu(mContext, parentView, Gravity.RIGHT);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);
        popup.setOnMenuItemClickListener(item ->
        {
            mEvent.invokeObserver(Collections.singletonList(item.getItemId()), enums.etype.onMenuSelected);
            return true;
        });

        popup.show();
        view.bringToFront();
    }
    void onSetBannerAdMargin(){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int padding_102dp = (int) (48 * scale + 0.52f);

        mWebviewContainer.setPadding(0,padding_102dp+helperMethod.dpFromPx(mContext,234),0,0);

        mBannerAds.setAlpha(0f);
        mBannerAds.animate().setDuration(1000).alpha(1f);
    }
    void onUpdateSearchBar(String url){
        if (mSearchbar == null)
        {
            return;
        }

        url = url.replace("boogle.store","genesis.onion");
        boolean isTextSelected = false;

        if(mSearchbar.isSelected()){
            isTextSelected = true;
        }
        mSearchbar.setText(url);
        mSearchbar.selectAll();

        if(isTextSelected){
            mSearchbar.selectAll();
        }

        mSearchbar.setSelection(0);
    }

    public void updateSearchPosition(){
        mSearchbar.setSelection(0);
    }

    void onNewTab(boolean keyboard){
        mSearchbar.setText("about:blank");
        if(keyboard){
            mSearchbar.requestFocus();
            mSearchbar.selectAll();
            InputMethodManager imm = (InputMethodManager)   mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
    void onUpdateLogs(String log){
        mLoadingText.setText(log);
    }
    void progressBarReset(){
        mProgressBar.setProgress(0);
    }
    void onProgressBarUpdate(int value,boolean loading_status){

        if(value==0)
        {
            progressReVerify(value,loading_status);
        }
        else if(mSplashScreen.getVisibility() == View.GONE)
        {
            status.sIsAppStarted = true;

            if(mProgressBar.getVisibility()==View.INVISIBLE)
            {
                mProgressBar.setProgress(10);
            }
            else
            {
                if(value==100){
                    progressReVerify(value,loading_status);
                }else {
                    mProgressBar.setProgress(value);
                }
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setAlpha(1);
        }
        else {
            mProgressBar.setProgress(0);
            helperMethod.hideKeyboard(mContext);
        }
    }
    private void progressReVerify(int value,boolean loading_status){
        if(mProgressHandler !=null){
            mProgressHandler.removeCallbacksAndMessages(null);
        }
        mProgressHandler = new Handler();
        mProgressHandler.postDelayed(() ->
        {
            if(!loading_status || value==100){
                mProgressBar.setProgress(100);
                mProgressBar.animate().alpha(0).withEndAction((() -> mProgressBar.setVisibility(View.GONE)));
                helperMethod.hideKeyboard(mContext);
            }
        }, 100);
    }
    void onClearSelections(boolean hideKeyboard){
        mSearchbar.clearFocus();
        if(hideKeyboard){
            helperMethod.hideKeyboard(mContext);
        }
    }

    void onFullScreenUpdate(boolean status){
        int value = !status ? 1 : 0;

        mTopBar.setClickable(!status);
        mTopBar.setAlpha(value);

        if(status){
            mWebviewContainer.setPadding(0,0,0,0);
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            mContext.getWindow().getDecorView().setSystemUiVisibility(flags);
            mProgressBar.setVisibility(View.GONE);
        }
        else {
            mWebviewContainer.setPadding(0,helperMethod.dpFromPx(mContext,234),0,0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                mContext.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }

            mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mProgressBar.setVisibility(View.VISIBLE);
        }

    }

    void onReDraw(){
        if(mWebviewContainer.getPaddingBottom()==0){
            mWebviewContainer.setPadding(0,0,0,1);
        }
        else {
            mWebviewContainer.setPadding(0,0,0,0);
        }
    }
    void onUpdateLogo(){

        switch (status.sSearchStatus)
        {
            case constants.BACKEND_GOOGLE_URL:
                mEngineLogo.setImageResource(R.drawable.genesis_logo);
                break;
            case constants.BACKEND_GENESIS_URL:
                mEngineLogo.setImageResource(R.drawable.duck_logo);
                break;
            case constants.BACKEND_DUCK_DUCK_GO_URL:
                mEngineLogo.setImageResource(R.drawable.google_logo);
                break;
        }
    }

    /*-------------------------------------------------------POST UI TASK HANDLER-------------------------------------------------------*/

    private void startPostTask(int m_id) {
        Message message = new Message();
        message.what = m_id;
        mUpdateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler(){
        mUpdateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.ON_URL_LOAD)
                {
                    mEvent.invokeObserver(Collections.singletonList(status.sSearchStatus), enums.etype.on_url_load);
                }
                if(msg.what == messages.UPDATE_LOADING_TEXT)
                {
                    if(mLogs !=null)
                    {
                        try
                        {
                            mLogs.call();
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


}