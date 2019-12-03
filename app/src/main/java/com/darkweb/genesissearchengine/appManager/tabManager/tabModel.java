package com.darkweb.genesissearchengine.appManager.tabManager;

import java.util.ArrayList;

class tabModel
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList = new ArrayList<>();

    /*Initializations*/

    void setList(ArrayList<tabRowModel> model)
    {
        mModelList = model;
    }
    ArrayList<tabRowModel> getList()
    {
        return mModelList;
    }
    private void removeFromMainList(int index)
    {
        mModelList.remove(index);
    }

    void onManualClear(int index){
         removeFromMainList(index);
    }
    void clearList(){
        mModelList.clear();
    }

}