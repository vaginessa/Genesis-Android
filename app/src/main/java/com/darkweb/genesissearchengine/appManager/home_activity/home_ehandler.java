package com.darkweb.genesissearchengine.appManager.home_activity;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import com.darkweb.genesissearchengine.appManager.list_manager.list_controller;
import com.darkweb.genesissearchengine.appManager.setting_manager.setting_controller;
import com.darkweb.genesissearchengine.appManager.setting_manager.setting_model;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.messageManager;
import com.darkweb.genesissearchengine.pluginManager.orbotManager;
import com.darkweb.genesissearchengine.pluginManager.proxyManager;
import com.example.myapplication.R;

import java.io.IOException;
import java.net.URL;

public class home_ehandler
{
    homeController appContoller;

    public home_ehandler()
    {
        appContoller = home_model.getInstance().getHomeInstance();
    }

    public boolean onEditorClicked(TextView v, int actionId, KeyEvent event)
    {

        if (actionId != EditorInfo.IME_ACTION_NEXT)
        {
            return false;
        }

        helperMethod.hideKeyboard(home_model.getInstance().getHomeInstance());
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
                    home_model.getInstance().getHomeInstance().onloadURL(url,true,true,true);
                    return true;
                }
            }
        }

        catch (IOException e){}

        String editedURL = getSearchEngine(v.getText().toString().replaceAll(" ","+"));
        home_model.getInstance().addHistory(editedURL);
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
        helperMethod.hideKeyboard(home_model.getInstance().getHomeInstance());
    }

    void onFloatingButtonPressed()
    {
        messageManager.getInstance().createMessage(enums.popup_type.report_url);
    }

    void onBackPressed()
    {
        appContoller.onBackPressedView();
    }

    void onMenuPressed(int menuId)
    {
        if (menuId == R.id.menu1) {
            helperMethod.openActivity(list_controller.class,constants.list_history,home_model.getInstance().getHomeInstance());
        }

        else if (menuId == R.id.menu2) {
            switchGateway();
        }

        else if (menuId == R.id.menu3) {
            helperMethod.openActivity(setting_controller.class,constants.list_history,home_model.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu4)
        {
            messageManager.getInstance().setData(home_model.getInstance().getHomeInstance().getSearchBarUrl());
            messageManager.getInstance().createMessage(enums.popup_type.bookmark);
        }
        else if (menuId == R.id.menu5)
        {
            helperMethod.openActivity(list_controller.class,constants.list_bookmark,home_model.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu6)
        {
            messageManager.getInstance().createMessage(enums.popup_type.report_url);
        }
        else if (menuId == R.id.menu7)
        {
            helperMethod.rateApp(home_model.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu8)
        {
            helperMethod.shareApp(home_model.getInstance().getHomeInstance());
        }
        else if (menuId == R.id.menu0)
        {
            helperMethod.openDownloadFolder(home_model.getInstance().getHomeInstance());
        }/*
        else if (menuId == R.id.menu9)
        {
            helperMethod.openActivity(setting_controller.class,constants.list_history);
        }
        else if (menuId == R.id.menu11)
        {
            home_model.getInstance().getHomeInstance().onloadURL("https://whatismycountry.com/",true,true,false);
        }*/

    }

    public void switchGateway()
    {
        if(status.gateway == false)
        {
            status.gateway = true;
            proxyManager.getInstance().startVPNConnection();
            preference_manager.getInstance().setBool(keys.gateway,true);
        }
        else
        {
            status.gateway = false;
            proxyManager.getInstance().disconnectConnection();
            preference_manager.getInstance().setBool(keys.gateway,false);
        }
    }

    public void switchSearchEngine(View view)
    {
        setting_model.getInstance().search_status = "Google";
        preference_manager.getInstance().setString(keys.search_engine, setting_model.getInstance().search_status);


        if(status.search_status.equals("Google"))
        {
            appContoller.stopHiddenView(true,true);
            preference_manager.getInstance().setString(keys.search_engine, strings.darkweb);
            status.search_status = enums.searchEngine.HiddenWeb.toString();
            home_model.getInstance().getHomeInstance().initSearchEngine();
            ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            ((ImageButton) view).setImageResource(R.drawable.google_logo);
        }
        else
        {
            if(orbotManager.getInstance().initOrbot("https://google.com"))
            {
                preference_manager.getInstance().setString(keys.search_engine,"Google");
                status.search_status = "Google";
                home_model.getInstance().getHomeInstance().initSearchEngine();
                ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            }
        }

    }



}
