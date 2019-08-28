package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import java.util.ArrayList;

class bookmarkModel
{
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> model_list = new ArrayList<>();

    /*Initializations*/

    void setList(ArrayList<bookmarkRowModel> model)
    {
        model_list = model;
    }
    ArrayList<bookmarkRowModel> getList()
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