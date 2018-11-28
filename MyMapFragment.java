package com.project.itai.FindAPlace.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.project.itai.FindAPlace.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MyMapFragment extends Fragment implements OnMapReadyCallback, IUserActionsOnMap {

    private GoogleMap mMap;
    private LatLng currentLocation;
    private String name;
    private Circle circle;

    private SharedPreferences preferences;

    public float radius;
    public boolean isKm;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieval of Radius and Measure Units
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        radius = preferences.getFloat("radius",1000.0f);

        isKm = preferences.getBoolean("isKm", isKm);

        if (isKm == true) {
            radius = radius * 1.0f;
        } else {
            radius = radius * 1.6f;
        }


        this.currentLocation = new LatLng(32.109333, 34.855499);
        name = "Tel Aviv";

        // Extracrting the intent from the wrapping activity e.g Second Activity/MapActivity
        Intent intent = ((Activity) getContext()).getIntent();

        // Initializing the map's location based on a command sent by the previous activity
        // This code segment is ONLY relevant to mobile mode, and not tablet mode
        String commandName = intent.getStringExtra("commandName");
        if (commandName != null && commandName.equals("focusOnLocation")) {
            double latitude = intent.getDoubleExtra("latitude", 32.109333);
            double longitude = intent.getDoubleExtra("longitude", 34.855499);
            name = intent.getStringExtra("name");
            this.currentLocation = new LatLng(latitude, longitude);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflation of fragment
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        //map fragment is the child fragment of fragment2
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        //TODO preference to measure in KM/Miles

        mapFragment.getMapAsync(this);
        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       //==
        //==
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.currentLocation,13));
        mMap.addMarker(new MarkerOptions().position(this.currentLocation).title(name));

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        //retrieval of Bounding Circle Radius and Measure Units
        radius = preferences.getFloat("radius",1000.0f);
        isKm = preferences.getBoolean("isKm", isKm);

        if (isKm == true) {
            radius = radius * 1.0f;
        } else {
            radius = radius * 1.6f;
        }
        circle  = mMap.addCircle((new CircleOptions().center(this.currentLocation).radius(radius).strokeColor(Color.RED)));

    }
//the full version of onFocusOnLocation
    @Override
    public void onFocusOnLocation(LatLng newLocation, String name) {
        double radius = 100;
        this.currentLocation = newLocation;
        this.name = name;
        if (mMap != null) {
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
            mMap.addMarker(new MarkerOptions().position(newLocation).title(name));
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            radius = preferences.getFloat("radius",1000.0f);
            isKm = preferences.getBoolean("isKm", isKm);

            if (isKm == true) {
                radius = radius * 1.0f;
            } else {
                radius = radius * 1.6f;
            }

            circle = mMap.addCircle((new CircleOptions().center(newLocation).radius(radius).strokeColor(Color.RED)));        }

        }

} // end of class
