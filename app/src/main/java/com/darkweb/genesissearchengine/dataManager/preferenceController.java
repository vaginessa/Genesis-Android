package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;

public class preferenceController
{
    /*Private Variables*/

    private preferencesModel preferences_model;

    /*Private Declarations*/

    private static final preferenceController ourInstance = new preferenceController();
    public static preferenceController getInstance()
    {
        return ourInstance;
    }

    /*Initializations*/

    private preferenceController()
    {
        preferences_model = new preferencesModel(homeModel.getInstance().getHomeInstance());
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

}
