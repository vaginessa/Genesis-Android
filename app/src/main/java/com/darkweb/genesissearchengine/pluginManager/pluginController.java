package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.appManager.list_manager.list_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preference_manager;

public class pluginController
{
    /*Private Variables*/

    private static final pluginController ourInstance = new pluginController();
    private homeController home_controller;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }
    private pluginController()
    {
        home_controller = home_model.getInstance().getHomeInstance();
    }

    /*Helper Method*/

    void onBannerAdLoaded()
    {
        home_controller.onBannerAdLoaded();
    }

    AppCompatActivity getAppContext()
    {
        return home_model.getInstance().getHomeInstance();
    }

    /*Exit Manager*/

    void stopAllServices(){
        preference_manager.getInstance().setBool(keys.low_memory,false);
        proxyManager.getInstance().disconnectConnection();
    }

    /*Analytics Manager*/

    void initializeAnalyticsManager(){
        analyticManager.getInstance().initialize();
        analyticManager.getInstance().logUser();
    }

    /*Proxy Manager*/

    void initBoogle(){
        if(status.search_status.equals(strings.darkweb))
        {
            home_model.getInstance().getHomeInstance().initBoogle();
        }
    }

    /*Onion Proxy Manager*/

    void setPort(int port){
        home_model.getInstance().setPort(port);
    }

    int getPort(){
        return home_model.getInstance().getPort();
    }

    /*Message Manager*/

    void onLoadURL(String url){
        home_controller.onloadURL(url,false,true,false);
    }

    void setWelcomeCancelPreference(){
        preference_manager.getInstance().setBool(keys.first_time_loaded,true);
    }

    void onReload(){
        home_controller.onReload();
    }

    void onClearHistory(){
        list_model.getInstance().getListInstance().onClearAll();
    }

}
