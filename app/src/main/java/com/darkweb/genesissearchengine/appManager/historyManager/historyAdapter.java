package com.darkweb.genesissearchengine.appManager.historyManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

import java.util.ArrayList;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<historyRowModel> model_list;
    private ArrayList<historyRowModel> temp_model_list;
    private eventObserver.eventListener event;
    private String filter = strings.emptyStr;

    historyAdapter(ArrayList<historyRowModel> model_list, eventObserver.eventListener event) {
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
    public void onBindViewHolder(@NonNull historyAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(temp_model_list.get(position));
        clearMessageItem(holder.messageButton,position);
    }

    @Override
    public int getItemCount() {
        return temp_model_list.size();
    }

    /*Listeners*/

    private void setItemViewOnClickListener(View itemView,int id, String url)
    {
        itemView.setOnClickListener(v ->
        {
            event.invokeObserver(url,enums.history_eventType.url_triggered);
        });
    }

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        clearButton.setOnClickListener(v ->
        {
            if(temp_model_list.size()>index){
                int index_temp = temp_model_list.get(index).getId();
                event.invokeObserver(temp_model_list.get(index).getHeader(),enums.history_eventType.url_clear_at);
                event.invokeObserver(model_list.get(index_temp).getId(),enums.history_eventType.remove_from_database);
                event.invokeObserver(temp_model_list.get(index).getId(),enums.history_eventType.url_clear);
                invokeFilter(false);
                event.invokeObserver(index,enums.history_eventType.is_empty);
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

        void bindListView(historyRowModel model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);
            itemContainer = itemView.findViewById(R.id.item_container);

            String header = model.getHeader();

            descriptionText.setText(model.getDescription());
            heaaderText.setText(model.getHeader());
            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(itemContainer,model.getId(),header);
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        temp_model_list.clear();
        for(int counter=0;counter<model_list.size();counter++){
            if(model_list.get(counter).getHeader().contains(filter)){
                historyRowModel model = model_list.get(counter);
                temp_model_list.add(new historyRowModel(model.getHeader(),model.getDescription(),counter));
            }
        }

        if(notify){
            notifyDataSetChanged();
        }
    }
}
