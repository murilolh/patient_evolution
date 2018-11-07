package com.patient_evolution.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.patient_evolution.R;
import com.patient_evolution.dao.DAO;

public class EmailActivity extends AppCompatActivity {

    private DAO dao;

    private TextView email1;
    private TextView email2;
    private TextView email3;
    private TextView email4;
    private TextView email5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        start();
    }

    public void save(View view) {

        dao.deleteAllEmail();

        for (int i = 1; i <= 5; i++) {
            if (i == 1 && !"".equals(email1.getText().toString())) {
                dao.addEmail(email1.getText().toString().trim());
            } else if (i == 2 && !"".equals(email2.getText().toString())) {
                dao.addEmail(email2.getText().toString().trim());
            } else if (i == 3 && !"".equals(email3.getText().toString())) {
                dao.addEmail(email3.getText().toString().trim());
            } else if (i == 4 && !"".equals(email4.getText().toString())) {
                dao.addEmail(email1.getText().toString().trim());
            } else if (i == 5 && !"".equals(email5.getText().toString())) {
                dao.addEmail(email5.getText().toString().trim());
            }
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(getApplicationContext(), getString(R.string.msgSaved, getString(R.string.emails)), Toast.LENGTH_SHORT).show();
    }

    private void start() {
        setContentView(R.layout.activity_email);

        dao = new DAO(this);
        final ArrayList<HashMap<String, String>> arrayKeys = dao.getAllEmail();

        email1 = (TextView) findViewById(R.id.txtEmail1);
        email2 = (TextView) findViewById(R.id.txtEmail2);
        email3 = (TextView) findViewById(R.id.txtEmail3);
        email4 = (TextView) findViewById(R.id.txtEmail4);
        email5 = (TextView) findViewById(R.id.txtEmail5);

        int i = 1;
        for (HashMap<String, String> key : arrayKeys) {
            if (i == 1) {
                email1.setText(key.get(DAO.COLUMN_EMAIL));
            } else if (i == 2) {
                email2.setText(key.get(DAO.COLUMN_EMAIL));
            } else if (i == 3) {
                email3.setText(key.get(DAO.COLUMN_EMAIL));
            } else if (i == 4) {
                email4.setText(key.get(DAO.COLUMN_EMAIL));
            } else if (i == 5) {
                email5.setText(key.get(DAO.COLUMN_EMAIL));
            }
            i++;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
