package com.darkweb.genesissearchengine.appManager.settingManager;

import com.darkweb.genesissearchengine.constants.enums;

public class eventObserver
{
    public interface eventListener
    {
        void invokeObserver(Object data, enums.eventType e_type);
    }
}
