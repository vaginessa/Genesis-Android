package com.darkweb.genesissearchengine.appManager.tabManager;

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
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

import org.mozilla.geckoview.GeckoSession;

import java.util.ArrayList;
import java.util.Collections;

public class tabAdapter extends RecyclerView.Adapter<tabAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList;
    private ArrayList<tabRowModel> mTempModelList;
    private eventObserver.eventListener mEvent;
    private String filter = strings.EMPTY_STR;

    tabAdapter(ArrayList<tabRowModel> model_list, eventObserver.eventListener event) {
        this.mModelList = model_list;
        this.mEvent = event;
        mTempModelList = new ArrayList<>();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tabAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mTempModelList.get(position));
        clearMessageItem(holder.messageButton,position);
        clearMessageItemContainer(holder.itemContainer,position,holder.data_model);
    }

    @Override
    public int getItemCount() {
        return mTempModelList.size();
    }

    /*Listeners*/

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        clearButton.setOnClickListener(v ->
        {
            if(mTempModelList.size()>index){
                mEvent.invokeObserver(Collections.singletonList(mModelList.get(mTempModelList.get(index).getmId()).getmId()),enums.etype.remove_from_database);
                mEvent.invokeObserver(Collections.singletonList(mTempModelList.get(index).getmId()),enums.etype.url_clear);
                invokeFilter(false);
                mEvent.invokeObserver(Collections.singletonList(index),enums.etype.is_empty);
            }
        });
    }

    private void clearMessageItemContainer(LinearLayout clearButton, int index,tabRowModel model)
    {
        clearButton.setOnClickListener(v ->
        {
            if(mTempModelList.size()>index){
                mEvent.invokeObserver(Collections.singletonList(mModelList.get(mTempModelList.get(index).getmId()).getmId()),enums.etype.remove_from_database);
                mEvent.invokeObserver(Collections.singletonList(mTempModelList.get(index).getmId()),enums.etype.url_clear);
                invokeFilter(false);
                mEvent.invokeObserver(Collections.singletonList(index),enums.etype.is_empty);
                mEvent.invokeObserver(Collections.singletonList(model),enums.etype.url_triggered);
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
        GeckoSession session;
        tabRowModel data_model;
        int mProgress;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(tabRowModel model) {

            heaaderText = itemView.findViewById(R.id.mHeader);
            descriptionText = itemView.findViewById(R.id.mDescription);
            itemContainer = itemView.findViewById(R.id.item_container);

            descriptionText.setText(model.getmDescription());
            heaaderText.setText(model.getmHeader());
            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);
            mProgress = model.getProgress();
            data_model = model;
            session = model.getmSession();
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        mTempModelList.clear();
        for(int counter = 0; counter< mModelList.size(); counter++){
            if(mModelList.get(counter).getmHeader().contains(filter) || mModelList.get(counter).getmDescription().contains(filter)){
                tabRowModel model = mModelList.get(counter);
                mTempModelList.add(new tabRowModel(model.getmSession(),counter,model.getmHeader(),model.getmDescription(),model.getProgress()));
            }
        }

        if(notify){
            notifyDataSetChanged();
        }
    }
}
