package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("CommitPrefEdits")
class dataModel
{
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    private ArrayList<historyRowModel> history = new ArrayList<>();
    private ArrayList<bookmarkRowModel> bookmarks = new ArrayList<>();
    private ArrayList<String> suggestions = new ArrayList<>();
    private Map<String, Boolean> history_cache = new HashMap<String, Boolean>();
    int max_history_id = 0;
    int history_size = 0;

    dataModel(AppCompatActivity app_context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        edit = prefs.edit();
    }

    void setString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    void setBool(String valueKey, boolean value)
    {
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    String getString(String valueKey, String valueDefault)
    {
        return prefs.getString(valueKey, valueDefault);
    }

    boolean getBool(String valueKey, boolean valueDefault)
    {
        return prefs.getBoolean(valueKey, valueDefault);
    }

    /*List Bookmarks and History*/

    void initializeHistory(ArrayList<historyRowModel> history){
        this.history = history;
        initializeCache(history);
        // homeModel.getInstance().getHomeInstance().reInitializeSuggestion();
    }
    void initializeCache(ArrayList<historyRowModel> history){
        for(int count=0;count<=history.size()-1;count++){
            history_cache.put(history.get(count).getHeader(),true);
            suggestions.add(history.get(count).getHeader());
        }
    }
    void addHistory(String url) {

        SimpleDateFormat d_form = new SimpleDateFormat("dd MMMM | hh:mm a");
        String date = d_form.format(new Date());

        Object url_exists = history_cache.get(url);
        if(url_exists!=null){
            for(int count =0 ;count<history.size();count++){
                historyRowModel model = history.get(count);
                if(model.getHeader().equals(url)){
                    history.remove(count);
                    history.add(0,model);
                    databaseController.getInstance().execSQL("UPDATE history SET date = '"+date+"' WHERE id="+model.getId(),null);
                    break;
                }
            }
            return;
        }
        else {
            suggestions.add(0,url);
        }

        if(history_size> constants.max_list_data_size)
        {
            databaseController.getInstance().execSQL("DELETE FROM history WHERE id IN (SELECT id FROM history ORDER BY id ASC LIMIT "+(constants.max_list_data_size/2)+")",null);
        }

        String[] params = new String[1];
        params[0] = url;

        max_history_id = max_history_id +1;
        history_size += 1;

        databaseController.getInstance().execSQL("INSERT INTO history(id,date,url) VALUES("+ max_history_id +",'"+date+"',?);",params);
        history.add(0,new historyRowModel(url,date, max_history_id));
        history_cache.put(url,true);
    }
    public ArrayList<historyRowModel> getHistory() {
        return history;
    }

    void setMaxHistoryID(int max_history_id){
        this.max_history_id = max_history_id;
    }

    void setHistorySize(int history_size){
        this.history_size = history_size;
    }

    void removeHistory(String url) {
        history_cache.remove(url);
        history_size -= 1;
    }

    void clearHistory() {
        history.clear();
        history_cache.clear();
    }

    void loadMoreHistory(ArrayList<historyRowModel> history){
        this.history.addAll(history);
        for(int count=0;count<=history.size()-1;count++){
            history_cache.put(history.get(count).getHeader(),true);
        }
    }

    void initializeBookmarks(){
        bookmarks = databaseController.getInstance().selectBookmark();
    }
    void addBookmark(String url, String title){
        int autoval = 0;
        if(bookmarks.size()> constants.max_list_size)
        {
            databaseController.getInstance().execSQL("delete from bookmark where id="+bookmarks.get(bookmarks.size()-1).getId(),null);
            bookmarks.remove(history.size()-1);
        }

        if(bookmarks.size()>0)
        {
            autoval = bookmarks.get(0).getId()+1;
        }

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;

        databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        bookmarks.add(0,new bookmarkRowModel(url,title,autoval));
    }
    ArrayList<bookmarkRowModel> getBookmark(){
        return bookmarks;
    }
    void clearBookmark() {
        bookmarks.clear();
    }

    ArrayList<String> getSuggestions(){
        return suggestions;
    }
    void initSuggestions(){
        suggestions.add("https://youtube.com");
        suggestions.add("https://en.wikipedia.org");
        suggestions.add("https://facebook.com");
        suggestions.add("https://twitter.com");
        suggestions.add("https://amazon.com");
        suggestions.add("https://imdb.com");
        suggestions.add("https://reddit.com");
        suggestions.add("https://pinterest.com");
        suggestions.add("https://ebay.com");
        suggestions.add("https://tripadvisor.com");
        suggestions.add("https://craigslist.org");
        suggestions.add("https://walmart.com");
        suggestions.add("https://instagram.com");
        suggestions.add("https://google.com");
        suggestions.add("https://nytimes.com");
        suggestions.add("https://apple.com");
        suggestions.add("https://linkedin.com");
        suggestions.add("https://indeed.com");
        suggestions.add("https://play.google.com");
        suggestions.add("https://espn.com");
        suggestions.add("https://webmd.com");
        suggestions.add("https://cnn.com");
        suggestions.add("https://homedepot.com");
        suggestions.add("https://etsy.com");
        suggestions.add("https://netflix.com");
        suggestions.add("https://quora.com");
        suggestions.add("https://microsoft.com");
        suggestions.add("https://target.com");
        suggestions.add("https://merriam-webster.com");
        suggestions.add("https://forbes.com");
        suggestions.add("https://mapquest.com");
        suggestions.add("https://nih.gov");
        suggestions.add("https://gamepedia.com");
        suggestions.add("https://yahoo.com");
        suggestions.add("https://healthline.com");
        suggestions.add("https://foxnews.com");
        suggestions.add("https://allrecipes.com");
        suggestions.add("https://quizlet.com");
        suggestions.add("https://weather.com");
        suggestions.add("https://bestbuy.com");
        suggestions.add("https://urbandictionary.com");
        suggestions.add("https://mayoclinic.org");
        suggestions.add("https://aol.com");
        suggestions.add("https://genius.com");
        suggestions.add("https://zillow.com");
        suggestions.add("https://usatoday.com");
        suggestions.add("https://glassdoor.com");
        suggestions.add("https://msn.com");
        suggestions.add("https://rottentomatoes.com");
        suggestions.add("https://lowes.com");
        suggestions.add("https://dictionary.com");
        suggestions.add("https://businessinsider.com");
        suggestions.add("https://usnews.com");
        suggestions.add("https://medicalnewstoday.com");
        suggestions.add("https://britannica.com");
        suggestions.add("https://washingtonpost.com");
        suggestions.add("https://usps.com");
        suggestions.add("https://finance.yahoo.com");
        suggestions.add("https://irs.gov");
        suggestions.add("https://yellowpages.com");
        suggestions.add("https://chase.com");
        suggestions.add("https://retailmenot.com");
        suggestions.add("https://accuweather.com");
        suggestions.add("https://wayfair.com");
        suggestions.add("https://go.com");
        suggestions.add("https://live.com");
        suggestions.add("https://login.yahoo.com");
        suggestions.add("https://steamcommunity.com");
        suggestions.add("https://xfinity.com");
        suggestions.add("https://cnet.com");
        suggestions.add("https://ign.com");
        suggestions.add("https://steampowered.com");
        suggestions.add("https://macys.com");
        suggestions.add("https://wikihow.com");
        suggestions.add("https://mail.yahoo.com");
        suggestions.add("wiktionary.org");
        suggestions.add("https://cbssports.com");
        suggestions.add("https://cnbc.com");
        suggestions.add("https://bankofamerica.com");
        suggestions.add("https://expedia.com");
        suggestions.add("https://wellsfargo.com");
        suggestions.add("https://groupon.com");
        suggestions.add("https://twitch.tv");
        suggestions.add("https://khanacademy.org");
        suggestions.add("https://theguardian.com");
        suggestions.add("https://paypal.com");
        suggestions.add("https://spotify.com");
        suggestions.add("https://att.com");
        suggestions.add("https://nfl.com");
        suggestions.add("https://realtor.com");
        suggestions.add("https://ca.gov");
        suggestions.add("https://goodreads.com");
        suggestions.add("https://office.com");
        suggestions.add("https://ufl.edu");
        suggestions.add("https://mlb.com");
        suggestions.add("https://foodnetwork.com");
        suggestions.add("https://bbc.com");
        suggestions.add("https://apartments.com");
        suggestions.add("https://npr.org");
        suggestions.add("https://wowhead.com");
        suggestions.add("https://duckduckgo.com");
        suggestions.add("https://bing.com");
        suggestions.add("https://google.com");
        suggestions.add("https://boogle.store");
    }



}
