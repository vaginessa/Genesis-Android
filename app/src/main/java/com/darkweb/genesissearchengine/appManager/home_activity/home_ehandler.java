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
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.fabricManager;
import com.darkweb.genesissearchengine.pluginManager.message_manager;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import com.darkweb.genesissearchengine.pluginManager.proxy_controller;
import com.example.myapplication.R;

import java.io.IOException;
import java.net.URL;

public class home_ehandler
{
    home_controller appContoller;

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

        helperMethod.hideKeyboard();
        String url = helperMethod.completeURL(v.getText().toString());
        try
        {
            URL host = new URL(url);
            boolean isUrlValid = Patterns.WEB_URL.matcher(url).matches();

            if(isUrlValid && host.getHost().replace("www.","").contains("."))
            {
                if(host.getHost().contains(constants.backendUrlHost) || host.getHost().contains(constants.frontEndUrlHost) || host.getHost().contains(constants.frontEndUrlHost_v1))
                {
                    fabricManager.getInstance().sendEvent("EDITOR BASE URL REQUEST : " + url);
                    appContoller.onloadURL(url.replace(constants.frontEndUrlHost_v1,constants.backendUrlHost),false,true,true);
                    return true;
                }
                else
                {
                    fabricManager.getInstance().sendEvent("EDITOR ONION URL REQUEST : " + url);
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
        fabricManager.getInstance().sendEvent("EDITOR SEARCHED : " + editedURL);

        return true;
    }

    public String getSearchEngine(String query)
    {
        if(status.search_status.equals(enums.searchEngine.HiddenWeb.toString()))
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
        fabricManager.getInstance().sendEvent("RELOAD BUTTON PRESSSED : ");
        appContoller.onReload();
    }

    void onMenuButtonPressed(View view)
    {
        appContoller.openMenu(view);
    }

    void onHomeButtonPressed()
    {
        appContoller.stopHiddenView(true,true);
        fabricManager.getInstance().sendEvent("HOME BUTTON PRESSSED : ");
        viewController.getInstance().checkSSLTextColor();
        appContoller.initSearchEngine();
        helperMethod.hideKeyboard();
    }

    void onFloatingButtonPressed()
    {
        fabricManager.getInstance().sendEvent("FLOATING BUTTON PRESSSED : ");
        message_manager.getInstance().reportURL();
    }

    void onBackPressed()
    {
        fabricManager.getInstance().sendEvent("BACK BUTTON PRESSSED : ");
        appContoller.onBackPressedView();
    }

    void onMenuPressed(int menuId)
    {
        if (menuId == R.id.menu1) {
            helperMethod.openActivity(list_controller.class,constants.list_history);
        }

        else if (menuId == R.id.menu2) {
            switchGateway();
        }

        else if (menuId == R.id.menu3) {
            helperMethod.openActivity(setting_controller.class,constants.list_history);
        }
        else if (menuId == R.id.menu4)
        {
            message_manager.getInstance().bookmark(home_model.getInstance().getHomeInstance().getSearchBarUrl());
        }
        else if (menuId == R.id.menu5)
        {
            helperMethod.openActivity(list_controller.class,constants.list_bookmark);
        }
        else if (menuId == R.id.menu6)
        {
            message_manager.getInstance().reportURL();
        }
        else if (menuId == R.id.menu7)
        {
            helperMethod.rateApp();
        }
        else if (menuId == R.id.menu8)
        {
            helperMethod.shareApp();
        }
        else if (menuId == R.id.menu0)
        {
            helperMethod.openDownloadFolder();
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
            proxy_controller.getInstance().startVPNConnection();
            preference_manager.getInstance().setBool(keys.gateway,true);
        }
        else
        {
            status.gateway = false;
            proxy_controller.getInstance().disconnectConnection();
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
            preference_manager.getInstance().setString(keys.search_engine,enums.searchEngine.HiddenWeb.toString());
            status.search_status = enums.searchEngine.HiddenWeb.toString();
            home_model.getInstance().getHomeInstance().initSearchEngine();
            ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            ((ImageButton) view).setImageResource(R.drawable.google_logo);
        }
        else
        {
            if(orbot_manager.getInstance().initOrbot("https://google.com"))
            {
                preference_manager.getInstance().setString(keys.search_engine,"Google");
                status.search_status = "Google";
                home_model.getInstance().getHomeInstance().initSearchEngine();
                ((ImageButton) view).setImageResource(R.drawable.genesis_logo);
            }
        }

    }



}
