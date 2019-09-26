package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

class bookmarkViewController
{
    /*Private Variables*/
    private AppCompatActivity context;

    private ImageView emptyListNotifier;
    private EditText searchBar;
    private RecyclerView listView;
    private Button clearButton;

    /*Initializations*/

    bookmarkViewController(ImageView emptyListNotifier, EditText searchBar, RecyclerView listView, Button clearButton,AppCompatActivity context)
    {
        this.context = context;
        this.emptyListNotifier = emptyListNotifier;
        this.searchBar = searchBar;
        this.listView = listView;
        this.clearButton = clearButton;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(context.getResources().getColor(R.color.blue_dark));
            }
            else {
                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                context.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.white));
            }
        }
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
