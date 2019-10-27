package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;

public class status
{
    /*App Status*/

    public static String current_ABI = "7.0";

    /*Settings Status*/

    public static String sTorLogsStatus = strings.EMPTY_STR;
    public static String sSearchStatus = constants.BACKEND_DUCK_DUCK_GO_URL;
    public static boolean sJavaStatus = true;
    public static boolean sHistoryStatus = true;
    public static boolean sGateway = false;
    public static boolean sIsAppPaused = false;
    public static boolean sIsTorInitialized = false;
    public static boolean sIsWelcomeEnabled = true;
    public static boolean sIsAppStarted = false;
    public static boolean sIsAppRated = false;
    public static boolean sFontAdjustable = true;
    public static boolean sCookieStatus = false;
    public static float sFontSize = 1;

    /*Initializations*/

    public static void initStatus()
    {
        status.sJavaStatus = dataController.getInstance().getBool(keys.JAVA_SCRIPT,true);
        status.sHistoryStatus = dataController.getInstance().getBool(keys.HISTORY_CLEAR,true);
        status.sSearchStatus = dataController.getInstance().getString(keys.SEARCH_ENGINE,constants.BACKEND_GENESIS_URL);
        status.sGateway = dataController.getInstance().getBool(keys.GATEWAY,false);
        status.sIsWelcomeEnabled = dataController.getInstance().getBool(keys.IS_WELCOME_ENABLED,true);
        status.sIsAppRated = dataController.getInstance().getBool(keys.IS_APP_RATED,false);
        status.sFontSize = dataController.getInstance().getFloat(keys.FONT_SIZE,100);
        status.sFontAdjustable = dataController.getInstance().getBool(keys.FONT_ADJUSTABLE,true);
        status.sCookieStatus = dataController.getInstance().getBool(keys.COOKIE_ADJUSTABLE,false);
    }

}
