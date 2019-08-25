package com.darkweb.genesissearchengine.appManager.setting_manager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

public class settingModel
{

    /*Private Variable Declarations*/

    private settingController settingInstance;

    /*Initializations*/

    private settingModel(){
    }
    private static final settingModel ourInstance = new settingModel();
    public static settingModel getInstance()
    {
        return ourInstance;
    }
    settingController getSettingInstance()
    {
        return settingInstance;
    }

    /*Helper Methods*/

    void setSettingInstance(settingController settingInstance)
    {
        this.settingInstance = settingInstance;
    }

    void init_status()
    {
        settingModel.getInstance().search_status = status.search_status;
        settingModel.getInstance().history_status = status.history_status;
        settingModel.getInstance().java_status = status.java_status;
    }

    /*Changed Status*/

    public String search_status = strings.emptyStr;
    boolean java_status = false;
    boolean history_status = true;

}
