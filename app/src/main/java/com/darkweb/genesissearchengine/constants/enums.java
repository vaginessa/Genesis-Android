package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/

    public enum searchEngine{HiddenWeb,Bing,Google}
    public enum adID{hidden_onion, hidden_onion_start,hidden_base}
    public enum navigationType{onion, base}

    /*Message Manager*/
    public enum popup_type{welcome,abi_error,rate_success,reported_success,bookmark, clear_history,clear_bookmark,report_url,rate_app,version_warning,start_orbot,download_file}
    public enum eventType{welcome,cancel_welcome,reload, clear_history,clear_bookmark,bookmark, app_rated,download_file, update_searcn, update_javascript, update_history,close_view}

    /*List Manager*/
    public enum history_eventType{url_triggered,url_clear,remove_from_database,is_empty}
    public enum bookmark_eventType{url_triggered,url_clear,remove_from_database,is_empty}

}