package com.darkweb.genesissearchengine.appManager.bookmarkManager;

public class bookmarkRowModel
{
    /*Private Variables*/

    private int mId;
    private String mHeader;
    private String mDescription;

    /*Initializations*/

    public bookmarkRowModel(String mHeader, String mDescription, int mId) {
        this.mId = mId;
        this.mHeader = mHeader;
        this.mDescription = mDescription;
    }

    /*Variable Getters*/

    public String getmHeader() {
        return mHeader;
    }
    public String getmDescription() {
        return mDescription;
    }
    public int getmId() {
        return mId;
    }
}
