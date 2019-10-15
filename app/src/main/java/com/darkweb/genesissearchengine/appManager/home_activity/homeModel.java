package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.helperMethod;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class homeModel
{
    private String search_engine;

    homeModel(){
        search_engine = constants.backendGoogle;
    }

    String getSearchEngine(){
        return status.search_status;
    }

    String url_complete(String url){
        try
        {
            String updateUrl = helperMethod.completeURL(url);
            URL host = new URL(updateUrl);
            boolean isUrlValid = Patterns.WEB_URL.matcher(updateUrl).matches();
            if(isUrlValid && host.getHost().replace("www.","").contains("."))
            {
                return updateUrl;
            }
        }

        catch (Exception ex){
            ex.printStackTrace();
        }

        if(status.search_status.equals(constants.backendGoogle)){
            return getSearchEngine()+"search?q="+url.replaceAll(" ","+");
        }
        else if(status.search_status.equals(constants.backendGenesis)){
            return getSearchEngine()+"/search?s_type=all&p_num=1&q="+url.replaceAll(" ","+");
        }
        else{
            return getSearchEngine()+"?q="+url.replaceAll(" ","+");
        }
    }


}
