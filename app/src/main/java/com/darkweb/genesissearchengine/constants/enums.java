package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/

    public enum searchEngine{HiddenWeb,Bing,Google}
    public enum adID{hidden_onion, hidden_onion_start,hidden_base}
    public enum navigationType{onion, base}

    /*Message Manager*/
    public enum popup_type{welcome,abi_error,rate_success,reported_success,bookmark,clear_data,report_url,rate_app,version_warning,start_orbot,download_file}
    public enum callbackType{welcome,cancel_welcome,reload, clear_history,bookmark, app_rated,download_file}

}