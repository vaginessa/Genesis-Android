package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import com.darkweb.genesissearchengine.constants.enums;

public class eventObserver
{
    public interface eventListener
    {
        void invokeObserver(Object data, enums.bookmark_eventType e_type);
    }
}
