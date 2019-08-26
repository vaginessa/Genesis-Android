package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CommitPrefEdits")
class preferencesModel
{
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    preferencesModel(AppCompatActivity app_context)
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

}
