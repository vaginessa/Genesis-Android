package com.darkweb.genesissearchengine.constants;

public class constants
{
    /*LOCAL URL CONSTANTS*/

    public static String blackMarket = "https://boogle.store/search?q=black+market&p_num=1&s_type=all";
    public static String leakedDocument = "https://boogle.store/search?q=leaked+document&p_num=1&s_type=all&p_num=1&s_type=all";
    public static String news = "https://boogle.store/search?q=latest%20news&p_num=1&s_type=news";
    public static String softwares = "https://boogle.store/search?q=softwares+tools&p_num=1&s_type=all&p_num=1&s_type=all";

    /*URL CONSTANTS*/

    public static String backendGenesis = "http://boogle.store";
    public static String backendUrlHost = "boogle.store";
    public static String updateUrl = "https://boogle.store/manual?abi=";
    public static String frontEndUrlHost = "genesis.store";
    public static String frontEndUrlHost_v1 = "genesis.onion";
    public static String backendGoogle = "https://www.google.com/";
    public static String backendDuckDuckGo = "https://duckduckgo.com/";
    public static String playstoreUrl = "https://play.google.com/store/apps/details?id=com.darkweb.genesissearchengine";

    /*BUILD CONSTANTS*/

    public static String build_type = "playstore"; //  "local" :: "playstore"

    /*PROXY CONSTANTS*/

    public static int proxy_type = 1;
    public static String proxy_socks = "127.0.0.1";
    public static int proxy_socks_version  = 5;
    public static boolean proxy_socks_remote_dns  = true;
    public static boolean proxy_cache  = true;
    public static boolean proxy_memory  = true;
    public static String proxy_useragent_override  = "Mozilla/5.0 (Android 9; Mobile; rv:67.0) Gecko/67.0 Firefox/67.0";
    public static boolean proxy_donottrackheader_enabled  = false;
    public static int proxy_donottrackheader_value  = 1;

    /*MENU CONSTANTS*/

    public static int list_history  = 1;
    public static int list_bookmark  = 2;

    /*SETTINGS CONSTANTS*/

    public static int max_list_data_size =5000;
    public static int max_list_size =5000;
    public static int start_list_size =100;
    public static String databae_name="genesis_dbase";

    /*ADMOB CONSTANTS*/

    public static String admobKey = "ca-app-pub-5074525529134731~2926711128";
    public static String testKey = "5AAFC2DFAE5C3906292EB576F0822FD7";

    /*PROXY CONSTANTS*/

    public static String channel_id = "vpn";
    public static String bypassDomains_1 = "*facebook.com";
    public static String bypassDomains_2 = "*wtfismyip.com";

    /*ANALYTICS CONSTANTS*/

    public static String unique_key_id = "*PREF_UNIQUE_ID";
    public static String user_email = "user@fabric.io";

    /*HOME CONSTANTS*/
    public static int min_progress_bar_value = 5;

}
