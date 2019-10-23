package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;


import org.mozilla.geckoview.GeckoSession;

import java.util.ArrayList;

public class dataController
{
    /*Private Variables*/

    private dataModel mPreferencesModel;
    private historyController mHistoryController;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context){
        mPreferencesModel = new dataModel(app_context);
        mPreferencesModel.initializeBookmarks();
        mPreferencesModel.setMaxHistoryID(databaseController.getInstance().getLargestHistoryID());
        mPreferencesModel.setHistorySize(databaseController.getInstance().getLargestHistoryID());
        mPreferencesModel.initSuggestions();
    }
    public void initializeListData(){
        if(!status.sHistoryStatus)
        {
            mPreferencesModel.initializeHistory(databaseController.getInstance().selectHistory(0,constants.START_LIST_SIZE));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
    }

    /*Saving Preferences*/

    public void setString(String valueKey, String value){
        mPreferencesModel.setString(valueKey, value);
    }
    public void setBool(String valueKey, boolean value){
        mPreferencesModel.setBool(valueKey, value);
    }
    public void setInt(String valueKey, int value)
    {
        mPreferencesModel.setFloat(valueKey, value);
    }

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault){
        return mPreferencesModel.getString(valueKey, valueDefault);
    }
    public boolean getBool(String valueKey, boolean valueDefault){
        return mPreferencesModel.getBool(valueKey, valueDefault);
    }
    public float getFloat(String valueKey, int valueDefault){
        return mPreferencesModel.getFloat(valueKey, valueDefault);
    }

    /*Recieving History*/

    public ArrayList<historyRowModel> getHistory() {
        return mPreferencesModel.getmHistory();
    }
    public void addHistory(String url) {
        mPreferencesModel.addHistory(url);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void removeHistory(String url){
        mPreferencesModel.removeHistory(url);
    }
    public void clearHistory(){
        mPreferencesModel.clearHistory();
    }
    public void loadMoreHistory(){
        ArrayList<historyRowModel> history = databaseController.getInstance().selectHistory(mPreferencesModel.getmHistory().size()-1,constants.MAX_LIST_SIZE);
        if(history.size()>0){
            mPreferencesModel.loadMoreHistory(history);
        }
        activityContextManager.getInstance().getHistoryController().updateHistory();
    }

    /*Recieving Bookmarks*/

    public ArrayList<bookmarkRowModel> getBookmark(){
        return mPreferencesModel.getBookmark();
    }
    public void addBookmark(String url,String title){
        mPreferencesModel.addBookmark(url,title);
    }
    public void clearBookmark(){
        mPreferencesModel.clearBookmark();
    }

    /*Recieving Suggestions*/

    public ArrayList<String> getSuggestions(){
        return mPreferencesModel.getmSuggestions();
    }

    /*Recieving Tabs*/

    public ArrayList<tabRowModel> getTab(){
        return mPreferencesModel.getTab();
    }
    public void addTab(String url, String title, GeckoSession session,int progress){
        mPreferencesModel.addTabs(url,title,session,progress);
    }
    public void clearTabs(){
        mPreferencesModel.clearTab();
    }
    public void closeTab(GeckoSession session){
        mPreferencesModel.closeTab(session);
    }
    public tabRowModel getCurrentTab(){
        return mPreferencesModel.getCurrentTab();
    }
}
