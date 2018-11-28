package com.project.itai.FindAPlace.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.project.itai.FindAPlace.fragments.Fragment1;
import com.project.itai.FindAPlace.activities.SecondActivity;
import com.google.android.gms.maps.model.LatLng;
import com.project.itai.FindAPlace.fragments.IUserActionsOnMap;


class MobileController implements IUserActionsOnMap {

    private Activity activity;

    public MobileController(Activity activity){
        this.activity = activity;
    }
    //mobile case - info will be sent to mapFragment via intents
    @Override
    public void onFocusOnLocation(LatLng newLocation ,String name) {
        Intent intent = new Intent(activity, SecondActivity.class);
        intent.putExtra("commandName", "focusOnLocation");

        intent.putExtra("latitude", newLocation.latitude);
        intent.putExtra("longitude", newLocation.longitude);

        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", newLocation.latitude);
        bundle.putDouble("longitude", newLocation.longitude);
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }
}
