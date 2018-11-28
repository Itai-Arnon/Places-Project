package com.project.itai.FindAPlace.dao;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.constants.PlacesConstants;
import com.project.itai.FindAPlace.helper.PlacesDBHelper;

import java.util.ArrayList;


/***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/
/*
 * itai arnon  Start OF Work on Places DAO:  22.08,18
 *LEFT not working all the way doesn't load places
 *
 *
 *
 **
 *
 * *************************************************************************************************************/

public class PlacesDao {

    private PlacesDBHelper dbhelper;
    private final static String LOG_TAG = "ITAI";


    public PlacesDao(Context context) {
        dbhelper = new PlacesDBHelper(context, PlacesConstants.DATABASE_NAME, null,
                PlacesConstants.DATABASE_VERSION);
    }

    // Adding a new place, will return 1 if no exception was made
    public long addPlace(Place place) {
        long id;
        //Content values uses put instead of the Result Set object
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues newPlacesValues =  getContentValuesFromBean(place);

        try {
            id = db.insertOrThrow(PlacesConstants.TABLE_NAME, null, newPlacesValues);

        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
        return id;
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/

    public Place getPlace(long id) {
        Place tmp = new Place();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        try {
            Cursor cursor = db.query(PlacesConstants.TABLE_NAME, null, PlacesConstants.PLACES_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);


            if (cursor.moveToFirst()) {
                tmp = getPlaceFromCursor(cursor);
            }


        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }

        return tmp;
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/

    // Update a place - requires the entire bean
    public long updatePlace(Place place) {
        long id;
        SQLiteDatabase db = dbhelper.getWritableDatabase();
       ContentValues values =  getContentValuesFromBean(place);


        try {
            id = db.update(PlacesConstants.TABLE_NAME, values, PlacesConstants.PLACES_ID
                    + "= ?", new String[]{String.valueOf(place.getId())});
        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
        return id;
    }

    public void deletePlace(long id) {//not all searches
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.delete(PlacesConstants.TABLE_NAME, PlacesConstants.PLACES_ID + "=" + id, null);
        db.close();
    }

    /*returns the entire list of places|getPlaceFromCursor aids in moving from the cursor to the ArrayList*/
    public ArrayList<Place> getAllPlaces() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(PlacesConstants.TABLE_NAME, null, null,
                null, null, null, null);
        ArrayList<Place>places = new ArrayList<>();
        while (cursor.moveToNext())
            //getPlaceFromCursor - adds one by one place object translated from cursor
            places.add(getPlaceFromCursor(cursor));
        return places;
    }
    // method to delete all places from the DB (for the menu option: delete all)
    public void deleteAllPlaces() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        try {
            db.delete(PlacesConstants.TABLE_NAME, null, null);

        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        } finally {
            db.close();

        }
    }

    /***********************************************WILL REPLACE DAO AT MAIN FILE***********************************/



    public boolean isSamePlace(String name, String overView) {
        boolean exists = true;
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String[] addrWhereParams = new String[]{name, overView};
        Cursor cursor = db.query(PlacesConstants.TABLE_NAME, null, PlacesConstants.PLACES_NAME +
                        "=?" + " AND " + PlacesConstants.PLACES_ADDRESS + "=?",//, DbConstants.XINDEX
                addrWhereParams, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    // assuring a place exists
    public boolean isExistentInDB(Place place) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();


        Cursor cursor = db.query(PlacesConstants.TABLE_NAME, null,
                PlacesConstants.PLACES_NAME + "=?" + " AND " + PlacesConstants.PLACES_ADDRESS + "=?",
                new String[]{String.valueOf(place.getName()), String.valueOf(place.getAddress())}, null, null, null, null);

        // Check if the Place was found
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public long getPlaceId(Place place) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.query(PlacesConstants.TABLE_NAME, null,
                    PlacesConstants.PLACES_NAME + "=?" + " AND " + PlacesConstants.PLACES_ADDRESS + "=?",
                    new String[]{String.valueOf(place.getName()),
                            String.valueOf(place.getAddress())}, null, null, null, null);
        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        }

        if (cursor.moveToFirst()) { // Check if the Place was found
            long id = cursor.getInt(cursor.getColumnIndex(PlacesConstants.PLACES_ID));
            return id;
        }
        return 0;
    }//end of dao


    //private function that transforms cursor entry to object type place
    private Place getPlaceFromCursor(Cursor cursor) {
        Place tmp = new Place();
        tmp.setId(cursor.getInt(0));// set ID
        tmp.setName(cursor.getString(1));//Subject
        tmp.setLat(cursor.getDouble(2));//getLng
        tmp.setLng(cursor.getDouble(3));//Rati
        tmp.setAddress(cursor.getString(4));//body
        tmp.setCity(cursor.getString(5));//city added near finish
        tmp.setUrlImage(cursor.getString(6));//url
        return tmp;//tmp is the requested favorite
    }

    private ContentValues getContentValuesFromBean(Place place) {
//meant for update and add
        ContentValues values = new ContentValues();

        values.put(PlacesConstants.PLACES_NAME, place.getName());
        values.put(PlacesConstants.PLACES_LATITUDE, place.getLat());
        values.put(PlacesConstants.PLACES_LONGTITUDE, place.getLng());
        values.put(PlacesConstants.PLACES_ADDRESS, place.getAddress());
        values.put(PlacesConstants.PLACES_CITY, place.getCity());//added near finish
        values.put(PlacesConstants.PLACES_URL, place.getUrlImage());
    return values;
    }
}


 /*public List<Place> getAllPlaces() {
        List<Place> places = new ArrayList<>();
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query(PlacesConstants.TABLE_NAME, null, null,
                null, null, null, null);
        try {
            while (cursor.moveToNext())
                //getPlaceFromCursor - adds one by one place object translated from cursor
                places.add(getPlaceFromCursor(cursor));
        } catch (SQLiteException err) {
            Log.e(LOG_TAG, err.getMessage());
            throw err;
        } finally {
            db.close();
            cursor.close();
        }
        return places;
    }*/

