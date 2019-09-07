package com.darkweb.genesissearchengine.appManager.historyManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

class historyViewController
{
    /*Private Variables*/

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;
    private Button clearButton;
    private ImageButton moreButton;

    /*Initializations*/

    historyViewController(ImageView emptyListNotifier, EditText searchBar, RecyclerView listView, Button clearButton,ImageButton moreButton)
    {
        this.emptyListNotifier = emptyListNotifier;
        this.searchBar = searchBar;
        this.listView = listView;
        this.clearButton = clearButton;
        this.moreButton = moreButton;
    }

    void updateIfListEmpty(int size,int duration){
        if(size>0){
            emptyListNotifier.animate().setDuration(duration).alpha(0f);
            clearButton.animate().setDuration(duration).alpha(1f);
            moreButton.animate().setDuration(duration).alpha(1f);
        }
        else {
            emptyListNotifier.animate().setDuration(duration).alpha(1f);
            clearButton.animate().setDuration(duration).alpha(0f);
            moreButton.animate().setDuration(duration).alpha(0f);
        }
    }

    void updateList(){
        int index = listView.getAdapter().getItemCount()-1;
        listView.getAdapter().notifyDataSetChanged();
        listView.scrollToPosition(index);
    }

    void removeFromList(int index)
    {
        listView.getAdapter().notifyItemRemoved(index);
        listView.getAdapter().notifyItemRangeChanged(index, listView.getAdapter().getItemCount());
    }

    void scrollToBottom(){
        listView.scrollToPosition(listView.getAdapter().getItemCount()-1);
    }

    void clearList(){
        listView.getAdapter().notifyDataSetChanged();
        updateIfListEmpty(listView.getAdapter().getItemCount(),300);
        searchBar.clearFocus();
        searchBar.setText(strings.emptyStr);
    }

}
