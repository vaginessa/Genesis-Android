package com.darkweb.genesissearchengine;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.example.myapplication.BuildConfig;

import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.crashlytics.android.answers.Answers.TAG;

public class helperMethod
{
    /*Helper Methods General*/

    public static boolean isNetworkAvailable(AppCompatActivity context){
        ConnectivityManager cm = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String completeURL(String url){
        if(!url.startsWith("www.")&& !url.startsWith("http://")&& !url.startsWith("https://")){
            url = "www."+url;
        }
        if(!url.startsWith("http://")&&!url.startsWith("https://")){
            url = "http://"+url;
        }
        return url;
    }

    public static SpannableString urlDesigner(String url){
        if (url.contains("https://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 123, 43)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else if (url.contains("http://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(Color.argb(255, 0, 128, 43)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        }
    }

    public static void hideKeyboard(AppCompatActivity context) {
        View view = context.findViewById(android.R.id.content);
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void rateApp(AppCompatActivity context){
        dataController.getInstance().setBool(keys.isAppRated,true);
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine")));
    }

    public static void shareApp(AppCompatActivity context) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out this Awesome App")
                .setSubject("Hi! Check out this Awesome App")
                .setText("Genesis | Onion Search | http://play.google.com/store/apps/details?id=" + context.getPackageName())
                .startChooser();
    }

    public static void openDownloadFolder(AppCompatActivity context)
    {
        context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }


    public static String getHost(String link){
        URL url;
        try
        {
            url = new URL(link);
            return url.getHost();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "";
        }

    }

    public static void openActivity( Class<?> cls,int type,AppCompatActivity context,boolean animation){
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.list_type, type);
        if(!animation){
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(myIntent);
    }

    public static void onMinimizeApp(AppCompatActivity context){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public static void showToast(String messaage,AppCompatActivity context)
    {
        Toast.makeText(context.getApplicationContext(),messaage,Toast.LENGTH_SHORT).show();
    }

    /*Helper Methods Splash Screen*/

    public static int screenHeight(boolean hasSoftKeys,AppCompatActivity context) {
        if(!hasSoftKeys)
        {
            return Resources.getSystem().getDisplayMetrics().heightPixels -(helperMethod.getNavigationBarHeight(context));
        }
        else
        {
            return (Resources.getSystem().getDisplayMetrics().heightPixels);
        }
    }

    public static int screenWidth()
    {
        return (Resources.getSystem().getDisplayMetrics().widthPixels);
    }

    private static int getNavigationBarHeight(AppCompatActivity context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static RotateAnimation getRotationAnimation(){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;
    }

    public static ViewGroup.MarginLayoutParams getCenterScreenPoint(ViewGroup.LayoutParams itemLayoutParams) {
        double heightloader = Resources.getSystem().getDisplayMetrics().heightPixels*0.78;
        ViewGroup.MarginLayoutParams params_loading = (ViewGroup.MarginLayoutParams) itemLayoutParams;
        params_loading.topMargin = (int)(heightloader);

        return params_loading;
    }

    public static boolean hasSoftKeys(WindowManager windowManager){
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static boolean isBuildValid (){
        return BuildConfig.FLAVOR.equals("aarch64") && Build.SUPPORTED_ABIS[0].equals("arm64-v8a") || BuildConfig.FLAVOR.equals("arm") && Build.SUPPORTED_ABIS[0].equals("armeabi-v7a") || BuildConfig.FLAVOR.equals("x86") && Build.SUPPORTED_ABIS[0].equals("x86") || BuildConfig.FLAVOR.equals("x86_64") && Build.SUPPORTED_ABIS[0].equals("x86_64");
    }

    public static void openPlayStore(String packageName,AppCompatActivity context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+packageName));
        context.startActivity(intent);
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void setTimePickerTime(final TimePicker picker, final Calendar cal) {
        if (Build.VERSION.SDK_INT >= 23) {
            picker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            picker.setMinute(cal.get(Calendar.MINUTE));
        } else {
            picker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            picker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }
    }

    public static Date parseDate(final SimpleDateFormat formatter,
                                  final String value,
                                  final boolean defaultToNow) {
        try {
            if (value != null && !value.isEmpty()) {
                return formatter.parse(value);
            }
        } catch (final ParseException e) {
        }
        return defaultToNow ? new Date() : null;
    }

    public static AlertDialog createStandardDialog(final AlertDialog.Builder builder,
                                             final GeckoSession.PromptDelegate.BasePrompt prompt,
                                             final GeckoResult<GeckoSession.PromptDelegate.PromptResponse> response) {
        final AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                if (!prompt.isComplete()) {
                    response.complete(prompt.dismiss());
                }
            }
        });
        return dialog;
    }

    public static void setCalendarTime(final Calendar cal, final TimePicker picker) {
        if (Build.VERSION.SDK_INT >= 23) {
            cal.set(Calendar.HOUR_OF_DAY, picker.getHour());
            cal.set(Calendar.MINUTE, picker.getMinute());
        } else {
            cal.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
            cal.set(Calendar.MINUTE, picker.getCurrentMinute());
        }
    }

    public static LinearLayout addStandardLayout(final AlertDialog.Builder builder,
                                           final String title, final String msg) {
        final ScrollView scrollView = new ScrollView(builder.getContext());
        final LinearLayout container = new LinearLayout(builder.getContext());
        final int horizontalPadding = getViewPadding(builder);
        final int verticalPadding = (msg == null || msg.isEmpty()) ? horizontalPadding : 0;
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(/* left */ horizontalPadding, /* top */ verticalPadding,
                /* right */ horizontalPadding, /* bottom */ verticalPadding);
        scrollView.addView(container);
        builder.setTitle(title)
                .setMessage(msg)
                .setView(scrollView);
        return container;
    }

    public static int parseColor(final String value, final int def) {
        try {
            return Color.parseColor(value);
        } catch (final IllegalArgumentException e) {
            return def;
        }
    }

    private static int getViewPadding(final AlertDialog.Builder builder) {
        final TypedArray attr = builder.getContext().obtainStyledAttributes(
                new int[] { android.R.attr.listPreferredItemPaddingLeft });
        final int padding = attr.getDimensionPixelSize(0, 1);
        attr.recycle();
        return padding;
    }
}
