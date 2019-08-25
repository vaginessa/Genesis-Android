package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.appManager.list_manager.list_model;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preferenceController;

@SuppressWarnings("ALL")
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
    private orbotManager orbot_manager;
    private proxyManager proxy_manager;

    /*Private Variables*/

    private static final pluginController ourInstance = new pluginController();
    private homeController home_controller;
    private homeModel home_model;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }
    private pluginController()
    {
        home_controller = homeModel.getInstance().getHomeInstance();
        home_model = homeModel.getInstance();
        instanceObjectInitialization();
    }

    private void instanceObjectInitialization()
    {
        ad_manager = new adManager(getAppContext(),new admobCallback(),home_controller.getBannerAd());
        analytic_manager = new analyticManager(getAppContext(),new analyticCallback());
        exit_manager = new exitManager(getAppContext(),new exitCallback());
        fabric_manager = new fabricManager(getAppContext(),new fabricCallback());
        firebase_manager = new firebaseManager(getAppContext(),new firebaseCallback());
        local_notification = new localNotification(getAppContext(),new notificationCallback());
        message_manager = new messageManager(getAppContext(),new messageCallback());
        orbot_manager = new orbotManager(getAppContext(),new orbotCallback());
        proxy_manager = new proxyManager(getAppContext(),new proxyCallback());
    }

    /*Helper Methods*/

    AppCompatActivity getAppContext()
    {
        return home_controller;
    }

    /*---------------------------------------------- EXTERNAL REQUEST LISTENER-------------------------------------------------------*/

    /*Message Manager*/
    public void MessageManagerHandler(String data,enums.popup_type type){
        message_manager.createMessage(data,type);
    }

    /*Proxy Manager*/
    public void proxyManager(boolean status){
        if(status){
            proxy_manager.startVPN();
        }
        else{
            proxy_manager.disconnectConnection();
        }
    }
    public boolean proxyStatus(){
        return proxy_manager.isProxyRunning();
    }

    /*Notification Manager*/
    public void createNotification(String title,String message){
        local_notification.createNotification(title,message);
    }

    /*Ad Manager*/
    public void initializeBannerAds(){
        ad_manager.initializeBannerAds();
    }

    /*Onion Proxy Manager*/
    public boolean OrbotManagerInit(String url){
        return orbot_manager.initOrbot(url);
    }
    public String orbotLogs(){
        return orbot_manager.getLogs();
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            home_controller.onBannerAdLoaded();
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Exit Manager*/
    private class exitCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            preferenceController.getInstance().setBool(keys.low_memory,false);
            proxy_manager.disconnectConnection();
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Analytics Manager*/
    private class analyticCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            analytic_manager.logUser();
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Fabric Manager*/
    private class fabricCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Firebase Manager*/
    private class firebaseCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Notification Manager*/
    private class notificationCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Proxy Manager*/
    private class proxyCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            if(status.search_status.equals(strings.darkweb))
            {
                home_controller.initBoogle();
            }
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            home_model.setPort(Integer.parseInt(data));
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

    /*Message Manager*/
    private class messageCallback implements callbackManager.callbackListener{
        @Override
        public void callbackSuccess(String data, enums.callbackType callback_type)
        {
            if(callback_type.equals(enums.callbackType.welcome))
            {
                home_controller.onloadURL(data,false,true,false);
            }
            else if(callback_type.equals(enums.callbackType.cancel_welcome)){
                preferenceController.getInstance().setBool(keys.first_time_loaded,true);
            }
            else if(callback_type.equals(enums.callbackType.reload)){
                home_controller.onReload();
            }
            else if(callback_type.equals(enums.callbackType.clear_history)){
                list_model.getInstance().getListInstance().onClearAll();
            }
            else if(callback_type.equals(enums.callbackType.bookmark)){
                String [] dataParser = data.split("split");
                home_model.addBookmark(dataParser[0],dataParser[1]);
            }
            else if(callback_type.equals(enums.callbackType.app_rated)){
                preferenceController.getInstance().setBool(keys.isAppRated,true);
            }
            else if(callback_type.equals(enums.callbackType.download_file)){
                homeModel.getInstance().getHomeInstance().downloadFile();
            }
        }

        @Override
        public void callbackFailure(int errorCode)
        {

        }
    }

}
