package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.Context;
import android.net.Uri;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;

import java.util.*;

public class homeModel
{
    /*Data Objects*/
    private ArrayList<historyRowModel> history = new ArrayList<>();
    private ArrayList<bookmarkRowModel> bookmarks = new ArrayList<>();
    private ArrayList<navigation_model> navigation = new ArrayList<navigation_model>();
    private HashSet<String> suggestions = new HashSet<String>();
    private static int port = 9150;

    private Context appContext;
    private homeController appInstance;

    /*Initializations*/
    public void initialization(){
        initializeHistory();
        initializeBookmarks();
    }

    /*Setter Getter Initializations*/
    private static final homeModel ourInstance = new homeModel();
    public static homeModel getInstance()
    {
        return ourInstance;
    }


    /*Getters Setters*/
    public void setPort(int port)
    {
        status.onionProxyPort = port;
    }


    public void setAppContext(Context appContext)
    {
        this.appContext = appContext;
    }
    public Context getAppContext()
    {
        return appContext;
    }


    public homeController getHomeInstance()
    {
        return appInstance;
    }
    public void setAppInstance(homeController appInstance)
    {
        this.appInstance = appInstance;
    }


    public void initializeHistory(){
        if(!status.history_status)
        {
            history = databaseController.getInstance().selectHistory();
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
        homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    public void addHistory(String url) {
        dataController.getInstance().addHistory(url);
    }
    public ArrayList<historyRowModel> getHistory() {
        return history;
    }


    private void initializeBookmarks(){
        bookmarks = databaseController.getInstance().selectBookmark();
    }
    public void addBookmark(String url,String title){
        dataController.getInstance().addBookmark(url,title);
    }
    public ArrayList<bookmarkRowModel> getBookmark(){
        return bookmarks;
    }


    public void initSuggestions(String url) {
        suggestions.add(url.replace("https://","").replace("http://",""));
    }
    public void addSuggestions(String url) {
        if(url.contains("boogle.store"))
        {
            Uri uri = Uri.parse(url);
            String actual_url = uri.getQueryParameter("q");
            suggestions.add(actual_url);
        }
        suggestions.add(url.replace("https://","").replace("http://",""));
        homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    public ArrayList<String> getSuggestions() {
        return new ArrayList<String>(suggestions);
    }

    /*Navigation*/

    public void addNavigation(String url,enums.navigationType type) {
        if(navigation.size()==0 || !navigation.get(navigation.size()-1).getURL().equals(url))
        {
            navigation.add(new navigation_model(url,type));
        }
    }
    public ArrayList<navigation_model> getNavigation() {
        return navigation;
    }

    /*Helper Method*/
    public boolean isUrlRepeatable(String url,String viewUrl){
        return url.equals(viewUrl) && !homeModel.getInstance().getHomeInstance().isInternetErrorOpened() || url.contains("https://boogle.store/search?q=&");

    }

}
