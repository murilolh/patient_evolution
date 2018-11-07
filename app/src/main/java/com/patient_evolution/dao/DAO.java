package com.patient_evolution.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DAO extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patientevolutiondb.sqlite";
    private static final String TABLE_AUTHORIZATION_NAME = "authorization";
    private static final String TABLE_EVOLUTION_NAME = "evolution";
    private static final String TABLE_EMAIL_NAME = "email";
    private static final String CREATE_TABLE_AUTHORIZATION = "create table authorization (id integer primary key autoincrement, authorization text, hospital text, patient text)";
    private static final String CREATE_TABLE_EVOLUTION = "create table evolution (id integer primary key autoincrement, authorization integer, visit text, responsible text, date text, hour text, evolution blob)";
    private static final String CREATE_TABLE_EMAIL = "create table email (id integer primary key autoincrement, email text)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String BIND_ID = "id = ?";
    private static final String DELETE_ALL_EVOLUTION = "DELETE FROM evolution WHERE authorization = ";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AUTHORIZATION = "authorization";
    public static final String COLUMN_VISIT = "visit";
    public static final String COLUMN_HOSPITAL = "hospital";
    public static final String COLUMN_PATIENT = "patient";
    public static final String COLUMN_RESPONSIBLE = "responsible";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_EVOLUTION = "evolution";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ID_AUTHORIZATION = "idAuthorization";

    public DAO(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_AUTHORIZATION);
        db.execSQL(CREATE_TABLE_EVOLUTION);
        db.execSQL(CREATE_TABLE_EMAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTable(TABLE_EMAIL_NAME));
        db.execSQL(dropTable(TABLE_EVOLUTION_NAME));
        db.execSQL(dropTable(TABLE_AUTHORIZATION_NAME));
        onCreate(db);
    }

    public boolean addAuthorization(String authorizationText, String authorizationHospital, String authorizationPatient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUTHORIZATION, authorizationText);
        values.put(COLUMN_HOSPITAL, authorizationHospital);
        values.put(COLUMN_PATIENT, authorizationPatient);
        db.insert(TABLE_AUTHORIZATION_NAME, null, values);
        db.close();
        return true;
    }

    public boolean addEvolution(Integer evolutionAuthorization, String evolutionVisit, String evolutionResponsible, String evolutionDate, String evolutionHour, String evolutionText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUTHORIZATION, evolutionAuthorization);
        values.put(COLUMN_VISIT, evolutionVisit);
        values.put(COLUMN_RESPONSIBLE, evolutionResponsible);
        values.put(COLUMN_DATE, evolutionDate);
        values.put(COLUMN_HOUR, evolutionHour);
        values.put(COLUMN_EVOLUTION, evolutionText);
        db.insert(TABLE_EVOLUTION_NAME, null, values);
        db.close();
        return true;
    }

    public boolean addEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_EMAIL_NAME, null, values);
        db.close();
        return true;
    }

    public boolean updateAuthorization(Integer authorizationId, String authorizationText, String authorizationHospital, String authorizationPatient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUTHORIZATION, authorizationText);
        values.put(COLUMN_HOSPITAL, authorizationHospital);
        values.put(COLUMN_PATIENT, authorizationPatient);
        db.update(TABLE_AUTHORIZATION_NAME, values, BIND_ID, new String[]{Integer.toString(authorizationId)});
        db.close();
        return true;
    }

    public boolean updateEvolution(Integer evolutionId, String evolutionVisit, String evolutionResponsible, String evolutionDate, String evolutionHour, String evolutionTxt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VISIT, evolutionVisit);
        values.put(COLUMN_RESPONSIBLE, evolutionResponsible);
        values.put(COLUMN_DATE, evolutionDate);
        values.put(COLUMN_HOUR, evolutionHour);
        values.put(COLUMN_EVOLUTION, evolutionTxt);
        db.update(TABLE_EVOLUTION_NAME, values, BIND_ID, new String[]{Integer.toString(evolutionId)});
        db.close();
        return true;
    }

    public Integer deleteAuthorization(Integer id) {
        this.getWritableDatabase().execSQL(DELETE_ALL_EVOLUTION + id);
        return delete(id, TABLE_AUTHORIZATION_NAME);
    }

    public Integer deleteEvolution(Integer id) {
        return delete(id, TABLE_EVOLUTION_NAME);
    }

    public void deleteList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(dropTable(TABLE_EVOLUTION_NAME));
        db.execSQL(dropTable(TABLE_AUTHORIZATION_NAME));
        db.execSQL(CREATE_TABLE_AUTHORIZATION);
        db.execSQL(CREATE_TABLE_EVOLUTION);
        db.close();
    }

    public void deleteAllEmail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(dropTable(TABLE_EMAIL_NAME));
        db.execSQL(CREATE_TABLE_EMAIL);
        db.close();
    }

    public Cursor getAuthorization(int id) {
        return this.getWritableDatabase().rawQuery("Select id, authorization, hospital, patient from " + TABLE_AUTHORIZATION_NAME + " where id = " + id + "", null);
    }

    public int getMaxIdAuthorization() {
        Cursor cursor = this.getWritableDatabase().rawQuery("Select MAX(id) as id from " + TABLE_AUTHORIZATION_NAME, null);
        if (!cursor.moveToFirst()) {
            return 0;
        }
        int retorno = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        cursor.close();
        return retorno;
    }

    public Cursor getEvolution(int id) {
        return this.getReadableDatabase().rawQuery(
                " Select e.id, a.authorization, a.hospital, a.patient, e.visit, e.responsible, e.date, e.hour, e.evolution " +
                        " from authorization a, evolution e" +
                        " where e.authorization = a.id and e.id = " + id, null);
    }

    public Cursor getAllAuthorizationEvolutionCursor() {
        return this.getReadableDatabase().rawQuery(
                " Select e.id, a.id as idAuthorization, a.authorization, a.hospital, a.patient," +
                        " e.visit, e.responsible, e.date, e.hour, e.evolution " +
                        " from authorization a, evolution e" +
                        " where e.authorization = a.id and a.id is not null and e.id is not null and a.authorization is not null and e.authorization is not null " +
                        " order by a.id, e.id", null);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
        Cursor cursor = getAllAuthorizationEvolutionCursor();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> authorization = new HashMap<>();
                authorization.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                authorization.put(COLUMN_ID_AUTHORIZATION, cursor.getString(cursor.getColumnIndex(COLUMN_ID_AUTHORIZATION)));
                authorization.put(COLUMN_AUTHORIZATION, cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORIZATION)));
                authorization.put(COLUMN_VISIT, cursor.getString(cursor.getColumnIndex(COLUMN_VISIT)));
                authorization.put(COLUMN_PATIENT, cursor.getString(cursor.getColumnIndex(COLUMN_PATIENT)));
                authorization.put(COLUMN_HOSPITAL, cursor.getString(cursor.getColumnIndex(COLUMN_HOSPITAL)));
                authorization.put(COLUMN_DATE, cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                arraylist.add(authorization);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public ArrayList<HashMap<String, String>> getAllAuthorization() {
        ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
        Cursor cursor = getAllAuthorizationCursor();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> authorization = new HashMap<>();
                authorization.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                authorization.put(COLUMN_AUTHORIZATION, cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORIZATION)));
                authorization.put(COLUMN_PATIENT, cursor.getString(cursor.getColumnIndex(COLUMN_PATIENT)));
                authorization.put(COLUMN_HOSPITAL, cursor.getString(cursor.getColumnIndex(COLUMN_HOSPITAL)));
                arraylist.add(authorization);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public ArrayList<HashMap<String, String>> getAllEvolution(Integer idAuthorization) {
        ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
        Cursor cursor = getAllEvolutionCursor(idAuthorization);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> evolution = new HashMap<>();
                evolution.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                evolution.put(COLUMN_VISIT, cursor.getString(cursor.getColumnIndex(COLUMN_VISIT)));
                evolution.put(COLUMN_DATE, cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                evolution.put(COLUMN_RESPONSIBLE, cursor.getString(cursor.getColumnIndex(COLUMN_RESPONSIBLE)));
                arraylist.add(evolution);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    public ArrayList<HashMap<String, String>> getAllEmail() {
        ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
        Cursor cursor = getAllEmailCursor();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> email = new HashMap<>();
                email.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                email.put(COLUMN_EMAIL, cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                arraylist.add(email);
            } while (cursor.moveToNext());
        }
        return arraylist;
    }

    private Cursor getAllAuthorizationCursor() {
        return this.getReadableDatabase().rawQuery("Select id, authorization, hospital, patient from authorization where id is not null and authorization is not null order by patient, hospital", null);
    }

    private Cursor getAllEvolutionCursor(Integer idAuthorization) {
        return this.getReadableDatabase().rawQuery("Select id, authorization, visit, responsible, date, hour, evolution from evolution where id is not null and authorization = " + idAuthorization + " order by date", null);
    }

    private Cursor getAllEmailCursor() {
        return this.getReadableDatabase().rawQuery("Select id, email from email where id is not null order by id", null);
    }

    private Integer delete(Integer id, String tableName) {
        return this.getWritableDatabase().delete(tableName, BIND_ID, new String[]{Integer.toString(id)});
    }

    private String dropTable(String tableName) {
        return DROP_TABLE + tableName + ";";
    }
}
