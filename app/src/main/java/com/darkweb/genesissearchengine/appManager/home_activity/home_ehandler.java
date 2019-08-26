package com.darkweb.genesissearchengine.appManager.home_activity;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import com.darkweb.genesissearchengine.appManager.list_manager.list_controller;
import com.darkweb.genesissearchengine.appManager.settingManager.settingController;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.preferenceController;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;

import java.io.IOException;
import java.net.URL;

public class home_ehandler
{
    homeController appContoller;

    public home_ehandler()
    {
        appContoller = homeModel.getInstance().getHomeInstance();
    }

    public boolean onEditorClicked(TextView v, int actionId, KeyEvent event)
    {

        if (actionId != EditorInfo.IME_ACTION_NEXT)
        {
            return false;
        }

        helperMethod.hideKeyboard(homeModel.getInstance().getHomeInstance());
        String url = helperMethod.completeURL(v.getText().toString());
        try
        {
            URL host = new URL(url);
            boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

            if(isUrlValid && host.getHost().replace("www.","").contains("."))
            {
                if(host.getHost().contains(constants.backendUrlHost) || host.getHost().contains(constants.frontEndUrlHost) || host.getHost().contains(constants.frontEndUrlHost_v1))
                {
                    appContoller.onloadURL(url.replace(constants.frontEndUrlHost_v1,constants.backendUrlHost),false,true,true);
                    return true;
                }
                else
                {
                    homeModel.getInstance().getHomeInstance().onloadURL(url,true,true,true);
                    return true;
                }
            }
        }

        catch (IOException e){}

        String editedURL = getSearchEngine(v.getText().toString().replaceAll(" ","+"));
        homeModel.getInstance().addHistory(editedURL);
        appContoller.onloadURL(editedURL,false,true,true);
        appContoller.onClearSearchBarCursorView();

        return true;
    }

    public String getSearchEngine(String query)
    {
        if(status.search_status.equals("Hidden Web"))
        {
            return  "https://boogle.store/search?q="+query+"&p_num=1&s_type=all&savesearch=on";
        }
        else if(status.search_status.equals(enums.searchEngine.Google.toString()))
        {
            return "https://www.google.com/search?source=hp&q="+query;
        }
        else
        {
            return "https://www.bing.com/search?q="+query;
        }

    }

    public void onReloadButtonPressed(View view)
    {
        appContoller.onReload();
    }

    void onMenuButtonPressed(View view)
    {
        appContoller.openMenu(view);
    }

    void onHomeButtonPressed()
    {
        appContoller.stopHiddenView(true,true);
        viewController.getInstance().checkSSLTextColor();
        appContoller.initSearchEngine();
        helperMethod.hideKeyboard(homeModel.getInstance().getHomeInstance());
    }

    void onFloatingButtonPressed()
    {
        pluginController.getInstance().MessageManagerHandler(null,enums.popup_type.report_url);
    }

    void onBackPressed()
    {
        appContoller.onBackPressedView();
    }

    void onMenuPressed(int menuId)
    {
        if (menuId == R.id.menu1) {
            helperMethod.openActivity(list_controller.class,constants.list_history, homeModel.getInstance().getHomeInstance());
        }

        else if (menuId == R.id.menu2) {
            switchGateway();
        }

        else if (menuId == R.id.menu3) {
            helperMethod.openActivity(settingController.class,constants.list_history, homeModel.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu4)
        {
            pluginController.getInstance().MessageManagerHandler(homeModel.getInstance().getHomeInstance().getSearchBarUrl(),enums.popup_type.bookmark);
        }
        else if (menuId == R.id.menu5)
        {
            helperMethod.openActivity(list_controller.class,constants.list_bookmark, homeModel.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu6)
        {
            pluginController.getInstance().MessageManagerHandler(null,enums.popup_type.report_url);
        }
        else if (menuId == R.id.menu7)
        {
            helperMethod.rateApp(homeModel.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu8)
        {
            helperMethod.shareApp(homeModel.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu0)
        {
            helperMethod.openDownloadFolder(homeModel.getInstance().getHomeInstance());
        }/*
        else if (menuId == R.id.menu9)
        {
            helperMethod.openActivity(settingController.class,constants.list_history);
        }
        else if (menuId == R.id.menu11)
        {
            homeModel.getInstance().getHomeInstance().onloadURL("https://whatismycountry.com/",true,true,false);
        }*/

    }

    public void switchGateway()
    {
        if(status.gateway == false)
        {
            status.gateway = true;
            pluginController.getInstance().proxyManager(true);
            preferenceController.getInstance().setBool(keys.gateway,true);
        }
        else
        {
            status.gateway = false;
            pluginController.getInstance().proxyManager(false);
            preferenceController.getInstance().setBool(keys.gateway,false);
        }
    }

    public void switchSearchEngine(View view)
    {
        // settingModel.getInstance().search_status = "Google";
        preferenceController.getInstance().setString(keys.search_engine, "Google");


        if(status.search_status.equals("Google"))
        {
            appContoller.stopHiddenView(true,true);
            preferenceController.getInstance().setString(keys.search_engine, strings.darkweb);
            status.search_status = enums.searchEngine.HiddenWeb.toString();
            homeModel.getInstance().getHomeInstance().initSearchEngine();
            ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            ((ImageButton) view).setImageResource(R.drawable.google_logo);
        }
        else
        {
            if(pluginController.getInstance().OrbotManagerInit())
            {
                preferenceController.getInstance().setString(keys.search_engine,"Google");
                status.search_status = "Google";
                homeModel.getInstance().getHomeInstance().initSearchEngine();
                ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            }
            else {
                pluginController.getInstance().MessageManagerHandler(null, enums.popup_type.start_orbot);
            }
        }
    }
}
