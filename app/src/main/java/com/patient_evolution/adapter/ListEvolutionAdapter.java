package com.patient_evolution.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.patient_evolution.dao.DAO;

public class ListEvolutionAdapter extends ArrayAdapter {

    private List<HashMap<String, String>> objects;

    public ListEvolutionAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<HashMap<String, String>> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText("Visit: " + objects.get(position).get(DAO.COLUMN_VISIT));
        text2.setText("Date: " + objects.get(position).get(DAO.COLUMN_DATE) + " - Responsible: " + objects.get(position).get(DAO.COLUMN_RESPONSIBLE));
        return view;
    }
}
