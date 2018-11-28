package com.project.itai.FindAPlace.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.itai.FindAPlace.constants.PlacesConstants;

public class PlacesDBHelper extends SQLiteOpenHelper {
    private static String LOG_TAG = "PlacesDBHelper";


    public PlacesDBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }
//creation of the SQL DataBase
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Creating all the tables");
        String CREATE_PLACES_TABLE = " CREATE TABLE " + PlacesConstants.TABLE_NAME +
                "(" + PlacesConstants.PLACES_ID + " INTEGER PRIMARY KEY, " + PlacesConstants.PLACES_NAME + " TEXT, "
                + PlacesConstants.PLACES_LATITUDE + " REAL, "+ PlacesConstants.PLACES_LONGTITUDE+ " REAL, "
                + PlacesConstants.PLACES_ADDRESS + " TEXT, " + PlacesConstants.PLACES_CITY+ " TEXT, " + PlacesConstants.PLACES_URL +" TEXT)";


        try {
            db.execSQL(CREATE_PLACES_TABLE);
        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, "Create table exception: " +
                    ex.getMessage());
        }
    }
//Upgrade of the Data Table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int
            newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + PlacesConstants.TABLE_NAME);
        onCreate(db);
    }//end of onUpgrade
}// end of class










