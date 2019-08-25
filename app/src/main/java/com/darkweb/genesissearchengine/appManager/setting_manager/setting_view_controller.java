package com.darkweb.genesissearchengine.appManager.setting_manager;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preferenceController;

import static com.darkweb.genesissearchengine.constants.status.history_status;
import static com.darkweb.genesissearchengine.constants.status.java_status;

class setting_view_controller
{
    /*Private Variables*/

    private Spinner search;
    private Spinner javascript;
    private Spinner history;
    private settingController setting_controller;
    private settingModel setting_model;
    private preferenceController preference_controller;

    /*Initializations*/

    setting_view_controller(Spinner search, Spinner javascript, Spinner history)
    {
        this.search = search;
        this.javascript = javascript;
        this.history = history;
        this.setting_controller = settingModel.getInstance().getSettingInstance();
        this.setting_model = settingModel.getInstance();
        preference_controller = preferenceController.getInstance();

        initViews();
        initJavascript();
        initHistory();
        initSearchEngine();
    }

    private void initViews()
    {
        search.setDropDownVerticalOffset(15);
        search.setDropDownHorizontalOffset(-15);
        javascript.setDropDownVerticalOffset(15);
        javascript.setDropDownHorizontalOffset(-15);
        history.setDropDownVerticalOffset(15);
        history.setDropDownHorizontalOffset(-15);
    }

    private void initJavascript()
    {
        if (java_status)
        {
            javascript.setSelection(0);
        }
        else
        {
            javascript.setSelection(1);
        }
    }

    private void initHistory()
    {
        if (history_status)
        {
            history.setSelection(0);
        }
        else
        {
            history.setSelection(1);
        }
    }

    @SuppressWarnings("unchecked")
    private void initSearchEngine()
    {
        String myString = preference_controller.getString(keys.search_engine, strings.darkweb);

        ArrayAdapter myAdap = (ArrayAdapter) search.getAdapter();
        int spinnerPosition = myAdap.getPosition(myString);
        search.setSelection(spinnerPosition);
    }

    /*Helper Methods*/

    void closeView()
    {

        if(!status.search_status.equals(setting_model.search_status))
        {
            status.search_status = setting_model.search_status;
            setting_controller.initSearchEngine();
            preference_controller.setString(keys.search_engine, setting_model.search_status);
        }
        if(status.java_status != setting_model.java_status)
        {
            status.java_status = setting_model.java_status;
            setting_controller.reInitGeckoView();
            preference_controller.setBool(keys.java_script, status.java_status);
        }
        if(status.history_status != setting_model.history_status)
        {
            status.history_status = setting_model.history_status;
            preference_controller.setBool(keys.history_clear, status.history_status);
        }


        setting_controller.finish();
    }

}
