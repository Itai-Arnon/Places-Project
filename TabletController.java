package com.project.itai.FindAPlace.controllers;

import com.project.itai.FindAPlace.fragments.Fragment1;
import com.project.itai.FindAPlace.fragments.IUserActionsOnMap;
import com.google.android.gms.maps.model.LatLng;

class TabletController implements IUserActionsOnMap {

    private IUserActionsOnMap mapsFragment;

    public TabletController(IUserActionsOnMap mapsFragment){
        this.mapsFragment = mapsFragment;
    }

    public void onFocusOnLocation(LatLng newLocation, String name){
        mapsFragment.onFocusOnLocation(newLocation,name);
    }
}
