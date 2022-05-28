package com.julo.weatherappjulo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "CityData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table CityFavourite(name TEXT primary key)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists CityFavourite");
    }

    public Boolean insertCityData(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        long result = DB.insert("CityFavourite", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean deleteCityData(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from CityFavourite where name =?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.delete("CityFavourite", "name=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean checkDataExist(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from CityFavourite where name =?", new String[]{name});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor readAllCityData() {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = null;
        if (DB != null) {
            cursor = DB.rawQuery("Select * from CityFavourite", null);
        }
        return cursor;
    }
}
