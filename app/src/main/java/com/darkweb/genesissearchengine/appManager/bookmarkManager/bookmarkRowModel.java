package com.darkweb.genesissearchengine.appManager.bookmarkManager;

public class bookmarkRowModel
{
    /*Private Variables*/

    private int id;
    private String header;
    private String description;

    /*Initializations*/

    public bookmarkRowModel(String header, String description, int id) {
        this.id = id;
        this.header = header;
        this.description = description;
    }

    /*Variable Getters*/

    public String getHeader() {
        return header;
    }
    public String getDescription() {
        return description;
    }
    public int getId() {
        return id;
    }
}
