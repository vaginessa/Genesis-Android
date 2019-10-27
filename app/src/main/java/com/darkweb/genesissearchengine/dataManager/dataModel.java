package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("CommitPrefEdits")
class dataModel
{
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEdit;

    private ArrayList<historyRowModel> mHistory = new ArrayList<>();
    private ArrayList<bookmarkRowModel> mBookmarks = new ArrayList<>();
    private ArrayList<tabRowModel> mTabs = new ArrayList<>();
    private ArrayList<String> mSuggestions = new ArrayList<>();
    private Map<String, Boolean> mHistoryCache = new HashMap<>();

    private int mMaxHistoryId = 0;
    private int mHistorySize = 0;

    dataModel(AppCompatActivity app_context){
        mPrefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        mEdit = mPrefs.edit();
    }

    /*Prefs Data Model*/

    void setString(String valueKey, String value){
        mEdit.putString(valueKey, value);
        mEdit.commit();
    }
    String getString(String valueKey, String valueDefault){
        return mPrefs.getString(valueKey, valueDefault);
    }

    void setBool(String valueKey, boolean value){
        mEdit.putBoolean(valueKey, value);
        mEdit.commit();
    }
    boolean getBool(String valueKey, boolean valueDefault){
        return mPrefs.getBoolean(valueKey, valueDefault);
    }

    void setFloat(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.commit();
    }
    int getFloat(String valueKey, int valueDefault)
    {
        return mPrefs.getInt(valueKey, valueDefault);
    }


    /*List History*/

    void initializeHistory(ArrayList<historyRowModel> history){
        this.mHistory = history;
        initializeCache(history);
        // homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    private void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){
            mHistoryCache.put(history.get(count).getmHeader(),true);
            mSuggestions.add(history.get(count).getmHeader());
        }
    }
    void addHistory(String url) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat d_form = new SimpleDateFormat("dd MMMM | hh:mm a");
        String date = d_form.format(new Date());

        Object url_exists = mHistoryCache.get(url);
        if(url_exists!=null){
            for(int count = 0; count< mHistory.size(); count++){
                historyRowModel model = mHistory.get(count);
                if(model.getmHeader().equals(url)){
                    mHistory.remove(count);
                    mHistory.add(0,model);
                    databaseController.getInstance().execSQL("UPDATE history SET date = '"+date+"' WHERE id="+model.getmId(),null);
                    break;
                }
            }
            return;
        }
        else {
            mSuggestions.add(0,url);
        }

        if(mHistorySize > constants.MAX_LIST_DATA_SIZE)
        {
            databaseController.getInstance().execSQL("DELETE FROM history WHERE id IN (SELECT id FROM History ORDER BY id ASC LIMIT "+(constants.MAX_LIST_DATA_SIZE /2)+")",null);
        }

        String[] params = new String[1];
        params[0] = url;

        mMaxHistoryId = mMaxHistoryId +1;
        mHistorySize += 1;

        databaseController.getInstance().execSQL("INSERT INTO history(id,date,url) VALUES("+ mMaxHistoryId +",'"+date+"',?);",params);
        mHistory.add(0,new historyRowModel(url,date, mMaxHistoryId));
        mHistoryCache.put(url,true);
    }

    ArrayList<historyRowModel> getmHistory() {
        return mHistory;
    }
    void setMaxHistoryID(int max_history_id){
        this.mMaxHistoryId = max_history_id;
    }
    void setHistorySize(int history_size){
        this.mHistorySize = history_size;
    }
    void removeHistory(String url) {
        mHistoryCache.remove(url);
        mHistorySize -= 1;
    }
    void clearHistory() {
        mHistory.clear();
        mHistoryCache.clear();
    }
    void loadMoreHistory(ArrayList<historyRowModel> history){
        this.mHistory.addAll(history);
        for(int count=0;count<=history.size()-1;count++){
            mHistoryCache.put(history.get(count).getmHeader(),true);
        }
    }

    /*List Bookmark*/

    void initializeBookmarks(){
        mBookmarks = databaseController.getInstance().selectBookmark();
    }
    void addBookmark(String url, String title){
        int autoval = 0;
        if(mBookmarks.size()> constants.MAX_LIST_SIZE)
        {
            databaseController.getInstance().execSQL("delete from bookmark where id="+ mBookmarks.get(mBookmarks.size()-1).getmId(),null);
            mBookmarks.remove(mHistory.size()-1);
        }

        if(mBookmarks.size()>0)
        {
            autoval = mBookmarks.get(0).getmId()+1;
        }

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;

        databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        mBookmarks.add(0,new bookmarkRowModel(url,title,autoval));
    }
    ArrayList<bookmarkRowModel> getBookmark(){
        return mBookmarks;
    }
    void clearBookmark() {
        mBookmarks.clear();
    }

    /*List Tabs*/

    void addTabs(geckoSession mSession){
        mTabs.add(0,new tabRowModel(mSession,mTabs.size()));
    }
    ArrayList<tabRowModel> getTab(){
        return mTabs;
    }
    void clearTab() {
        mTabs.clear();
    }
    void closeTab(geckoSession mSession) {
        for(int counter = 0; counter< mTabs.size(); counter++){
            if(mTabs.get(counter).getSession().getSessionID()==mSession.getSessionID())
            {
                mTabs.remove(counter);
                break;
            }
        }
    }
    tabRowModel getCurrentTab(){
        if(mTabs.size()>0){
            return mTabs.get(0);
        }
        else {
            return null;
        }
    }

    int getTotalTabs(){
        return mTabs.size();
    }

    /*List Suggestion*/

    ArrayList<String> getmSuggestions(){
        return mSuggestions;
    }
    void initSuggestions(){
        mSuggestions.add("https://youtube.com");
        mSuggestions.add("https://en.wikipedia.org");
        mSuggestions.add("https://facebook.com");
        mSuggestions.add("https://twitter.com");
        mSuggestions.add("https://amazon.com");
        mSuggestions.add("https://imdb.com");
        mSuggestions.add("https://reddit.com");
        mSuggestions.add("https://pinterest.com");
        mSuggestions.add("https://ebay.com");
        mSuggestions.add("https://tripadvisor.com");
        mSuggestions.add("https://craigslist.org");
        mSuggestions.add("https://walmart.com");
        mSuggestions.add("https://instagram.com");
        mSuggestions.add("https://google.com");
        mSuggestions.add("https://nytimes.com");
        mSuggestions.add("https://apple.com");
        mSuggestions.add("https://linkedin.com");
        mSuggestions.add("https://indeed.com");
        mSuggestions.add("https://play.google.com");
        mSuggestions.add("https://espn.com");
        mSuggestions.add("https://webmd.com");
        mSuggestions.add("https://cnn.com");
        mSuggestions.add("https://homedepot.com");
        mSuggestions.add("https://etsy.com");
        mSuggestions.add("https://netflix.com");
        mSuggestions.add("https://quora.com");
        mSuggestions.add("https://microsoft.com");
        mSuggestions.add("https://target.com");
        mSuggestions.add("https://merriam-webster.com");
        mSuggestions.add("https://forbes.com");
        mSuggestions.add("https://mapquest.com");
        mSuggestions.add("https://nih.gov");
        mSuggestions.add("https://gamepedia.com");
        mSuggestions.add("https://yahoo.com");
        mSuggestions.add("https://healthline.com");
        mSuggestions.add("https://foxnews.com");
        mSuggestions.add("https://allrecipes.com");
        mSuggestions.add("https://quizlet.com");
        mSuggestions.add("https://weather.com");
        mSuggestions.add("https://bestbuy.com");
        mSuggestions.add("https://urbandictionary.com");
        mSuggestions.add("https://mayoclinic.org");
        mSuggestions.add("https://aol.com");
        mSuggestions.add("https://genius.com");
        mSuggestions.add("https://zillow.com");
        mSuggestions.add("https://usatoday.com");
        mSuggestions.add("https://glassdoor.com");
        mSuggestions.add("https://msn.com");
        mSuggestions.add("https://rottentomatoes.com");
        mSuggestions.add("https://lowes.com");
        mSuggestions.add("https://dictionary.com");
        mSuggestions.add("https://businessinsider.com");
        mSuggestions.add("https://usnews.com");
        mSuggestions.add("https://medicalnewstoday.com");
        mSuggestions.add("https://britannica.com");
        mSuggestions.add("https://washingtonpost.com");
        mSuggestions.add("https://usps.com");
        mSuggestions.add("https://finance.yahoo.com");
        mSuggestions.add("https://irs.gov");
        mSuggestions.add("https://yellowpages.com");
        mSuggestions.add("https://chase.com");
        mSuggestions.add("https://retailmenot.com");
        mSuggestions.add("https://accuweather.com");
        mSuggestions.add("https://wayfair.com");
        mSuggestions.add("https://go.com");
        mSuggestions.add("https://live.com");
        mSuggestions.add("https://login.yahoo.com");
        mSuggestions.add("https://steamcommunity.com");
        mSuggestions.add("https://xfinity.com");
        mSuggestions.add("https://cnet.com");
        mSuggestions.add("https://ign.com");
        mSuggestions.add("https://steampowered.com");
        mSuggestions.add("https://macys.com");
        mSuggestions.add("https://wikihow.com");
        mSuggestions.add("https://mail.yahoo.com");
        mSuggestions.add("wiktionary.org");
        mSuggestions.add("https://cbssports.com");
        mSuggestions.add("https://cnbc.com");
        mSuggestions.add("https://bankofamerica.com");
        mSuggestions.add("https://expedia.com");
        mSuggestions.add("https://wellsfargo.com");
        mSuggestions.add("https://groupon.com");
        mSuggestions.add("https://twitch.tv");
        mSuggestions.add("https://khanacademy.org");
        mSuggestions.add("https://theguardian.com");
        mSuggestions.add("https://paypal.com");
        mSuggestions.add("https://spotify.com");
        mSuggestions.add("https://att.com");
        mSuggestions.add("https://nfl.com");
        mSuggestions.add("https://realtor.com");
        mSuggestions.add("https://ca.gov");
        mSuggestions.add("https://goodreads.com");
        mSuggestions.add("https://office.com");
        mSuggestions.add("https://ufl.edu");
        mSuggestions.add("https://mlb.com");
        mSuggestions.add("https://foodnetwork.com");
        mSuggestions.add("https://bbc.com");
        mSuggestions.add("https://apartments.com");
        mSuggestions.add("https://npr.org");
        mSuggestions.add("https://wowhead.com");
        mSuggestions.add("https://duckduckgo.com");
        mSuggestions.add("https://bing.com");
        mSuggestions.add("https://google.com");
        mSuggestions.add("https://boogle.store");
    }

}
