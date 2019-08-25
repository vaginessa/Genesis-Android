package com.darkweb.genesissearchengine.appManager.home_activity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.orbotManager;
import com.darkweb.genesissearchengine.pluginManager.pluginController;

public class webviewClient
{
    boolean isGeckoView = false;

    public void saveCache(String url)
    {
        if(url.contains("boogle"))
        {
            homeModel.getInstance().addNavigation(url,enums.navigationType.base);
            homeModel.getInstance().addHistory(url);
        }
    }

    public void loadWebViewClient(WebView webview)
    {
        WebViewClient client = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                if(url.contains("advert"))
                {
                    homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                    helperMethod.openPlayStore(url.split("__")[1], homeModel.getInstance().getHomeInstance());
                    return true;
                }

                isGeckoView = false;
                if(homeModel.getInstance().isUrlRepeatable(url,view.getUrl()))
                {
                    homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                    return true;
                }
                if(!url.contains("boogle"))
                {
                    homeModel.getInstance().getHomeInstance().stopHiddenView(false,true);
                    isGeckoView = true;

                    if(pluginController.getInstance().OrbotManagerInit(url))
                    {
                        homeModel.getInstance().getHomeInstance().onloadURL(url,true,true,false);
                    }
                    return true;
                }
                else
                {
                    homeModel.getInstance().getHomeInstance().stopHiddenView(false,true);
                    homeModel.getInstance().addNavigation(url,enums.navigationType.base);
                    homeModel.getInstance().addHistory(url);
                    homeModel.getInstance().getHomeInstance().onRequestTriggered(false,url);
                    return false;
                }
            }
            @Override
            public void onPageFinished(WebView  view, String  url)
            {
                super.onPageFinished(view, url);
                homeModel.getInstance().getHomeInstance().onPageFinished(false);
                homeModel.getInstance().getHomeInstance().onUpdateSearchBarView(url);
                homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(0);
                status.isApplicationLoaded = true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                homeModel.getInstance().getHomeInstance().onInternetErrorView();
            }
        };

        webview.setWebViewClient(client);

        webview.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int newProgress)
            {
                if(!isGeckoView)
                {
                    if(newProgress<95 && newProgress>5)
                    {
                        homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(newProgress);
                    }
                    else if(newProgress<=5)
                    {
                        homeModel.getInstance().getHomeInstance().onProgressBarUpdateView(4);
                    }
                }
            }
        });
    }
}
