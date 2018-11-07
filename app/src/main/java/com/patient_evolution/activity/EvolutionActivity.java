package com.patient_evolution.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.patient_evolution.dao.DAO;
import com.patient_evolution.R;

public class EvolutionActivity extends AppCompatActivity {

    private int idAuthorization = 0;
    private int idEvolution = 0;
    private TextView authorization;
    private TextView visit;
    private TextView hospital;
    private TextView patient;
    private TextView responsible;
    private TextView date;
    private TextView hour;
    private TextView evolution;
    private Button save;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evolution);

        authorization = (TextView) findViewById(R.id.txtEmail1);
        visit = (TextView) findViewById(R.id.txtVisit);
        hospital = (TextView) findViewById(R.id.txtHospital);
        patient = (TextView) findViewById(R.id.txtPatient);
        responsible = (TextView) findViewById(R.id.txtResponsible);
        date = (TextView) findViewById(R.id.txtData);
        hour = (TextView) findViewById(R.id.txtHour);
        evolution = (TextView) findViewById(R.id.txtEvolution);
        save = (Button) findViewById(R.id.btnSaveEvolution);

        Date today = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        SimpleDateFormat hourFormat = new SimpleDateFormat(getString(R.string.timeFormat));

        date.setText(dateFormat.format(today));
        hour.setText(hourFormat.format(today));

        dao = new DAO(this);

        Bundle extras = getIntent().getExtras();
        {
            idAuthorization = extras.getInt(DAO.COLUMN_ID_AUTHORIZATION);
            int Value = extras.getInt(DAO.COLUMN_ID);

            if (Value > 0) {
                Cursor rs = dao.getEvolution(Value);
                if (rs != null && rs.moveToFirst()) {
                    idEvolution = Value;

                    String strAuthorization = rs.getString(rs.getColumnIndex(DAO.COLUMN_AUTHORIZATION));
                    String strVisit = rs.getString(rs.getColumnIndex(DAO.COLUMN_VISIT));
                    String strHospital = rs.getString(rs.getColumnIndex(DAO.COLUMN_HOSPITAL));
                    String strPatient = rs.getString(rs.getColumnIndex(DAO.COLUMN_PATIENT));
                    String strResponsible = rs.getString(rs.getColumnIndex(DAO.COLUMN_RESPONSIBLE));
                    String strData = rs.getString(rs.getColumnIndex(DAO.COLUMN_DATE));
                    String strHour = rs.getString(rs.getColumnIndex(DAO.COLUMN_HOUR));
                    String strEvolution = rs.getString(rs.getColumnIndex(DAO.COLUMN_EVOLUTION));

                    if (!rs.isClosed()) {
                        rs.close();
                    }

                    authorization.setText(strAuthorization);
                    authorization.setFocusable(false);
                    authorization.setClickable(false);

                    hospital.setText(strHospital);
                    hospital.setFocusable(false);
                    hospital.setClickable(false);

                    patient.setText(strPatient);
                    patient.setFocusable(false);
                    patient.setClickable(false);

                    visit.setText(strVisit);
                    visit.setFocusable(false);
                    visit.setClickable(false);

                    responsible.setText(strResponsible);
                    responsible.setFocusable(false);
                    responsible.setClickable(false);

                    date.setText(strData);
                    date.setFocusable(false);
                    date.setClickable(false);

                    hour.setText(strHour);
                    hour.setFocusable(false);
                    hour.setClickable(false);

                    evolution.setText(strEvolution);
                    evolution.setFocusable(false);
                    evolution.setClickable(false);

                    save.setVisibility(View.INVISIBLE);
                    save.setFocusable(false);
                    save.setClickable(false);
                }
            } else {
                authorization.setText(extras.getCharSequence(DAO.COLUMN_AUTHORIZATION));
                authorization.setFocusable(false);
                authorization.setClickable(false);
                authorization.setEnabled(false);

                hospital.setText(extras.getCharSequence(DAO.COLUMN_HOSPITAL));
                hospital.setFocusable(false);
                hospital.setClickable(false);
                hospital.setEnabled(false);

                patient.setText(extras.getCharSequence(DAO.COLUMN_PATIENT));
                patient.setFocusable(false);
                patient.setClickable(false);
                patient.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.getInt(DAO.COLUMN_ID) > 0) {
                getMenuInflater().inflate(R.menu.menu_evolution, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.editEvolution:
                authorization.setEnabled(false);
                authorization.setFocusableInTouchMode(false);
                authorization.setClickable(false);

                hospital.setEnabled(false);
                hospital.setFocusableInTouchMode(false);
                hospital.setClickable(false);

                patient.setEnabled(false);
                patient.setFocusableInTouchMode(false);
                patient.setClickable(false);

                visit.setEnabled(true);
                visit.setFocusableInTouchMode(true);
                visit.setClickable(true);

                responsible.setEnabled(true);
                responsible.setFocusableInTouchMode(true);
                responsible.setClickable(true);

                date.setEnabled(true);
                date.setFocusableInTouchMode(true);
                date.setClickable(true);

                hour.setEnabled(true);
                hour.setFocusableInTouchMode(true);
                hour.setClickable(true);

                evolution.setEnabled(true);
                evolution.setFocusableInTouchMode(true);
                evolution.setClickable(true);

                save.setVisibility(View.VISIBLE);
                save.setEnabled(true);
                save.setFocusable(true);
                save.setClickable(true);

                return true;
            case R.id.deleteEvolution:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.clear)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dao.deleteEvolution(idEvolution);
                                Bundle extras = getIntent().getExtras();
                                Toast.makeText(getApplicationContext(), getString(R.string.msgDeleted, getString(R.string.evolution)), Toast.LENGTH_SHORT).show();
                                if(extras.getBoolean("originAll")) {
                                    Intent intent = new Intent(getApplicationContext(), AllActivity.class);
                                    startActivity(intent);
                                } else {
                                    Bundle dataBundle = new Bundle();
                                    dataBundle.putInt(DAO.COLUMN_ID, idAuthorization);
                                    Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                                    intent.putExtras(dataBundle);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle(getString(R.string.msgDelete, getString(R.string.evolution)));
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();

        if(extras.getBoolean("originAll")){
            Intent intent = new Intent(getApplicationContext(), AllActivity.class);
            startActivity(intent);
        } else {
            Bundle dataBundle = new Bundle();
            dataBundle.putInt(DAO.COLUMN_ID, idAuthorization);
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            intent.putExtras(dataBundle);
            startActivity(intent);
        }
        finish();
    }

    public void save(View view) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(visit == null || visit.getText() == null || visit.getText().toString().trim().isEmpty() ||
               responsible == null || responsible.getText() == null || responsible.getText().toString().trim().isEmpty() ||
               date == null || date.getText() == null || date.getText().toString().trim().isEmpty() ||
               hour == null || hour.getText() == null || hour.getText().toString().trim().isEmpty() ||
               evolution == null || evolution.getText() == null || evolution.getText().toString().trim().isEmpty()){
                Toast.makeText(getApplicationContext(), R.string.msgAllRequired, Toast.LENGTH_SHORT).show();
            } else if (extras.getInt(DAO.COLUMN_ID) > 0) {
                if (dao.updateEvolution(idEvolution, visit.getText().toString().trim(), responsible.getText().toString().trim(), date.getText().toString().trim(),
                        hour.getText().toString().trim(), evolution.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgUpdated, getString(R.string.evolution)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msgNotUpdate, Toast.LENGTH_SHORT).show();
                }
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(DAO.COLUMN_ID_AUTHORIZATION, idAuthorization);
                dataBundle.putInt(DAO.COLUMN_ID, idEvolution);
                if(extras.getBoolean("originAll")) {
                    dataBundle.putBoolean("originAll", true);
                }
                Intent intent = new Intent(getApplicationContext(), EvolutionActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            } else {
                if (dao.addEvolution(idAuthorization, visit.getText().toString().trim(), responsible.getText().toString().trim(), date.getText().toString().trim(),
                        hour.getText().toString().trim(), evolution.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgSaved, getString(R.string.evolution)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msgNotSave, Toast.LENGTH_SHORT).show();
                }

                if(extras.getBoolean("originAll")) {
                    Intent intent = new Intent(getApplicationContext(), AllActivity.class);
                    startActivity(intent);
                } else {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putInt(DAO.COLUMN_ID, idAuthorization);
                    Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
                finish();
            }
        }
    }
}
