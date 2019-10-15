package com.darkweb.genesissearchengine.appManager.home_activity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

class AdBlocker {
    private static final Set<String> AD_HOSTS = new HashSet<>();


    AdBlocker(Context context){
        new Thread(){
            public void run(){
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("adServer.txt")))) {
                    String mLine;
                    while ((mLine = reader.readLine()) != null) {
                        AD_HOSTS.add(mLine);
                    }
                } catch (IOException ignored) {
                }
            }
        }.start();
    }

    boolean isAd(String url) {
        try {
            return isAdHost(getHost(url));
        } catch (MalformedURLException e) {
            Log.e("Devangi..", e.toString());
            return false;
        }
    }

    private boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    private String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

}