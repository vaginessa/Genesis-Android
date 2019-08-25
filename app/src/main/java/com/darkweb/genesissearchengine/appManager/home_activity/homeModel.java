package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.Context;
import android.net.Uri;
import com.darkweb.genesissearchengine.appManager.database_manager.database_controller;
import com.darkweb.genesissearchengine.appManager.list_manager.list_row_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;

import java.text.SimpleDateFormat;
import java.util.*;

public class homeModel
{
    /*Data Objects*/
    private ArrayList<list_row_model> history = new ArrayList<list_row_model>();
    private ArrayList<list_row_model> bookmarks = new ArrayList<list_row_model>();
    private ArrayList<navigation_model> navigation = new ArrayList<navigation_model>();
    private HashSet<String> suggestions = new HashSet<String>();
    private static int port = 9150;

    private Context appContext;
    private homeController appInstance;

    /*Initializations*/
    public void initialization(){
        database_controller.getInstance().initialize();
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
            history = database_controller.getInstance().selectHistory();
        }
        else
        {
            database_controller.getInstance().execSQL("delete from history where 1",null);
        }
        homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    public void addHistory(String url) {

        if(history.size()> constants.max_history_size)
        {
            database_controller.getInstance().execSQL("delete from history where id="+history.get(history.size()-1).getId(),null);
            history.remove(history.size()-1);
        }

        int autoval = 0;
        if(history.size()>0)
        {
            autoval = history.get(0).getId()+1;
        }

        addSuggestions(url);
        SimpleDateFormat d_form = new SimpleDateFormat("dd MMMM | hh:mm a");
        String date = d_form.format(new Date());

        String[] params = new String[1];
        params[0] = url;

        database_controller.getInstance().execSQL("INSERT INTO history(id,date,url) VALUES("+autoval+",'"+date+"',?);",params);
        history.add(0,new list_row_model(url,date,autoval));
    }
    public ArrayList<list_row_model> getHistory() {
        return history;
    }


    private void initializeBookmarks(){
        bookmarks = database_controller.getInstance().selectBookmark();
    }
    public void addBookmark(String url,String title){
        int autoval = 0;
        if(bookmarks.size()> constants.max_bookmark_size)
        {
            database_controller.getInstance().execSQL("delete from bookmark where id="+bookmarks.get(bookmarks.size()-1).getId(),null);
            bookmarks.remove(history.size()-1);
        }

        if(bookmarks.size()>0)
        {
            autoval = bookmarks.get(0).getId()+1;
        }

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;

        database_controller.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        bookmarks.add(0,new list_row_model(url,title,autoval));
    }
    public ArrayList<list_row_model> getBookmark(){
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
