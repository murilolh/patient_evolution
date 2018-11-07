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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.patient_evolution.adapter.ListEvolutionAdapter;
import com.patient_evolution.R;
import com.patient_evolution.dao.DAO;

public class AuthorizationActivity extends AppCompatActivity {

    private int idAuthorization = 0;
    private TextView authorization;
    private TextView hospital;
    private TextView patient;
    private Button save;
    private TextView evolutionTitle;
    private ListView listEvolution;

    private DAO dao;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getInt(DAO.COLUMN_ID) > 0) {
            getMenuInflater().inflate(R.menu.menu_authorization, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.newEvolution:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(DAO.COLUMN_ID, 0);
                dataBundle.putInt(DAO.COLUMN_ID_AUTHORIZATION, idAuthorization);
                dataBundle.putCharSequence(DAO.COLUMN_AUTHORIZATION, authorization.getText());
                dataBundle.putCharSequence(DAO.COLUMN_PATIENT, patient.getText());
                dataBundle.putCharSequence(DAO.COLUMN_HOSPITAL, hospital.getText());

                Intent intent = new Intent(getApplicationContext(), EvolutionActivity.class);
                intent.putExtras(dataBundle);

                startActivityForResult(intent, 0);
                finish();
                return true;
            case R.id.editAuthorization:
                authorization.setEnabled(true);
                authorization.setFocusableInTouchMode(true);
                authorization.setClickable(true);

                hospital.setEnabled(true);
                hospital.setFocusableInTouchMode(true);
                hospital.setClickable(true);

                patient.setEnabled(true);
                patient.setFocusableInTouchMode(true);
                patient.setClickable(true);

                save.setVisibility(View.VISIBLE);
                save.setEnabled(true);
                save.setFocusable(true);
                save.setClickable(true);

                evolutionTitle.setVisibility(View.INVISIBLE);
                evolutionTitle.setFocusable(false);
                evolutionTitle.setClickable(false);

                listEvolution.setVisibility(View.INVISIBLE);
                listEvolution.setFocusable(false);
                listEvolution.setClickable(false);

                return true;
            case R.id.deleteAuthorization:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.clear)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dao.deleteAuthorization(idAuthorization);

                                Toast.makeText(getApplicationContext(), getString(R.string.msgDeleted, getString(R.string.authorization)), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle(R.string.msgDeleteAuthorization);
                d.show();

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

    public void save(View view) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (authorization == null || authorization.getText() == null || authorization.getText().toString().trim().isEmpty() ||
                    hospital == null || hospital.getText() == null || hospital.getText().toString().trim().isEmpty() ||
                    patient == null || patient.getText() == null || patient.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.msgAllRequired, Toast.LENGTH_SHORT).show();
            } else if (extras.getInt(DAO.COLUMN_ID) > 0) {
                if (dao.updateAuthorization(idAuthorization, authorization.getText().toString().trim(), hospital.getText().toString().trim(), patient.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgUpdated, getString(R.string.authorization)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msgNotUpdate, Toast.LENGTH_SHORT).show();
                }
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(DAO.COLUMN_ID, idAuthorization);
                Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            } else {
                if (dao.addAuthorization(authorization.getText().toString().trim(), hospital.getText().toString().trim(), patient.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msgSaved, getString(R.string.authorization)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msgNotSave, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void start() {
        setContentView(R.layout.activity_authorization);

        authorization = (TextView) findViewById(R.id.txtEmail1);
        hospital = (TextView) findViewById(R.id.txtHospital);
        patient = (TextView) findViewById(R.id.txtPatient);
        save = (Button) findViewById(R.id.btnSaveAuthorization);
        evolutionTitle = (TextView) findViewById(R.id.txtTitleEvolutions);
        listEvolution = (ListView) findViewById(R.id.listEvolutions);

        dao = new DAO(this);

        Bundle extras = getIntent().getExtras();
        {
            int Value = extras.getInt(DAO.COLUMN_ID);

            if (Value > 0) {
                Cursor rs = dao.getAuthorization(Value);
                if (rs != null && rs.moveToFirst()) {
                    idAuthorization = Value;

                    String strAuthorization = rs.getString(rs.getColumnIndex(DAO.COLUMN_AUTHORIZATION));
                    String strHospital = rs.getString(rs.getColumnIndex(DAO.COLUMN_HOSPITAL));
                    String strPatient = rs.getString(rs.getColumnIndex(DAO.COLUMN_PATIENT));

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

                    save.setVisibility(View.INVISIBLE);
                    save.setFocusable(false);
                    save.setClickable(false);

                    evolutionTitle.setVisibility(View.VISIBLE);
                    evolutionTitle.setEnabled(true);
                    evolutionTitle.setFocusable(true);
                    evolutionTitle.setClickable(true);

                    final ArrayList<HashMap<String, String>> arrayKeys = dao.getAllEvolution(idAuthorization);

                    ListEvolutionAdapter adapter = new ListEvolutionAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, arrayKeys);

                    listEvolution.setAdapter(adapter);
                    listEvolution.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle dataBundle = new Bundle();
                            dataBundle.putInt(DAO.COLUMN_ID_AUTHORIZATION, idAuthorization);
                            dataBundle.putInt(DAO.COLUMN_ID, Integer.parseInt(arrayKeys.get(position).get(DAO.COLUMN_ID)));
                            Intent intent = new Intent(getApplicationContext(), EvolutionActivity.class);
                            intent.putExtras(dataBundle);
                            startActivity(intent);
                            finish();
                        }
                    });

                    listEvolution.setVisibility(View.VISIBLE);
                    listEvolution.setEnabled(true);
                    listEvolution.setFocusable(true);
                    listEvolution.setClickable(true);

                }
            } else {
                save.setVisibility(View.VISIBLE);
                save.setEnabled(true);
                save.setFocusable(true);
                save.setClickable(true);

                evolutionTitle.setVisibility(View.INVISIBLE);
                evolutionTitle.setFocusable(false);
                evolutionTitle.setClickable(false);

                listEvolution.setVisibility(View.INVISIBLE);
                listEvolution.setFocusable(false);
                listEvolution.setClickable(false);
            }
        }
    }
}
