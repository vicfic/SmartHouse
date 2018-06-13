package com.udc.muei.apm.apm_smarthouse.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Manuel González on 11/06/2018 -- 18:08.
 */

public class BeaconsBDHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "beaconsDataBase.db";

    public static final String TABLE_NAME = "TablaBeacons";

    public static final String COLUMN_IDBD = "Id";
    public static final String COLUMN_UUID = "Uuid";
    public static final String COLUMN_GRUPOID = "GrupoId";
    public static final String COLUMN_BEACONID = "BeaconId";
    public static final String COLUMN_LUGARID = "LugarId";
    public static final String COLUMN_RANGO = "Rango";
    public static final String COLUMN_DISTANCIA = "Distancia";
    public static final String COLUMN_NOMBRE_LUGAR = "Nombre_Lugar";
    public static final String COLUMN_NOTIFICADO = "Notificado";


    public BeaconsBDHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_IDBD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UUID + " TEXT, "+
                COLUMN_GRUPOID + " TEXT, " +
                COLUMN_BEACONID + " TEXT, " +
                COLUMN_LUGARID + " INTEGER, "+
                COLUMN_RANGO + " INTEGER, "+
                COLUMN_DISTANCIA + " INTEGER, "+
                COLUMN_NOMBRE_LUGAR + " TEXT, "+
                COLUMN_NOTIFICADO +" TEXT )";
        db.execSQL(CREATE_TABLE);
    }


    public ArrayList<BeaconCustom> loadHandler(){
        String query = "Select * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<BeaconCustom> listBeacons = new ArrayList<>();
        while (cursor.moveToNext()) {

            String uuid = cursor.getString(1);
            String grupoid = cursor.getString(2);
            String beaconid = cursor.getString(3);
            int lugarid = cursor.getInt(4);
            int rango = cursor.getInt(5);
            int distancia = cursor.getInt(6);
            String nombre_lugar = cursor.getString(7);
            int notificado = cursor.getInt(8);

            BeaconCustom beaconCustom = new BeaconCustom(distancia,uuid,grupoid,beaconid,lugarid,rango, nombre_lugar, notificado);

            listBeacons.add(beaconCustom);
        }

        cursor.close();

        db.close();

        return listBeacons;
    }

    public void addBeacon(BeaconCustom beaconCustom){
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID, beaconCustom.getUuid());
        values.put(COLUMN_GRUPOID, beaconCustom.getIdGrupo());
        values.put(COLUMN_BEACONID, beaconCustom.getIdBeacon());
        values.put(COLUMN_LUGARID, beaconCustom.getLugarId());
        values.put(COLUMN_RANGO, beaconCustom.getDistanciaRango());
        values.put(COLUMN_DISTANCIA, beaconCustom.getDistance());
        values.put(COLUMN_NOMBRE_LUGAR, beaconCustom.getNombre_lugar());
        values.put(COLUMN_NOTIFICADO, beaconCustom.getNotificado());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void  addBeacons (ArrayList<BeaconCustom> beacons){

        SQLiteDatabase db = this.getWritableDatabase();
        for (BeaconCustom beacon : beacons){
            ContentValues values = new ContentValues();
            values.put(COLUMN_UUID, beacon.getUuid());
            values.put(COLUMN_GRUPOID, beacon.getIdGrupo());
            values.put(COLUMN_BEACONID, beacon.getIdBeacon());
            values.put(COLUMN_LUGARID, beacon.getLugarId());
            values.put(COLUMN_RANGO, beacon.getDistanciaRango());
            values.put(COLUMN_DISTANCIA, beacon.getDistance());
            values.put(COLUMN_NOMBRE_LUGAR, beacon.getNombre_lugar());
            values.put(COLUMN_NOTIFICADO, beacon.getNotificado());
            db.insert(TABLE_NAME, null, values);

        }
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void deleteBeacons (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

    public int getBeaconRange (int ID){

        String query = "Select * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_IDBD + " = " + "'" + ID + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int range =-1;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            range = cursor.getInt(5);
            cursor.close();
        }

        db.close();

        return range;


    }

    public BeaconCustom getBeacon (int ID){

        String query = "Select * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_IDBD + " = " + "'" + ID + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        BeaconCustom beaconCustom = null;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            String uuid = cursor.getString(1);
            String grupoId = cursor.getString(2);
            String beaconId = cursor.getString(3);
            int lugarId = cursor.getInt(4);
            int range = cursor.getInt(5);
            int distancia = cursor.getInt(6);
            String nombreLugar = cursor.getString(7);
            int notificado = cursor.getInt(8);

            cursor.close();

            beaconCustom = new BeaconCustom(distancia,uuid,grupoId,beaconId,lugarId,range,nombreLugar,notificado);
        }

        db.close();

        return beaconCustom;


    }

    public int findBeacon(String uuid, String grupoId, String beaconId) {

        String query = "Select * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_UUID + " = " + "'" + uuid + "'"+
                " AND "+COLUMN_GRUPOID + " = "+ "'"+ grupoId +"'"+
                " AND "+COLUMN_BEACONID + " = "+"'"+beaconId +"'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);


        int beaconIdBD=-1;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            beaconIdBD = cursor.getInt(0);
            cursor.close();
        }

        db.close();

        return beaconIdBD;

    }

    public boolean updateDistancia(int ID, int distancia) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();

        args.put(COLUMN_IDBD, ID);

        args.put(COLUMN_DISTANCIA, distancia);

        boolean resultado = db.update(TABLE_NAME, args, COLUMN_IDBD + " = " + ID, null) > 0;
        db.close();

        return resultado;

    }

    public boolean updateNotificado(int ID, int notificado) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();

        args.put(COLUMN_IDBD, ID);

        args.put(COLUMN_NOTIFICADO, notificado);

        boolean resultado = db.update(TABLE_NAME, args, COLUMN_IDBD + " = " + ID, null) > 0;

        db.close();
        return resultado;

    }
}
