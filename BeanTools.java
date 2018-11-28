package com.project.itai.FindAPlace.utils;

import android.database.Cursor;

import com.project.itai.FindAPlace.beans.Place;

public class BeanTools {
/*
will contain function to move between beans/ bean - cursor

 */
    public Place getFavFromCursor(Cursor cursor) {
        Place tmp = new Place();
        tmp.setId(cursor.getInt(0));// set ID
        tmp.setName(cursor.getString(1));//Subject
        tmp.setAddress(cursor.getString(2));//body
        tmp.setUrlImage(cursor.getString(3));//url
        tmp.setLat(cursor.getDouble(4));//Rating
        tmp.setLng(cursor.getDouble(5));//getLng  integer is converted to boolean
        return tmp;//tmp is the requested movie object
    }

   // public void Place getFavFromJason(){};

}
