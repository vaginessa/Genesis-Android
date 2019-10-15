package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;


import java.util.ArrayList;

public class dataController
{
    /*Private Variables*/

    private dataModel preferences_model;
    private historyController history_controller;

    /*Private Declarations*/

    private static final dataController ourInstance = new dataController();
    public static dataController getInstance()
    {
        return ourInstance;
    }

    /*Initializations*/

    private dataController()
    {
    }

    public void initialize(AppCompatActivity app_context)
    {
        preferences_model = new dataModel(app_context);
        preferences_model.initializeBookmarks();
        preferences_model.setMaxHistoryID(databaseController.getInstance().getLargestHistoryID());
        preferences_model.setHistorySize(databaseController.getInstance().getLargestHistoryID());
        preferences_model.initSuggestions();
    }

    public void initializeListData()
    {
        if(!status.history_status)
        {
            preferences_model.initializeHistory(databaseController.getInstance().selectHistory(0,constants.start_list_size));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
    }

    /*Saving Preferences*/

    public void setString(String valueKey, String value)
    {
        preferences_model.setString(valueKey, value);
    }

    public void setBool(String valueKey, boolean value)
    {
        preferences_model.setBool(valueKey, value);
    }

    public void setInt(String valueKey, int value)
    {
        preferences_model.setFloat(valueKey, value);
    }

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault)
    {
        return preferences_model.getString(valueKey, valueDefault);
    }

    public boolean getBool(String valueKey, boolean valueDefault)
    {
        return preferences_model.getBool(valueKey, valueDefault);
    }

    public float getFloat(String valueKey, int valueDefault)
    {
        return preferences_model.getFloat(valueKey, valueDefault);
    }

    /*Recieving History and Bookmarks*/

    public ArrayList<historyRowModel> getHistory() {
        return preferences_model.getHistory();
    }
    public void addHistory(String url) {
        preferences_model.addHistory(url);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void removeHistory(String url){
        preferences_model.removeHistory(url);
    }
    public void clearHistory(){
        preferences_model.clearHistory();
    }
    public void loadMoreHistory(){
        ArrayList<historyRowModel> history = databaseController.getInstance().selectHistory(preferences_model.getHistory().size()-1,constants.max_list_size);
        if(history.size()>0){
            preferences_model.loadMoreHistory(history);
        }
        activityContextManager.getInstance().getHistoryController().updateHistory();
    }

    public ArrayList<bookmarkRowModel> getBookmark(){
        return preferences_model.getBookmark();
    }
    public void addBookmark(String url,String title){
        preferences_model.addBookmark(url,title);
    }
    public void clearBookmark(){
        preferences_model.clearBookmark();
    }

    public ArrayList<String> getSuggestions(){
        return preferences_model.getSuggestions();
    }
}
