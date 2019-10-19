package com.darkweb.genesissearchengine.appManager.home_activity;

import com.darkweb.genesissearchengine.constants.enums;

import java.util.List;

public class eventObserver
{
    public interface eventListener
    {
        void invokeObserver(List<Object> data, enums.home_eventType e_type);
    }
}
