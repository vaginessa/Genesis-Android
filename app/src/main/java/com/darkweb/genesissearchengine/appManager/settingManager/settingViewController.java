package com.darkweb.genesissearchengine.appManager.settingManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

class settingViewController
{
    /*Private Variables*/

    private eventObserver.eventListener event;
    private AppCompatActivity context;

    private Spinner search;
    private Spinner javascript;
    private Spinner history;

    /*Initializations*/

    settingViewController(Spinner search, Spinner javascript, Spinner history, settingController app_context, String currentSearchEngine, eventObserver.eventListener event,AppCompatActivity context)
    {
        this.search = search;
        this.javascript = javascript;
        this.history = history;
        this.event = event;
        this.context = context;

        initViews();
        initJavascript();
        initHistory();
        initSearchEngine();
        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(context.getResources().getColor(R.color.blue_dark));
            }
            else {
                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                context.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }

    private void initViews()
    {
        search.setDropDownVerticalOffset(15);
        search.setDropDownHorizontalOffset(-15);
        javascript.setDropDownVerticalOffset(15);
        javascript.setDropDownHorizontalOffset(-15);
        history.setDropDownVerticalOffset(15);
        history.setDropDownHorizontalOffset(-15);
    }

    private void initJavascript()
    {
        if (java_status)
        {
            javascript.setSelection(0);
        }
        else
        {
            javascript.setSelection(1);
        }
    }

    private void initHistory()
    {
        if (history_status)
        {
            history.setSelection(0);
        }
        else
        {
            history.setSelection(1);
        }
    }

    @SuppressWarnings("unchecked")
    private void initSearchEngine()
    {
        search.setSelection(getEngineIndex());
    }

    /*External Helper Methods*/

    private int getEngineIndex(){
        if(status.search_status.equals(constants.backendGenesis)){
            return 0;
        }
        else if(status.search_status.equals(constants.backendGoogle)){
            return 1;
        }
        else
            return 2;
    }
}
