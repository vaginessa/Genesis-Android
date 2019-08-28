package com.darkweb.genesissearchengine.appManager.historyManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.strings;

class historyViewController
{
    /*Private Variables*/

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;
    private Button clearButton;

    /*Initializations*/

    historyViewController(ImageView emptyListNotifier, EditText searchBar, RecyclerView listView, Button clearButton)
    {
        this.emptyListNotifier = emptyListNotifier;
        this.searchBar = searchBar;
        this.listView = listView;
        this.clearButton = clearButton;
    }

    void updateIfListEmpty(int size,int duration){
        if(size>0){
            emptyListNotifier.animate().setDuration(duration).alpha(0f);
            clearButton.animate().setDuration(duration).alpha(1f);
        }
        else {
            emptyListNotifier.animate().setDuration(duration).alpha(1f);
            clearButton.animate().setDuration(duration).alpha(0f);
        }
    }

    void removeFromList(int index)
    {
        listView.getAdapter().notifyItemRemoved(index);
        listView.getAdapter().notifyItemRangeChanged(index, listView.getAdapter().getItemCount());
    }

    void clearList(){
        listView.getAdapter().notifyDataSetChanged();
        updateIfListEmpty(listView.getAdapter().getItemCount(),300);
        searchBar.clearFocus();
        searchBar.setText(strings.emptyStr);
    }

}
