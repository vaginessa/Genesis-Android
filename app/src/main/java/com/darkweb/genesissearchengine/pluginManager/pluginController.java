package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import java.util.Collections;
import java.util.List;

public class pluginController
{
    /*Plugin Instance*/

    private adManager mAdManager;
    private analyticManager mAnalyticManager;
    private fabricManager mFabricManager;
    private firebaseManager mFirebaseManager;
    private messageManager mMessageManager;
    private activityContextManager mContextManager;
    private boolean mIsInitialized = false;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private homeController mHomeController;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }
    private pluginController()
    {
    }

    public void initialize(){
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    private void instanceObjectInitialization()
    {
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager = activityContextManager.getInstance();

        mAdManager = new adManager(getAppContext(),new admobCallback(), mHomeController.getBannerAd());
        mAnalyticManager = new analyticManager(getAppContext(),new analyticCallback());
        getAppContext().startService(new Intent(getAppContext(), exitManager.class));
        mFabricManager = new fabricManager(getAppContext(),new fabricCallback());
        mFirebaseManager = new firebaseManager(getAppContext(),new firebaseCallback());
        mMessageManager = new messageManager(new messageCallback());
    }

    public void initializeAllProxies(AppCompatActivity context){
        orbotManager.getInstance().initialize(context,new orbotCallback());
    }

    public void updateCookiesStatus(){
        orbotManager.getInstance().updateCookiesStatus();
    }

    /*Helper Methods*/

    private AppCompatActivity getAppContext()
    {
        return mHomeController;
    }
    public boolean isInitialized(){
        return mIsInitialized;
    }
    void proxyManagerExitInvoke(){
        mHomeController.onClose();
        orbotManager.getInstance().onClose();
        onResetMessage();
        System.exit(1);
    }

    /*---------------------------------------------- EXTERNAL REQUEST LISTENER-------------------------------------------------------*/

    /*Message Manager*/
    public void MessageManagerHandler(AppCompatActivity app_context,List<String> data,enums.etype type){
        mMessageManager.createMessage(app_context,data,type);
    }
    public void onResetMessage(){
        mMessageManager.onReset();
    }

    /*Firebase Manager*/
    public void logEvent(String value){
        mFirebaseManager.logEvent(value);
    }

    /*Ad Manager*/
    public void initializeBannerAds(){
        mAdManager.loadAds();
    }

    public boolean isAdvertLoaded(){
       return mAdManager.isAdvertLoaded();
    }

    /*Onion Proxy Manager*/
    public void initializeOrbot(Context context){
        orbotManager.getInstance().startOrbot(context);
    }
    public boolean isOrbotRunning(){
        return orbotManager.getInstance().isOrbotRunning();
    }
    public void setProxy(String url){
        orbotManager.getInstance().setProxy(url);
    }
    public String orbotLogs(){
        return orbotManager.getInstance().getLogs();
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {
            mHomeController.onSetBannerAdMargin();
        }
    }

    /*Analytics Manager*/
    private class analyticCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {
            mAnalyticManager.logUser();
        }
    }

    /*Fabric Manager*/
    private class fabricCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {
        }
    }

    /*Firebase Manager*/
    private class firebaseCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {
        }
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {

        }
    }

    /*Message Manager*/
    private class messageCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> data, enums.etype event_type)
        {
            if(event_type.equals(enums.etype.welcome))
            {
                mHomeController.onLoadURL(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.cancel_welcome)){
                dataController.getInstance().setBool(keys.IS_WELCOME_ENABLED,false);
            }
            else if(event_type.equals(enums.etype.reload)){
                if(orbotManager.getInstance().isOrbotRunning())
                {
                    mHomeController.onReload(null);
                }
                else {
                    mMessageManager.createMessage(mHomeController, Collections.singletonList(data.get(0).toString()),enums.etype.start_orbot);
                }
            }
            else if(event_type.equals(enums.etype.clear_history)){
                dataController.getInstance().clearHistory();
                dataController.getInstance().clearSuggestions();
                mContextManager.getHistoryController().onclearData();
                mHomeController.onClearSession();
                dataController.getInstance().clearTabs();
            }
            else if(event_type.equals(enums.etype.clear_bookmark)){
                dataController.getInstance().clearBookmark();
                mContextManager.getBookmarkController().onclearData();
            }
            else if(event_type.equals(enums.etype.bookmark)){
                String [] dataParser = data.get(0).toString().split("split");
                if(dataParser.length>1){
                    logEvent(strings.URL_BOOKMARKED);
                    dataController.getInstance().addBookmark(dataParser[0],dataParser[1]);
                }else {
                    dataController.getInstance().addBookmark(dataParser[0],"");
                }
            }
            else if(event_type.equals(enums.etype.app_rated)){
                dataController.getInstance().setBool(keys.IS_APP_RATED,true);
            }
            else if(event_type.equals(enums.etype.download_file)){
                mHomeController.onDownloadFile();
            }
            else if(event_type.equals(enums.etype.download_file_manual)){
                mHomeController.onManualDownload(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.connect_vpn)){
                boolean status = (boolean)data.get(0);
            }
            else if(event_type.equals(enums.etype.open_link_new_tab)){
                mHomeController.onOpenLinkNewTab(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.open_link_current_tab)){
                mHomeController.onLoadURL(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.copy_link)){
                helperMethod.copyURL(data.get(0).toString(),mContextManager.getHomeController());
            }
            else if(event_type.equals(enums.etype.clear_tab)){
                dataController.getInstance().clearTabs();
                activityContextManager.getInstance().getTabController().finish();
            }
        }
    }
}
