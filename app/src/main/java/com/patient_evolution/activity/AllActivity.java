package com.patient_evolution.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.patient_evolution.adapter.ListAllAdapter;
import com.patient_evolution.R;
import com.patient_evolution.dao.DAO;
import com.patient_evolution.session.Session;

public class AllActivity extends AppCompatActivity {

    private static final String SORT_AUTHORIZATION = "1";
    private static final String SORT_VISIT = "2";
    private static final String SORT_HOSPITAL = "3";
    private static final String SORT_PATIENT = "4";
    private static final String SORT_DATE = "5";
    private DAO dao;
    private ListAllAdapter adapter;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new Session(getApplicationContext());

        if (session.getAllSort() == null || session.getAllSort().isEmpty()) {
            session.setAllSort(SORT_AUTHORIZATION);
        }
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.sortAllAuthorization:
                session.setAllSort(SORT_AUTHORIZATION);
                adapter.sortList();
                return true;
            case R.id.sortAllVisit:
                session.setAllSort(SORT_VISIT);
                adapter.sortList();
                return true;
            case R.id.sortAllHospital:
                session.setAllSort(SORT_HOSPITAL);
                adapter.sortList();
                return true;
            case R.id.sortAllPatient:
                session.setAllSort(SORT_PATIENT);
                adapter.sortList();
                return true;
            case R.id.sortAllDate:
                session.setAllSort(SORT_DATE);
                adapter.sortList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void start() {
        TextView filter;
        ListView listAll;
        setContentView(R.layout.activity_all);
        dao = new DAO(this);
        filter = (TextView) findViewById(R.id.txtFilter);
        listAll = (ListView) findViewById(R.id.listAll);
        final ArrayList<HashMap<String, String>> arrayKeys = dao.getAll();

        adapter = new ListAllAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, arrayKeys, new AllComparator());

        listAll.setAdapter(adapter);
        listAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataBundle = new Bundle();
                dataBundle.putBoolean("originAll", true);
                dataBundle.putInt(DAO.COLUMN_ID_AUTHORIZATION, Integer.parseInt(arrayKeys.get(position).get(DAO.COLUMN_ID_AUTHORIZATION)));
                dataBundle.putInt(DAO.COLUMN_ID, Integer.parseInt(arrayKeys.get(position).get(DAO.COLUMN_ID)));
                Intent intent = new Intent(getApplicationContext(), EvolutionActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });
        adapter.sortList();

        if (session.getAllFilter() != null && !session.getAllFilter().isEmpty()) {
            filter.setText(session.getAllFilter());
            adapter.getFilter().filter(session.getAllFilter());
        }

        filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                session.setAllFilter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private class AllComparator implements Comparator<HashMap<String, String>> {

        public int compare(HashMap<String, String> c1, HashMap<String, String> c2) {
            if (SORT_AUTHORIZATION.equals(session.getAllSort())) {
                return c1.get(DAO.COLUMN_AUTHORIZATION).toLowerCase().compareTo(c2.get(DAO.COLUMN_AUTHORIZATION).toLowerCase());
            } else if (SORT_VISIT.equals(session.getAllSort())) {
                return c1.get(DAO.COLUMN_VISIT).toLowerCase().compareTo(c2.get(DAO.COLUMN_VISIT).toLowerCase());
            } else if (SORT_HOSPITAL.equals(session.getAllSort())) {
                return c1.get(DAO.COLUMN_HOSPITAL).toLowerCase().compareTo(c2.get(DAO.COLUMN_HOSPITAL).toLowerCase());
            } else if (SORT_PATIENT.equals(session.getAllSort())) {
                return c1.get(DAO.COLUMN_PATIENT).toLowerCase().compareTo(c2.get(DAO.COLUMN_PATIENT).toLowerCase());
            } else if (SORT_DATE.equals(session.getAllSort())) {
                return c1.get(DAO.COLUMN_DATE).toLowerCase().compareTo(c2.get(DAO.COLUMN_DATE).toLowerCase());
            } else {
                return 0;
            }

        }
    }
}
