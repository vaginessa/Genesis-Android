package com.darkweb.genesissearchengine.appManager.historyManager;

public class historyRowModel
{
    /*Private Variables*/

    private int mId;
    private String mHeader;
    private String mDescription;

    /*Initializations*/

    public historyRowModel(String mHeader, String mDescription,int mId) {
        this.mId = mId;
        this.mHeader = mHeader;
        this.mDescription = mDescription;
    }

    /*Variable Getters*/

    public String getmHeader() {
        return mHeader;
    }
    String getmDescription() {
        return mDescription;
    }
    public int getmId() {
        return mId;
    }
}
