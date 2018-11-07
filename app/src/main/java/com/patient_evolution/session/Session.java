package com.patient_evolution.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setMainSort(String sort) {
        prefs.edit().putString("mainSort", sort).apply();
    }

    public String getMainSort() {
        return prefs.getString("mainSort","");
    }

    public void setAllSort(String sort) {
        prefs.edit().putString("allSort", sort).apply();
    }

    public String getAllSort() {
        return prefs.getString("allSort","");
    }

    public void setMainFilter(String mainFilter) {
        prefs.edit().putString("mainFilter", mainFilter).apply();
    }

    public String getMainFilter() {
        return prefs.getString("mainFilter","");
    }

    public void setAllFilter(String allFilter) {
        prefs.edit().putString("allFilter", allFilter).apply();
    }

    public String getAllFilter() {
        return prefs.getString("allFilter","");
    }
}
