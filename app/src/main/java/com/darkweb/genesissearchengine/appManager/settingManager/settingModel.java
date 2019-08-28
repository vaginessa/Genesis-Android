package com.darkweb.genesissearchengine.appManager.settingManager;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

public class settingModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener event;

    /*Initializations*/

    settingModel(eventObserver.eventListener event){
        init_status();
        this.event = event;
    }

    /*Helper Methods*/

    private void init_status()
    {
        search_status = status.search_status;
        history_status = status.history_status;
        java_status = status.java_status;
    }

    /*Changed Status*/

    private String search_status = strings.emptyStr;
    private boolean java_status = false;
    private boolean history_status = true;

    String getSearchStatus(){
        return search_status;
    }

    public boolean getJavaStatus(){
        return java_status;
    }

    public boolean getHistoryStatus(){
        return history_status;
    }

    void setSearchStatus(String search_status){
        this.search_status = search_status;
    }

    void setJavaStatus(boolean java_status){
        this.java_status = java_status;
    }

    void setHistoryStatus(boolean history_status){
        this.history_status = history_status;
    }

    void onCloseView()
    {
        if(!status.search_status.equals(search_status))
        {
            event.invokeObserver(search_status, enums.eventType.update_searcn);
        }
        if(status.java_status != java_status)
        {
            event.invokeObserver(java_status, enums.eventType.update_javascript);
        }
        if(status.history_status != history_status)
        {
            event.invokeObserver(history_status, enums.eventType.update_history);
        }
        event.invokeObserver(history_status, enums.eventType.close_view);
    }

}
