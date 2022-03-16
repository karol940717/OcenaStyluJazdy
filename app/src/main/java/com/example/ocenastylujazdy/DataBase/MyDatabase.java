package com.example.ocenastylujazdy.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabase extends SQLiteOpenHelper {


    public MyDatabase(@Nullable Context context, int i) {
        super(context, "sensorsData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table sensZ(id1 integer primary key autoincrement,  accelerationZ);");
        db.execSQL("create table sensZX(id2 integer primary key autoincrement,  accelerationZexceed);");
        db.execSQL("create table Gps(id3 integer primary key autoincrement,  CurrentSpeed);");
        db.execSQL("create table GpsL(id4 integer primary key autoincrement,  SpeedLimit);");
        db.execSQL("create table Throttle(id5 integer primary key autoincrement,  throttlePositon);");
        db.execSQL("create table ThrottleX(id6 integer primary key autoincrement,  throttlePositionexceed);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("drop Table if exists sensD");
    }

    //zapisywanie danych
    public void writeData(Float accelerationZ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accelerationZ", accelerationZ);
        db.insertOrThrow("sensZ", null, values);
    }
    public void writeDataZX( Float accelerationZexceed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accelerationZexceed", accelerationZexceed);
        db.insertOrThrow("sensZX", null, values);
    }
    public void writeDataSpeed( Float CurrentSpeed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CurrentSpeed", CurrentSpeed);
        db.insertOrThrow("Gps", null, values);
    }
    public void writeDataSpeedLimit( Float SpeedLimit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SpeedLimit", SpeedLimit);
        db.insertOrThrow("GpsL", null, values);
    }
    public void writeDataThrottle( Float throttlePositon) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("throttlePositon", throttlePositon);
        db.insertOrThrow("Throttle", null, values);
    }
    public void writeDataThrottleX( Float  throttlePositionexceed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(" throttlePositionexceed",  throttlePositionexceed);
        db.insertOrThrow("ThrottleX", null, values);

    }



    //odczyt danych
    public Cursor getAllData() {
        String[] columns = {"id1", "accelerationZ"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("sensZ", columns, null, null, null, null, null, null);
    }
    public Cursor getAllDataZX() {
        String[] columnsZX = {"id2","accelerationZexceed"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("sensZX", columnsZX, null, null, null, null, null, null);
    }
    public Cursor getAllDataSpeed() {
        String[] columnsSpeed = {"id3","CurrentSpeed"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("Gps", columnsSpeed, null, null, null, null, null, null);
    }
    public Cursor getAllDataSpeedLimit() {
        String[] columnsSpeedLimit = {"id4","SpeedLimit"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("GpsL", columnsSpeedLimit, null, null, null, null, null, null);
    }
    public Cursor getAllDataThrottle() {
        String[] columnsThrottle = {"id5","throttlePositon"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("Throttle", columnsThrottle, null, null, null, null, null, null);
    }
    public Cursor getAllDataThrottleX() {
        String[] columnsThrottleX = {"id6","throttlePositionexceed"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("ThrottleX", columnsThrottleX, null, null, null, null, null, null);
    }

    //usuwanie danych
    public void deleteAllData() {
        SQLiteDatabase db = getWritableDatabase();
        //resetowanie tabeli
        db.delete("sensZ", null, null);
        db.delete("sensZX", null, null);
        db.delete("Gps", null, null);
        db.delete("GpsL", null, null);
        db.delete("Throttle", null, null);
        db.delete("ThrottleX", null, null);
        //resetowanie autoinkrementacji, id
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"sensZ"});
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"sensZX"});
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"Gps"});
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"GpsL"});
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"Throttle"});
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"ThrottleX"});

    }

}
