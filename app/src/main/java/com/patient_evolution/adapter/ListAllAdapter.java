package com.patient_evolution.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.patient_evolution.dao.DAO;

public class ListAllAdapter extends ArrayAdapter {

    private List<HashMap<String, String>> original;
    private List<HashMap<String, String>> filtered;
    private Filter filter;
    private Comparator<HashMap<String, String>> comparator;

    public ListAllAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<HashMap<String, String>> original, Comparator<HashMap<String, String>> comparator) {
        super(context, resource, textViewResourceId, original);
        this.original = original;
        this.original = new ArrayList(original);
        this.filtered = new ArrayList(original);
        this.comparator = comparator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText("Authorization: " + filtered.get(position).get(DAO.COLUMN_AUTHORIZATION) + " - Visit: " + filtered.get(position).get(DAO.COLUMN_VISIT) + " - Date: " + filtered.get(position).get(DAO.COLUMN_DATE));
        text2.setText("Hospital: " + filtered.get(position).get(DAO.COLUMN_HOSPITAL) + " - Patient: " + filtered.get(position).get(DAO.COLUMN_PATIENT));
        return view;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AuthorizationFilter();
        }

        return filter;
    }

    public void sortList() {
        Collections.sort(filtered, comparator);

        clear();
        int count = filtered.size();
        for (int i = 0; i < count; i++) {
            HashMap<String, String> item = filtered.get(i);
            add(item);
        }

        if (filtered.size() > 0) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }

    }

    private class AuthorizationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix.length() == 0) {
                List<HashMap<String, String>> list = new ArrayList(original);
                results.values = list;
                results.count = list.size();
            } else {
                final List<HashMap<String, String>> list = original;

                int count = list.size();
                final List<HashMap<String, String>> nlist = new ArrayList(count);

                for (int i = 0; i < count; i++) {
                    final HashMap<String, String> item = list.get(i);
                    final String authorization = item.get(DAO.COLUMN_AUTHORIZATION).toLowerCase();
                    final String visit = item.get(DAO.COLUMN_VISIT).toLowerCase();
                    final String hospital = item.get(DAO.COLUMN_HOSPITAL).toLowerCase();
                    final String patient = item.get(DAO.COLUMN_PATIENT).toLowerCase();
                    final String date = item.get(DAO.COLUMN_DATE).toLowerCase();

                    if (authorization.contains(prefix) || visit.contains(prefix) || hospital.contains(prefix) || patient.contains(prefix) || date.contains(prefix)) {
                        nlist.add(item);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (ArrayList) results.values;
            sortList();
        }
    }
}
