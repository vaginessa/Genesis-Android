package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/

    public enum searchEngine{HiddenWeb, DuckDuckGo,Google}
    public enum adID{hidden_onion, hidden_onion_start,hidden_base}
    public enum navigationType{onion, base}

    /*Message Manager*/
    public enum popup_type{welcome,abi_error,rate_success,reported_success,bookmark, clear_history,clear_bookmark,report_url,rate_app,version_warning,start_orbot,download_file,tor_banned}
    public enum eventType{welcome,cancel_welcome,reload,connect_vpn,start_home,disable_splash, clear_history,clear_bookmark,bookmark, app_rated,download_file, update_searcn, update_javascript, update_history,close_view}


    /*List Manager*/
    public enum history_eventType{url_triggered,url_clear,url_clear_at,remove_from_database,is_empty,load_more}
    public enum bookmark_eventType{url_triggered,url_clear,remove_from_database,is_empty}

    /*Home Manager*/
    public enum home_eventType{progress_update,on_url_load,back_list_empty,start_proxy,onMenuSelected,on_request_completed,on_page_loaded,on_load_error,download_file_popup,download_file,proxy_error,on_init_ads}

}