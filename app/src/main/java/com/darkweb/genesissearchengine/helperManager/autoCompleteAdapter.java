package com.darkweb.genesissearchengine.appManager.homeManager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.darkweb.genesissearchengine.helperMethod;
import com.example.myapplication.R;

public class autoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> fullList;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    private int getResultCount=0;
    private LayoutInflater inflater;

    public int getResultCount()
    {
        if(getResultCount>0)
            return 4;
        else
            return getResultCount;
    }

    autoCompleteAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {

        super(context, resource, textViewResourceId, objects);
        fullList = (ArrayList<String>) objects;
        mOriginalValues = new ArrayList<String>(fullList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public String getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        String item = getItem(position);
        if (convertView == null) {
            convertView = view = inflater.inflate(
                    R.layout.hint_view, null);
        }
        TextView myTv = convertView.findViewById(R.id.hintCompletionHeader);

        myTv.setText(helperMethod.urlDesigner(fullList.get(position)));

        String val = fullList.get(position);
        fullList.remove(position);
        fullList.add(0,val);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<String>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<String> values = mOriginalValues;
                int count = values.size();

                ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            getResultCount = results.count;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if(results.values!=null){
                fullList = (ArrayList<String>) results.values;
            }else{
                fullList = new ArrayList<String>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
