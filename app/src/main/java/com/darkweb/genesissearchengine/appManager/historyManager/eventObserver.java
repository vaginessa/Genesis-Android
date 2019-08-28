package com.darkweb.genesissearchengine.appManager.historyManager;

import com.darkweb.genesissearchengine.constants.enums;

public class eventObserver
{
    public interface eventListener
    {
        void invokeObserver(Object data, enums.history_eventType e_type);
    }
}
