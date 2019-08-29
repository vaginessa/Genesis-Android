package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;

public class status
{
    /*App Status*/

    public static boolean isApplicationLoaded = false;
    public static boolean isPlayStoreInstalled = true;
    public static String version_code = "7.0";
    public static String current_ABI = "7.0";

    /*Settings Status*/

    public static String search_status = strings.emptyStr;
    public static boolean java_status = false;
    public static boolean history_status = true;
    public static boolean gateway = false;

    /*Initializations*/

    public static void initStatus()
    {
        status.java_status = dataController.getInstance().getBool(keys.java_script,true);
        status.history_status = dataController.getInstance().getBool(keys.history_clear,true);
        status.search_status = dataController.getInstance().getString(keys.search_engine,strings.darkweb);
        status.gateway = dataController.getInstance().getBool(keys.gateway,false);
    }

}
