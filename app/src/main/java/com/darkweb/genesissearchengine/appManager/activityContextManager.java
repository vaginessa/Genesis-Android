package com.darkweb.genesissearchengine.appManager;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.home_activity.homeController;

public class activityContextManager
{
    /*Private Variables*/

    private static final activityContextManager ourInstance = new activityContextManager();
    public static activityContextManager getInstance()
    {
        return ourInstance;
    }

    /*Private Contexts*/
    private historyController history_controller;
    private bookmarkController bookmark_controller;
    private homeController home_controller;

    /*Initialization*/

    private activityContextManager()
    {
    }

    /*List ContextGetterSetters*/
    public historyController getHistoryController(){
        return history_controller;
    }
    public void setHistoryController(historyController history_controller){
        this.history_controller = history_controller;
    }
    public bookmarkController getBookmarkController(){
        return bookmark_controller;
    }
    public void setBookmarkController(bookmarkController bookmark_controller){
        this.bookmark_controller = bookmark_controller;
    }
    public homeController getHomeController(){
        return home_controller;
    }
    public void setHomeController(homeController home_controller){
        this.home_controller = home_controller;
    }
}
