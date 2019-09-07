package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

import java.util.ArrayList;

public class bookmarkAdapter extends RecyclerView.Adapter<bookmarkAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> model_list;
    private ArrayList<bookmarkRowModel> temp_model_list;
    private eventObserver.eventListener event;
    private String filter = strings.emptyStr;

    bookmarkAdapter(ArrayList<bookmarkRowModel> model_list, eventObserver.eventListener event) {
        this.model_list = model_list;
        this.event = event;
        temp_model_list = new ArrayList<>();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmarkAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(temp_model_list.get(position));
        clearMessageItem(holder.messageButton,position);
    }

    @Override
    public int getItemCount() {
        return temp_model_list.size();
    }

    /*Listeners*/

    private void setItemViewOnClickListener(View itemView, String url)
    {
        itemView.setOnClickListener(v ->
        {
            event.invokeObserver(url,enums.bookmark_eventType.url_triggered);
        });
    }

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        clearButton.setOnClickListener(v ->
        {
            if(temp_model_list.size()>index){
                event.invokeObserver(model_list.get(temp_model_list.get(index).getId()).getId(),enums.bookmark_eventType.remove_from_database);
                event.invokeObserver(temp_model_list.get(index).getId(),enums.bookmark_eventType.url_clear);
                invokeFilter(false);
                event.invokeObserver(index,enums.bookmark_eventType.is_empty);
            }
        });
    }

    /*View Holder Extensions*/

    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView heaaderText;
        TextView descriptionText;
        ImageButton messageButton;
        ImageView empty_message;
        LinearLayout itemContainer;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(bookmarkRowModel model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);
            itemContainer = itemView.findViewById(R.id.item_container);

            String header = model.getHeader();

            descriptionText.setText(model.getHeader());
            heaaderText.setText(model.getDescription());
            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(itemContainer,header);
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        temp_model_list.clear();
        for(int counter=0;counter<model_list.size();counter++){
            if(model_list.get(counter).getHeader().contains(filter) || model_list.get(counter).getDescription().contains(filter)){
                bookmarkRowModel model = model_list.get(counter);
                temp_model_list.add(new bookmarkRowModel(model.getHeader(),model.getDescription(),counter));
            }
        }

        if(notify){
            notifyDataSetChanged();
        }
    }
}
