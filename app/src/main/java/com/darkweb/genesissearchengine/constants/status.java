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

    public static String search_status = constants.backendGenesis;
    public static boolean java_status = true;
    public static boolean history_status = true;
    public static boolean gateway = true;
    public static boolean isAppPaused = false;
    public static boolean isTorInitialized = false;
    public static boolean isBootstrapped = false;

    /*Initializations*/

    public static void initStatus()
    {
        status.java_status = dataController.getInstance().getBool(keys.java_script,true);
        status.history_status = dataController.getInstance().getBool(keys.history_clear,true);
        status.search_status = dataController.getInstance().getString(keys.search_engine,constants.backendGenesis);
        status.gateway = dataController.getInstance().getBool(keys.gateway,true);
        status.isBootstrapped = dataController.getInstance().getBool(keys.is_bootstrapped,false);
    }

}
