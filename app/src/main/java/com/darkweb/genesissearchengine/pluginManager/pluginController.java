package com.darkweb.genesissearchengine.pluginManager;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.dataController;

public class pluginController
{
    /*Plugin Instance*/

    private adManager ad_manager;
    private analyticManager analytic_manager;
    private exitManager exit_manager;
    private fabricManager fabric_manager;
    private firebaseManager firebase_manager;
    private localNotification local_notification;
    private messageManager message_manager;
    //private orbotManager orbot_manager;
    //private proxyManager proxy_manager;
    private activityContextManager contextManager;
    private boolean is_initialized = false;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private homeController home_controller;
    private historyController history_controller;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }
    private pluginController()
    {
    }

    public void reset(){
        //orbotManager.getInstance().onReset();
        proxyManager.getInstance().disconnectConnection();
    }

    public void initialize(){
        instanceObjectInitialization();
        is_initialized = true;
    }

    private void instanceObjectInitialization()
    {
        home_controller = activityContextManager.getInstance().getHomeController();
        contextManager = activityContextManager.getInstance();

        ad_manager = new adManager(getAppContext(),new admobCallback(),home_controller.getBannerAd());
        analytic_manager = new analyticManager(getAppContext(),new analyticCallback());
        getAppContext().startService(new Intent(getAppContext(), exitManager.class));
        fabric_manager = new fabricManager(getAppContext(),new fabricCallback());
        firebase_manager = new firebaseManager(getAppContext(),new firebaseCallback());
        local_notification = new localNotification(getAppContext(),new notificationCallback());
        message_manager = new messageManager(new messageCallback());
    }

    public void initializeAllProxies(AppCompatActivity context){
        //orbot_manager = orbotManager.getInstance();
        //proxy_manager = proxyManager.getInstance();
        orbotManager.getInstance().initialize(context,new orbotCallback());
        proxyManager.getInstance().initialize(context,new proxyCallback());
    }

    /*Helper Methods*/

    private AppCompatActivity getAppContext()
    {
        return home_controller;
    }

    public boolean isInitialized(){
        return is_initialized;
    }

    /*---------------------------------------------- EXTERNAL REQUEST LISTENER-------------------------------------------------------*/

    /*Message Manager*/
    public void MessageManagerHandler(AppCompatActivity app_context,String data,enums.popup_type type){
        message_manager.createMessage(app_context,data,type);
    }

    /*Proxy Manager*/
    public void proxyManagerInvoke(boolean status){
        Log.i("SUPFUCK","SUP2");
        if(status){
            proxyManager.getInstance().startVPN();
        }
        else{
            proxyManager.getInstance().disconnectConnection();
        }
    }

    public void onPause(){
        message_manager.onReset();
    }

    void proxyManagerExitInvoke(){
        proxyManager.getInstance().disconnectConnection();
        //orbotManager.getInstance().onReset();
    }

    public boolean proxyStatus(){
        return proxyManager.getInstance().isProxyRunning();
    }

    /*Notification Manager*/
    public void createNotification(String title,String message){
        local_notification.createNotification(title,message);
    }

    /*Ad Manager*/
    public void

    initializeBannerAds(){
        if(ad_manager!=null){
            ad_manager.initializeBannerAds();
        }
    }

    /*Onion Proxy Manager*/
    public boolean OrbotManagerInit(){
        return orbotManager.getInstance().isOrbotRunning(false);
    }
    public void setProxy(boolean status){
        orbotManager.getInstance().setProxy(status);
    }
    public String orbotLogs(){
        return orbotManager.getInstance().getLogs();
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            home_controller.onBannerAdLoaded();
        }
    }

    /*Analytics Manager*/
    private class analyticCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            analytic_manager.logUser();
        }
    }

    /*Fabric Manager*/
    private class fabricCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
        }
    }

    /*Firebase Manager*/
    private class firebaseCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
        }
    }

    /*Notification Manager*/
    private class notificationCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
        }
    }

    /*Proxy Manager*/
    private class proxyCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            if(e_type.equals(enums.eventType.disable_splash))
            {
                if(home_controller!=null){
                    activityContextManager.getInstance().getHomeController().disableSplash();
                }
            }
        }
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {

        }
    }

    /*Message Manager*/
    private class messageCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            if(e_type.equals(enums.eventType.welcome))
            {
                home_controller.loadURL(data.toString());
            }
            else if(e_type.equals(enums.eventType.cancel_welcome)){
                dataController.getInstance().setBool(keys.is_welcome_enabled,false);
            }
            else if(e_type.equals(enums.eventType.reload)){
                if(orbotManager.getInstance().isOrbotRunning(false))
                {
                    home_controller.loadURL(data.toString());
                }
                else {
                    message_manager.createMessage(home_controller,data.toString(),enums.popup_type.start_orbot);
                }
            }
            else if(e_type.equals(enums.eventType.clear_history)){
                dataController.getInstance().clearHistory();
                contextManager.getHistoryController().onclearData();
            }
            else if(e_type.equals(enums.eventType.clear_bookmark)){
                dataController.getInstance().clearBookmark();
                contextManager.getBookmarkController().onclearData();
            }
            else if(e_type.equals(enums.eventType.bookmark)){
                String [] dataParser = data.toString().split("split");
                if(dataParser.length>1){
                    dataController.getInstance().addBookmark(dataParser[0],dataParser[1]);
                }else {
                    dataController.getInstance().addBookmark(dataParser[0],"");
                }
            }
            else if(e_type.equals(enums.eventType.app_rated)){
                dataController.getInstance().setBool(keys.isAppRated,true);
            }
            else if(e_type.equals(enums.eventType.download_file)){
                home_controller.onDownloadFile();
            }
            else if(e_type.equals(enums.eventType.connect_vpn)){
                boolean status = (boolean)data;
                home_controller.startGateway(status);
            }
            else if(e_type.equals(enums.eventType.start_home)){
                home_controller.disableSplash();
            }
        }
    }

}
