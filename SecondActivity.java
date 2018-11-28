package com.project.itai.FindAPlace.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.project.itai.FindAPlace.R;

public class SecondActivity extends FragmentActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //moved to Maps Activity
    /*    double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);*/





//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);

        // The fragment will read the new arguments and will initialize itself based on them
//        savedInstanceState.put.setArguments(bundleArguments);
    }


}
