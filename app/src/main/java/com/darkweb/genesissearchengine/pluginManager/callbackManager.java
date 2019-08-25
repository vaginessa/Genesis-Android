package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.constants.enums;

public class callbackManager
{
    public interface callbackListener
    {
        void callbackSuccess(String data, enums.callbackType callback_type);
        void callbackFailure(int errorCode);
    }
}
