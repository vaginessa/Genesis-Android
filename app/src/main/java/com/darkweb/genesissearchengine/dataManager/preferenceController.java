package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;

public class preferenceController
{
    /*Private Variables*/

    private AppCompatActivity app_context;

    /*Private Declarations*/

    private static final preferenceController ourInstance = new preferenceController();
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public static preferenceController getInstance()
    {
        return ourInstance;
    }

    /*Initializations*/

    private preferenceController()
    {
        app_context = homeModel.getInstance().getHomeInstance();
    }

    @SuppressLint("CommitPrefEdits")
    public void initialize()
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        edit = prefs.edit();
    }

    /*Saving Preferences*/

    public void setString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    public void setBool(String valueKey, boolean value)
    {
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault)
    {
        return prefs.getString(valueKey, valueDefault);
    }

    public boolean getBool(String valueKey, boolean valueDefault)
    {
        return prefs.getBoolean(valueKey, valueDefault);
    }

}
