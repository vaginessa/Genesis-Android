package com.darkweb.genesissearchengine.appManager.databaseManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.darkweb.genesissearchengine.constants.constants;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class databaseController
{

    /*Private Variables*/

    private static final databaseController ourInstance = new databaseController();
    private SQLiteDatabase database_instance;

    public static databaseController getInstance()
    {
        return ourInstance;
    }

    private databaseController()
    {
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context)
    {
        try
        {
            database_instance = app_context.openOrCreateDatabase(constants.databae_name, MODE_PRIVATE, null);
            database_instance.execSQL("CREATE TABLE IF NOT EXISTS " + "history" + " (id INT(4),date VARCHAR,url VARCHAR);");
            database_instance.execSQL("CREATE TABLE IF NOT EXISTS " + "bookmark" + " (id INT(4),title VARCHAR,url VARCHAR);");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    /*Helper Methods*/

    public void execSQL(String query,String[] params)
    {
        if(params==null)
        {
            database_instance.execSQL(query);
        }
        else
        {
            database_instance.execSQL(query,params);
        }
    }

    public ArrayList<historyRowModel> selectHistory(){
        ArrayList<historyRowModel> tempmodel = new ArrayList<>();
        Cursor c = database_instance.rawQuery("SELECT * FROM history ORDER BY id DESC ", null);
        if (c.moveToFirst()){
            do {
                tempmodel.add(new historyRowModel(c.getString(2), c.getString(1),Integer.parseInt(c.getString(0))));
                homeModel.getInstance().initSuggestions(c.getString(2));
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

    public ArrayList<bookmarkRowModel> selectBookmark(){
        ArrayList<bookmarkRowModel> tempmodel = new ArrayList<>();
        Cursor c = database_instance.rawQuery("SELECT * FROM bookmark ORDER BY id DESC ", null);

        if (c.moveToFirst()){
            do {
                tempmodel.add(new bookmarkRowModel(c.getString(2), c.getString(1),Integer.parseInt(c.getString(0))));
            } while(c.moveToNext());
        }
        c.close();

        return  tempmodel;
    }

    public void deleteFromList(int index,String table) {
        databaseController.getInstance().execSQL("delete from "+table+" where id="+index,null);
    }

}
