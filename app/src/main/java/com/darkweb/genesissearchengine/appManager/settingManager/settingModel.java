package com.darkweb.genesissearchengine.appManager.settingManager;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;

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
    private boolean font_adjustable = true;
    private float font_size = 1;

    String getSearchStatus(){
        return search_status;
    }

    public boolean getJavaStatus(){
        return java_status;
    }

    public boolean getHistoryStatus(){
        return history_status;
    }

    float getFontSize(){
        return this.font_size;
    }

    void setSearchStatus(String search_status){
        this.search_status = search_status;
    }

    void setFontSize(float font_size){
        this.font_size = font_size;
    }

    void setAdjustableStatus(boolean font_status){
        this.font_adjustable = font_status;
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
            event.invokeObserver(java_status, enums.eventType.update_javascript);
        }
        if(status.fontAdjustable != font_adjustable)
        {
            dataController.getInstance().setBool(keys.font_adjustable,font_adjustable);
            dataController.getInstance().setInt(keys.font_size,100);

            status.fontAdjustable = font_adjustable;
            status.fontSize = 100;
            font_size = 100;

            event.invokeObserver(font_size, enums.eventType.update_font_adjustable);
        }
        if(status.fontSize != font_size)
        {
            if(font_size<=0){
                font_size = 1;
            }

            dataController.getInstance().setInt(keys.font_size,(int)font_size);
            dataController.getInstance().setBool(keys.font_adjustable,false);

            status.fontSize = font_size;
            status.fontAdjustable = false;
            font_adjustable = false;

            event.invokeObserver(font_size, enums.eventType.update_font_size);
        }

        event.invokeObserver(history_status, enums.eventType.close_view);
    }

}
