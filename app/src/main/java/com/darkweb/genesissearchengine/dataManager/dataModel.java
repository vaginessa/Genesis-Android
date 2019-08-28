package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("CommitPrefEdits")
class dataModel
{
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    private ArrayList<historyRowModel> history = new ArrayList<>();
    private ArrayList<bookmarkRowModel> bookmarks = new ArrayList<>();

    dataModel(AppCompatActivity app_context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        edit = prefs.edit();
    }

    void setString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    void setBool(String valueKey, boolean value)
    {
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    String getString(String valueKey, String valueDefault)
    {
        return prefs.getString(valueKey, valueDefault);
    }

    boolean getBool(String valueKey, boolean valueDefault)
    {
        return prefs.getBoolean(valueKey, valueDefault);
    }

    /*List Bookmarks and History*/

    void initializeHistory(ArrayList<historyRowModel> history){
        this.history = history;
        // homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    void addHistory(String url) {

        if(history.size()> constants.max_history_size)
        {
            databaseController.getInstance().execSQL("delete from history where id="+history.get(history.size()-1).getId(),null);
            history.remove(history.size()-1);
        }

        int autoval = 0;
        if(history.size()>0)
        {
            autoval = history.get(0).getId()+1;
        }

        SimpleDateFormat d_form = new SimpleDateFormat("dd MMMM | hh:mm a");
        String date = d_form.format(new Date());

        String[] params = new String[1];
        params[0] = url;

        databaseController.getInstance().execSQL("INSERT INTO history(id,date,url) VALUES("+autoval+",'"+date+"',?);",params);
        history.add(0,new historyRowModel(url,date,autoval));
    }
    public ArrayList<historyRowModel> getHistory() {
        return history;
    }
    void clearHistory() {
        history.clear();
    }


    void initializeBookmarks(){
        bookmarks = databaseController.getInstance().selectBookmark();
    }
    void addBookmark(String url, String title){
        int autoval = 0;
        if(bookmarks.size()> constants.max_bookmark_size)
        {
            databaseController.getInstance().execSQL("delete from bookmark where id="+bookmarks.get(bookmarks.size()-1).getId(),null);
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

        databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        bookmarks.add(0,new bookmarkRowModel(url,title,autoval));
    }
    ArrayList<bookmarkRowModel> getBookmark(){
        return bookmarks;
    }
    void clearBookmark() {
        bookmarks.clear();
    }



}
