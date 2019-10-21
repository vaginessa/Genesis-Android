package com.darkweb.genesissearchengine.appManager.homeManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

public class launcherActivity extends AppCompatActivity
{
    boolean isStarted = false;

    /*Start Application*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invalid_setup_view);
        initPostUI();
        helperMethod.openActivity(homeController.class, constants.list_history, this,false);
    }

    /*Restart on Low Memory*/
    @Override
    public void onResume()
    {
        if(isStarted){
            helperMethod.openActivity(homeController.class, constants.list_history, this,false);
            pluginController.getInstance().logEvent(strings.app_restarted);
        }
        else {
            isStarted = true;
        }
        super.onResume();
    }

    /*Initialize Background*/
    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ease_blue));
        }
    }

}
