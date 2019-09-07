package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;

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
    private activityContextManager contextManager;

    /*Private Variables*/

    private static final pluginController ourInstance = new pluginController();
    private homeController home_controller;
    private historyController history_controller;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }
    private pluginController()
    {
        home_controller = activityContextManager.getInstance().getHomeController();
        contextManager = activityContextManager.getInstance();
        instanceObjectInitialization();
    }

    public void initialize(){
    }

    private void instanceObjectInitialization()
    {
        ad_manager = new adManager(getAppContext(),new admobCallback(),home_controller.getBannerAd());
        analytic_manager = new analyticManager(getAppContext(),new analyticCallback());
        // exit_manager = new exitManager(getAppContext(),new exitCallback());
        fabric_manager = new fabricManager(getAppContext(),new fabricCallback());
        firebase_manager = new firebaseManager(getAppContext(),new firebaseCallback());
        local_notification = new localNotification(getAppContext(),new notificationCallback());
        message_manager = new messageManager(new messageCallback());
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
    public void MessageManagerHandler(AppCompatActivity app_context,String data,enums.popup_type type){
        message_manager.createMessage(app_context,data,type);
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
    public void

    initializeBannerAds(){
        ad_manager.initializeBannerAds();
    }

    /*Onion Proxy Manager*/
    public boolean OrbotManagerInit(boolean deepCheck){
        return orbot_manager.isOrbotRunning(deepCheck);
    }
    public void setProxy(boolean status){
        orbot_manager.setProxy(status);
    }
    public String orbotLogs(){
        return orbot_manager.getLogs();
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

    /*Exit Manager*/
    private class exitCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(Object data, enums.eventType e_type)
        {
            dataController.getInstance().setBool(keys.low_memory,false);
            proxy_manager.disconnectConnection();
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
            if(status.search_status.equals(strings.darkweb))
            {
                //home_controller.initBoogle();
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
                dataController.getInstance().setBool(keys.first_time_loaded,true);
            }
            else if(e_type.equals(enums.eventType.reload)){
                if(orbot_manager.isOrbotRunning(true))
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
        }
    }

}
