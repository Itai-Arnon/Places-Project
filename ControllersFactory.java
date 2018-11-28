package com.project.itai.FindAPlace.controllers;

import android.support.v4.app.FragmentActivity;

import com.project.itai.FindAPlace.fragments.Fragment1;
import com.project.itai.FindAPlace.fragments.IUserActionsOnMap;
import com.project.itai.FindAPlace.R;

public class ControllersFactory {

    public static IUserActionsOnMap createUserInteractionsController(FragmentActivity activity) {

        // Getting a reference to the maps fragment, ONLY FOUND IN THE TABLET MODE
        IUserActionsOnMap mapFragment = (IUserActionsOnMap) activity.getSupportFragmentManager().findFragmentById(R.id.map);

        // If the fragment exists --> Tablet mode
        if (mapFragment != null) {
            return new TabletController(mapFragment);
        }
        else
        {
            // Fragment is null --> Mobile mode
            return new MobileController(activity);
        }


    }
}
