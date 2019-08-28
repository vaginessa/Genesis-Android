package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.constants.status;


import java.util.ArrayList;

public class dataController
{
    /*Private Variables*/

    private dataModel preferences_model;

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
    }

    public void initializeListData()
    {
        if(!status.history_status)
        {
            preferences_model.initializeHistory(databaseController.getInstance().selectHistory());
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

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault)
    {
        return preferences_model.getString(valueKey, valueDefault);
    }

    public boolean getBool(String valueKey, boolean valueDefault)
    {
        return preferences_model.getBool(valueKey, valueDefault);
    }

    /*Recieving History and Bookmarks*/

    public ArrayList<historyRowModel> getHistory() {
        return preferences_model.getHistory();
    }
    public void addHistory(String url) {
        preferences_model.addHistory(url);
    }

    public ArrayList<bookmarkRowModel> getBookmark(){
        return preferences_model.getBookmark();
    }
    public void addBookmark(String url,String title){
        preferences_model.addBookmark(url,title);
    }

    public void clearHistory(){
        preferences_model.clearHistory();
    }

    public void clearBookmark(){
        preferences_model.clearBookmark();
    }

}
