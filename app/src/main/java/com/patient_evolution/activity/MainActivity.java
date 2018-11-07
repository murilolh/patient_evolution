package com.patient_evolution.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.patient_evolution.adapter.ListAuthorizationAdapter;
import com.patient_evolution.R;
import com.patient_evolution.dao.DAO;
import com.patient_evolution.entity.Evolution;
import com.patient_evolution.session.Session;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_CHOOSE_FILE = 1;
    private static final String SORT_AUTHORIZATION = "1";
    private static final String SORT_HOSPITAL = "2";
    private static final String SORT_PATIENT = "3";
    private DAO dao;
    private ListAuthorizationAdapter adapter;
    private Session session;
    private TextView filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        session = new Session(getApplicationContext());

        if (session.getMainSort() == null || session.getMainSort().isEmpty()) {
            session.setMainSort(SORT_PATIENT);
        }
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_CHOOSE_FILE && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                InputStream filepath = super.getContentResolver().openInputStream(uri);
                if (filepath != null) {
                    String linha;
                    Evolution evolution = new Evolution();
                    List<Evolution> listEvolution = new ArrayList<>();
                    boolean isTextEvolution = false;

                    Scanner scanner = new Scanner(filepath).useDelimiter(System.getProperty("line.separator"));
                    while (scanner.hasNext()) {
                        linha = scanner.next();
                        if (linha != null) {
                            if (linha.contains("<idEvolution>")) {
                                evolution = new Evolution();
                            } else if (linha.contains("<authorizationEvolution>")) {
                                evolution.setAuthorization(linha.replace("    <authorizationEvolution>", "").replace("</authorizationEvolution>", ""));
                            } else if (linha.contains("<visitEvolution>")) {
                                evolution.setVisit(linha.replace("    <visitEvolution>", "").replace("</visitEvolution>", ""));
                            } else if (linha.contains("<hospitalEvolution>")) {
                                evolution.setHospital(linha.replace("    <hospitalEvolution>", "").replace("</hospitalEvolution>", ""));
                            } else if (linha.contains("<patientEvolution>")) {
                                evolution.setPatient(linha.replace("    <patientEvolution>", "").replace("</patientEvolution>", ""));
                            } else if (linha.contains("<responsibleEvolution>")) {
                                evolution.setResponsible(linha.replace("    <responsibleEvolution>", "").replace("</responsibleEvolution>", ""));
                            } else if (linha.contains("<dateEvolution>")) {
                                evolution.setDate(linha.replace("    <dateEvolution>", "").replace("</dateEvolution>", ""));
                            } else if (linha.contains("<hourEvolution>")) {
                                evolution.setHour(linha.replace("    <hourEvolution>", "").replace("</hourEvolution>", ""));
                            } else if (linha.contains("<textEvolution>")) {
                                evolution.setEvolution(linha.replace("    <textEvolution>", "").replace("</textEvolution>", ""));
                                isTextEvolution = true;
                            } else if (isTextEvolution) {
                                evolution.setEvolution(evolution.getEvolution() + System.getProperty("line.separator") + linha.replace("</textEvolution>", ""));
                            }

                            if (linha.indexOf("</textEvolution>") > 0) {
                                isTextEvolution = false;
                                listEvolution.add(evolution);
                            }
                        } else {
                            evolution.setEvolution(evolution.getEvolution() + System.getProperty("line.separator"));
                        }
                    }

                    checkNewEvolutions(listEvolution);

                    Toast.makeText(getApplicationContext(), "Evolutions imported!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Intent intent;

        switch (item.getItemId()) {
            case R.id.newAuthorization:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(DAO.COLUMN_ID, 0);

                intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                intent.putExtras(dataBundle);

                startActivityForResult(intent, 0);
                finish();
                return true;
            case R.id.setEmails:
                intent = new Intent(getApplicationContext(), EmailActivity.class);

                startActivityForResult(intent, 0);
                finish();
                return true;
            case R.id.importList:

                Intent chooseFile;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("file/txt");
                startActivityForResult(Intent.createChooser(chooseFile, getString(R.string.msgChooseFile)), ACTIVITY_CHOOSE_FILE);

                return true;
            case R.id.sendList:
                File file = criarArquivo();

                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
                SimpleDateFormat hourFormat = new SimpleDateFormat(getString(R.string.timeFormat));

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");

                final ArrayList<HashMap<String, String>> arrayKeys = dao.getAllEmail();
                String[] stringEmails = new String[arrayKeys.size()];

                for (int i = 0; i < arrayKeys.size(); i++) {
                    stringEmails[i] = arrayKeys.get(i).get(DAO.COLUMN_EMAIL);
                }

                emailIntent.putExtra(Intent.EXTRA_EMAIL, stringEmails);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.msgFileSubject, dateFormat.format(today), hourFormat.format(today)));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msgFileText, dateFormat.format(today), hourFormat.format(today)));
                if (file != null) {
                    Uri uri = Uri.fromFile(file);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                }
                startActivity(Intent.createChooser(emailIntent, getString(R.string.msgChooseEmailApp)));
                Toast.makeText(getApplicationContext(), R.string.msgFileGenerated, Toast.LENGTH_SHORT).show();

                return true;
            case R.id.clearList:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.clear)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                criarArquivo();
                                dao.deleteList();

                                Toast.makeText(getApplicationContext(), R.string.msgClearedList, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle(R.string.msgClearList);
                d.show();
                return true;
            case R.id.viewAll:
                intent = new Intent(getApplicationContext(), AllActivity.class);

                startActivityForResult(intent, 0);
                finish();
                return true;
            case R.id.version:
                Toast.makeText(getApplicationContext(), R.string.versionNumber, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sortAuthorization:
                session.setMainSort(SORT_AUTHORIZATION);
                adapter.sortList();
                return true;
            case R.id.sortHospital:
                session.setMainSort(SORT_HOSPITAL);
                adapter.sortList();
                return true;
            case R.id.sortPatient:
                session.setMainSort(SORT_PATIENT);
                adapter.sortList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }

    private void start() {
        ListView listaAuthorization;
        setContentView(R.layout.activity_main);
        dao = new DAO(this);
        filter = (TextView) findViewById(R.id.txtFilter);
        listaAuthorization = (ListView) findViewById(R.id.listAuthorizations);
        final ArrayList<HashMap<String, String>> arrayKeys = dao.getAllAuthorization();

        adapter = new ListAuthorizationAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, arrayKeys, new AuthorizationComparator());

        listaAuthorization.setAdapter(adapter);
        listaAuthorization.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(DAO.COLUMN_ID, Integer.parseInt(arrayKeys.get(position).get(DAO.COLUMN_ID)));
                Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });
        adapter.sortList();

        if (session.getMainFilter() != null && !session.getMainFilter().isEmpty()) {
            filter.setText(session.getMainFilter());
            adapter.getFilter().filter(session.getMainFilter());
        }

        filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                adapter.getFilter().filter(s.toString());
                session.setMainFilter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private File criarArquivo() {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        SimpleDateFormat hourFormat = new SimpleDateFormat(getString(R.string.timeFormat));
        Cursor cursor = dao.getAllAuthorizationEvolutionCursor();
        String filename = "file" + dateFormat.format(today) + hourFormat.format(today) + ".xml";
        File file;
        try {
            final int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            File root = new File(Environment.getExternalStorageDirectory(), "patient_evolution");
            if (!root.exists()) {
                root.mkdirs();
            }
            file = new File(root, filename);
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, true), getString(R.string.encoding));

            writer.append("<?xml version=\"1.0\" encoding=\"" + getString(R.string.encoding) + "\"?>" + System.getProperty("line.separator"));
            writer.append("<evolutions>" + System.getProperty("line.separator"));
            if (cursor.moveToFirst()) {
                do {
                    writer.append("  <evolution>" + System.getProperty("line.separator"));
                    writer.append("    <idEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_ID)).trim() + "</idEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <authorizationEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_AUTHORIZATION)).trim() + "</authorizationEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <visitEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_VISIT)).trim() + "</visitEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <hospitalEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_HOSPITAL)).trim() + "</hospitalEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <patientEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_PATIENT)).trim() + "</patientEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <responsibleEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_RESPONSIBLE)).trim() + "</responsibleEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <dateEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_DATE)).trim() + "</dateEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <hourEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_HOUR)).trim() + "</hourEvolution>" + System.getProperty("line.separator"));
                    writer.append("    <textEvolution>" + cursor.getString(cursor.getColumnIndex(DAO.COLUMN_EVOLUTION)).trim() + "</textEvolution>" + System.getProperty("line.separator"));
                    writer.append("  </evolution>" + System.getProperty("line.separator"));
                } while (cursor.moveToNext());
            }
            writer.append("</evolutions>");
            writer.flush();
            writer.close();
            return file;
        } catch (FileNotFoundException fnf) {
            Toast.makeText(getApplicationContext(), R.string.msgNotGenerate, Toast.LENGTH_SHORT).show();
            return null;
        } catch (IOException io) {
            Toast.makeText(getApplicationContext(), R.string.msgNotGenerate, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void checkNewEvolutions(List<Evolution> listEvolution) {

        Cursor cursor = dao.getAllAuthorizationEvolutionCursor();
        Evolution lastInsertedAuthorization = new Evolution();
        int idLastAuthorizationInserted = 0;

        for (Evolution evolution : listEvolution) {
            int idFoundAuthorization = 0;
            boolean foundAuthorization = false;
            boolean foundEvolution = false;
            if (cursor.moveToFirst()) {
                do {
                    if (evolution.getAuthorization().equals(cursor.getString(cursor.getColumnIndex(DAO.COLUMN_AUTHORIZATION))) &&
                            evolution.getHospital().equals(cursor.getString(cursor.getColumnIndex(DAO.COLUMN_HOSPITAL))) &&
                            evolution.getPatient().equals(cursor.getString(cursor.getColumnIndex(DAO.COLUMN_PATIENT)))) {
                        foundAuthorization = true;
                        idFoundAuthorization = cursor.getInt(cursor.getColumnIndex(DAO.COLUMN_ID_AUTHORIZATION));
                        if (evolution.getVisit().equals(cursor.getString(cursor.getColumnIndex(DAO.COLUMN_VISIT)))) {
                            foundEvolution = true;
                            dao.updateEvolution(cursor.getInt(cursor.getColumnIndex(DAO.COLUMN_ID)), evolution.getVisit(), evolution.getResponsible(), evolution.getDate(), evolution.getHour(), evolution.getEvolution());
                        }
                    }
                } while (cursor.moveToNext());
            }

            if (!foundEvolution && foundAuthorization) {
                dao.addEvolution(idFoundAuthorization, evolution.getVisit(), evolution.getResponsible(), evolution.getDate(), evolution.getHour(), evolution.getEvolution());
            } else if (!foundEvolution) {
                if (!evolution.getAuthorization().equals(lastInsertedAuthorization.getAuthorization()) ||
                        !evolution.getHospital().equals(lastInsertedAuthorization.getHospital()) ||
                        !evolution.getPatient().equals(lastInsertedAuthorization.getPatient())) {
                    lastInsertedAuthorization = evolution;
                    dao.addAuthorization(evolution.getAuthorization(), evolution.getHospital(), evolution.getPatient());
                    idLastAuthorizationInserted = dao.getMaxIdAuthorization();
                }
                dao.addEvolution(idLastAuthorizationInserted, evolution.getVisit(), evolution.getResponsible(), evolution.getDate(), evolution.getHour(), evolution.getEvolution());
            }
        }
    }

    private class AuthorizationComparator implements Comparator<HashMap<String, String>> {

        public int compare(HashMap<String, String> c1, HashMap<String, String> c2) {
            if (SORT_AUTHORIZATION.equals(session.getMainSort())) {
                return c1.get(DAO.COLUMN_AUTHORIZATION).toLowerCase().compareTo(c2.get(DAO.COLUMN_AUTHORIZATION).toLowerCase());
            } else if (SORT_HOSPITAL.equals(session.getMainSort())) {
                return c1.get(DAO.COLUMN_HOSPITAL).toLowerCase().compareTo(c2.get(DAO.COLUMN_HOSPITAL).toLowerCase());
            } else if (SORT_PATIENT.equals(session.getMainSort())) {
                return c1.get(DAO.COLUMN_PATIENT).toLowerCase().compareTo(c2.get(DAO.COLUMN_PATIENT).toLowerCase());
            } else {
                return 0;
            }

        }
    }
}
