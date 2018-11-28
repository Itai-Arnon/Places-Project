package com.project.itai.FindAPlace.fragments;


// The fragment delegates the click event, to the activity
// Because in this specific excercise, we've decided that the button's behavior context related
// context related = the activity decides (the behavior can change in different activities)


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.itai.FindAPlace.R;

import com.project.itai.FindAPlace.adapters.SearchAdapter;
import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.constants.PlacesConstants;
import com.project.itai.FindAPlace.dao.PlacesDao;


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Fragment1 extends Fragment {

    private IUserActionsOnMap parentActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Place> placesData = new ArrayList<>();
    private PlacesDao placesDao;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    //  keys  needed preferences values
    private final String FAVTABLE = "Shared_Text";
    private final String JSSTRING = "json_string:";
    private final String USER_LOC_LATITUDE = "user_lat";
    private final String USER_LOC_LONGTITUDE = "user_long";
    private final String UNIT = "isKm";
    private boolean isLongClick = false; //assures a long click won't activate a short click


    private LocationManager locManager;
    private LocationListener locListener;
    private static Location lastLocation = null;
    private double distanceInMeters;
    private String provider;

    // zero means it checks all the time
    final static int AMOUNT_OF_MS_BETWEEN_LOC_UPDATE = 0;
    private final String PERMISSION_STRING = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private Snackbar userLocationSnackbar;
    private Snackbar distanceFailBar;
    private Snackbar distanceBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //default location
        lastLocation = getDefaultLocation();

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null)
                    lastLocation = location;

                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        //Location Services L Manager Instance
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
// Optional Additions to the service Criteria Object

        /*Crucial for the  function success/work*/
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "Permissions were not Granted \n Make sure the problem is remefied", Toast.LENGTH_LONG).show();
            return;
        }
        //following location provider -- set to update continuously
        //Alternate Version:  locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AMOUNT_OF_MS_BETWEEN_LOC_UPDATE, 0, new locListener());

        //prepare location provider
        provider = locManager.getBestProvider(new Criteria(), true);
        if (provider != null) {
            //request location updates
            locManager.requestLocationUpdates(provider, 1000, 1, locListener);
        } else {
            Toast.makeText(getContext(), "Location Provider Has Failed, Exiting", Toast.LENGTH_LONG).show();
            System.exit(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View fragmentView = inflater.inflate(R.layout.fragment_fragment1, container, false);
        Log.e(PlacesConstants.MY_NAME, "is container null:" + (container == null));
        mRecyclerView = fragmentView.findViewById(R.id.search_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //linear layout creates are linear type View
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //defining  the fragment preferences from the default file name
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        //retrieval of Saved list - the one saved in the search button
        Gson gson = new Gson();
        String jsStringRestore = preferences.getString(JSSTRING, null);
        Type type = new TypeToken<ArrayList<Place>>() {
        }.getType();
        if (!TextUtils.isEmpty(jsStringRestore))
            placesData = gson.fromJson(jsStringRestore, type);

        if (placesData.size() > 0) {
            mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        //Search Button
        Button searchButton = fragmentView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Receiving text
                TextView text = fragmentView.findViewById(R.id.search_box);
                String newText = text.getText().toString();

                //Tranfers text to Uppercase to ease the translation
                newText = (newText.toUpperCase()).replaceAll(" ", "");
                text.setText(newText);

                if (newText.length() == 0) {
                    //in the case nothing was added
                    Toast.makeText(getView().getContext(), "no addition made", Toast.LENGTH_LONG).show();
                } else if (TextUtils.equals(newText, "CITY")) {  //city category
                    Place[] places = {
                            new Place("Nassau", 25.05, -77.4833, "New Providence", "Bahamas", "tmp"),
                            new Place("Brussels", 50.833, 4.33, "Belgium", "", "tmp"),
                            new Place("Vienna", 48.2, 16.337, "Austria", "", "tmp"),
                            new Place("Sydney", -1 * 33.8688, 151.2093, "Australia", "", "tmp"),
                            new Place("London", 51.507351, -0.127758, "England", "", "tmp"),
                            new Place("Tel Aviv", 32.078829, 34.781175, "Israel", "", "tmp")};

                    for (Place place : places) {
                        if (!placesData.contains(place))//assures only unique members enter
                            placesData.add(place);
                    }
                } else if (TextUtils.equals(newText, "HOTEL")) {   //hotels

                    Place[] places = {

                            new Place("Dan Tlv Hotel", 32.064715, 34.763190, "Yarkon 22", "Tel Aviv", "http:"),
                            new Place("Hilton Tlv Hotel", 32.089191, 34.770317, "Yarkon 10", "Tel Aviv", "http:"),
                            new Place("Dan Eilat Hotel", 29.55805, 34.94821, "N.Coast p.o 176", "Eilat", "http:"),
                            new Place("Leonardo Tiberias Hotel", 32.79221, 35.53124, "Habanim St.1", "Tiberias", "http:")};


                    for (Place place : places) {
                        if (!placesData.contains(place))
                            placesData.add(place);
                    }
                } else if (TextUtils.equals(newText, "REST")) {         //restaurants
                    Place[] places = {
                            new Place("Giraffe", 32.076992, 34.781158, "Ibn Gabirol St.49", " Tel Aviv", "tmp"),
                            new Place("River TLV", 32.081441, 34.789787, "Weizmann St.14", "Tel Aviv", "tmp"),
                            new Place("La Cuccina", 29.548784, 34.9638, "Royal Beach Boardwalk", "Eilat", "tmp"),
                            new Place("Avenue Beach Bar", 29.549800, 34.954800, "Pa'amei HaShalom 10", "Eilat", "tmp"),
                            new Place("HaBokrim Stake House", 32.790633, 34.964091, "Kdoshey Yassi St 1", "Haifa", "tmp"),
                            new Place("Ha Chavit", 32.795067, 34.956454, "David Elazar Rd.", "Haifa", "tmp")
                    };

                    for (Place place : places) {
                        if (false == placesData.contains(place))
                            placesData.add(place);
                    }
                }

                if (placesData.size() > 0) {

                    mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //insertion
                    Gson gson = new Gson();
                    String jsStringInsert = gson.toJson(placesData);
                    // resetPreferences();
                    editor = preferences.edit();
                    editor.putString(JSSTRING, jsStringInsert);
                    Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + jsStringInsert + "::");
                    editor.apply();
                }
            }
        });
        // Returns the location of the user
        Button userLocation = fragmentView.findViewById(R.id.user_loc);
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location;
                LatLng yourPosition;

                location = getlastLocation();// the last location is the user location


                if (location != null) {
                    double lat = location.getLatitude(); //location is turned into LatLng object
                    double lng = location.getLongitude();
                    double latitude = Double.parseDouble(new DecimalFormat("##.##").format(lat));
                    double longitude = Double.parseDouble(new DecimalFormat("##.##").format(lng));
                    yourPosition = new LatLng(latitude, longitude);
                    parentActivity.onFocusOnLocation(yourPosition, "User Location");


                    //saving loci in SP: meant for future features
                    editor = preferences.edit();
                    editor.putString(USER_LOC_LONGTITUDE, String.valueOf(location.getLongitude()));
                    editor.putString(USER_LOC_LATITUDE, String.valueOf(location.getLatitude()));
                    editor.apply();

                    //informing the user about his loci. The snackbar will not disappear
                    // until the dismiss button is pressed and so are most of the snackbars
                    userLocationSnackbar = Snackbar.make(getView(), "your latitude is: " + latitude
                                    + "\nyour longitude is: " + longitude,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userLocationSnackbar.dismiss();
                                }
                            });
                    userLocationSnackbar.show();
                } else {
                    Snackbar.make(getView(), "Failed to find your Location",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }

        });

        //clears out the Model , allows more flexibility
        Button newSearch = fragmentView.findViewById(R.id.new_search);
        newSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesData.clear();
                mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Contents Cleared", Toast.LENGTH_LONG).show();
            }
        });

        return fragmentView;
    }

    //The Listener class RecyclerAdapter
    class OnLocationListener implements SearchAdapter.Listener {
        Location targetLocation = new Location(provider);
        Location userLocation;// the requested on focus location to be measure with User Location

        @Override
        public void onLocation(Place place) {
            //assures a long click won't activate a short click
            if (!(isLongClick)) {

                // We extract the item which the user clicked on. And notify his choice
                Toast.makeText(getActivity().getApplicationContext(), place.getName() +
                        "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
                LatLng newLocation = new LatLng(place.getLat(), place.getLng());
                //define  user location
                userLocation = getlastLocation();
                //distance calculation
                if (userLocation != null && place != null) {

                    //transfer loci from place obj to location object
                    targetLocation.setLatitude(place.getLat());
                    targetLocation.setLongitude(place.getLng());
                    //calculate distance part of onLocationChanged method:LocationListener interface
                    double distanceInMeters = targetLocation.distanceTo(userLocation);


                    // via method textBasedOnUnit() arrays both miles and km are accommodated

                    distanceBar = Snackbar.make(getView(), "Distance to " + place.getName() + ": " + textBasedOnUnit(userLocation, targetLocation),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    distanceBar.dismiss();
                                }
                            });
                    distanceBar.show();
                    parentActivity.onFocusOnLocation(newLocation, place.getName());
                    //failure to find distance
                } else {
                    distanceFailBar = Snackbar.make(getView(), "Failed to find Distance",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    distanceFailBar.dismiss();
                                }
                            });
                    distanceFailBar.show();
                    parentActivity.onFocusOnLocation(newLocation, place.getName());
                }
            }
            isLongClick = false; // short click is now allowed
        }
    }

    // implements item long click from the inside of the SearchAdapter
    class OnLocationLongListener implements SearchAdapter.LongClickListener {
        @Override
        public boolean onLongLocation(Place place) {
            isLongClick = true; //assures a long click won't activate a short click
            if (placesDao == null)
                placesDao = new PlacesDao(getContext());

            Log.e("MyApp", "Place Retreived " + place.getName());
            Log.d("MyApp", "Place " + place.toString2());

            //assures similar places will not enter the placesdao
            if (!placesDao.isExistentInDB(place)) {
                Place newPlace = new Place(place.getName(), place.getLat(), place.getLng(), place.getAddress(), place.getCity(), place.getUrlImage());
                placesDao.addPlace(newPlace);

                //Saving favorites to json format
                ArrayList<Place> tmpList = new ArrayList<>();
                tmpList.add(newPlace);
                Gson gson = new Gson();
                String FavWrite = gson.toJson(tmpList);
                //preferences
                editor = preferences.edit();
                editor.putString(FAVTABLE, FavWrite);
                editor.apply();

                //announcing addition to favorite
                Toast.makeText(getActivity().getApplicationContext(), "Adding to Favorites: " + place.getName()
                        , Toast.LENGTH_LONG).show();
                //Log test
                Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + FavWrite+ "::");


            } else
                Toast.makeText(getActivity().getApplicationContext(), "Already Exists In Favorites", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public Location getlastLocation() {

        return lastLocation;
    }

    //default location is Tel Aviv
    public Location getDefaultLocation() {

        Location locationDefault = new Location(provider);
        locationDefault.setLongitude(32.109333);
        locationDefault.setLatitude(34.855499);
        return locationDefault;
    }


    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("places", placesData);
    }

    @Override
    // The context is in fact the activity which hosts the fragment
    // This function is being called after the activity is being created
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IUserActionsOnMap) {
            // If the activity which holds the current fragment, obeys to the rules in the
            // "contract", defined in the interface ("IUserActions"), then we save a
            // reference to the external activity, in order to call it, each time the button
            // had been pressed
            this.parentActivity = (IUserActionsOnMap) context;//retrieves context from MainActivity
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IUserActionsOnMap");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        parentActivity = null;
    }


    // Stops the Location listener
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager != null && locListener != null) {
            if (ContextCompat.checkSelfPermission(getContext(), PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
                locManager.removeUpdates(locListener);
            }
            locManager = null;
            locListener = null;
        }
    }


    public interface IUserActionsOnMap {
        void onFocusOnLocation(LatLng newLocation, String name);
    }

    // Calculates the the distance based on the preferred units and returns it as text
    public String textBasedOnUnit(Location userLoci, Location targetLoci) {

        String UnitName[] = {"km", "mile"};
        String distanceText;
        double distanceInMeters = userLoci.distanceTo(targetLoci);
        double UnitFactor[] =
                {Math.round(distanceInMeters / 1000),
                        ((Math.round(distanceInMeters / (1000 * 1.6))))};

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean kmFlag = preferences.getBoolean(UNIT, false);

        if (kmFlag)
            distanceText = " " + UnitFactor[0] + " " + UnitName[0];
        else
            distanceText = " " + UnitFactor[1] + " " + UnitName[1];
        return distanceText;
    }

}



