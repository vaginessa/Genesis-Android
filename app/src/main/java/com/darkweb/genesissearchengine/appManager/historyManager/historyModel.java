package com.darkweb.genesissearchengine.appManager.historyManager;

import java.util.ArrayList;

public class historyModel
{
    /*Private Variables*/

    private ArrayList<historyRowModel> model_list = new ArrayList<>();

    /*Initializations*/

    void setList(ArrayList<historyRowModel> model)
    {
        model_list = model;
    }
    ArrayList<historyRowModel> getList()
    {
        return model_list;
    }
    private void removeFromMainList(int index)
    {
        model_list.remove(index);
    }

    void onManualClear(int index){
         removeFromMainList(index);
    }

    void clearList(){
        model_list.clear();
    }



}