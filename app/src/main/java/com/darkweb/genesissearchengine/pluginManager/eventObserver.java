package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.constants.enums;

public class eventObserver
{
    public interface eventListener
    {
        void invokeObserver(Object data, enums.eventType e_type);
    }
}
