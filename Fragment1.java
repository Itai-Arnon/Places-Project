package com.project.itai.FindAPlace.fragments;


// The fragment delegates the click event, to the activity
// Because in this specific excercise, we've decided that the button's behavior context related
// context related = the activity decides (the behavior can change in different activities)

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.ArrayList;


public class Fragment1 extends Fragment  {

    private IUserActionsOnMap parentActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Place> placesData = new ArrayList<>();
    private PlacesDao placesDao;
    private long id = 0;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    // the proposed keys for needed preferences values
    public final String PREF_NAME = "sharedPreferences";
    public final String RADIUS = "radius";
    public final String FAVTABLE = "Shared_Text";
    public final String JSSTRING = "json_string:";

    public boolean isLongClick = false; //assures a long click won't activate a short click

    //todo I might want to change to regular SharePref

    private boolean isKmToBeSaved = true;


    Menu saveMenu;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View fragmentView = inflater.inflate(R.layout.fragment_fragment1, container, false);

        Log.e(PlacesConstants.MY_NAME, "is container null:" + (container == null));
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.search_recycler_view);


        Toolbar toolbarFav = (Toolbar)fragmentView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarFav);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        //linear layout creates are linear type View
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        
        // alternative: preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        //TODO - not working at the moment: string might be null and hence nullifying placesDats
        //retrieval
        Gson gson = new Gson();
        String jsStringRestore = preferences.getString(JSSTRING, null);
        Type type = new TypeToken<ArrayList<Place>>() {
        }.getType();
        if (!TextUtils.isEmpty(jsStringRestore))
            placesData = gson.fromJson(jsStringRestore, type);
      
         if(placesData.size() > 0)
         {
                   mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                   mRecyclerView.setAdapter(mAdapter);
                   mAdapter.notifyDataSetChanged();
         }

//todo should be added under certain conditions. Expanded



        //Search Button
        Button searchButton = (Button) fragmentView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = fragmentView.findViewById(R.id.search_box);
                String newText = text.getText().toString();
                //not sure about replaceAll purpose
                newText = (newText.toUpperCase()).replaceAll(" ", "");
                text.setText(newText);
                //TODO doesnt have an api yet
                if (newText.length() == 0) {
                    Toast toast = Toast.makeText(getView().getContext(), "Search string is empty", Toast.LENGTH_LONG);
                    toast.show();
                }

                if (TextUtils.equals(newText, "HOTEL")) {

                    // todo create an array that adds places to placesData
                    // todo and allows addition only if unique
                    //place has a city field
                    Place[] places = {
                            new Place("Nassau", 25.05, -77.4833, "New Providence", "Bahamas", "tmp"),
                            new Place("Brussels", 50.833, 4.33,"Belgium","", "tmp"),
                            new Place("Vienna", 48.2, 16.337,"Austria", "", "tmp"),
                            new Place("Sydney", 33.8688, 151.2093, "Australia","", "tmp"),
                            new Place("London", 51.507351, -0.127758, "England","", "tmp"),
                            new Place("Tel Aviv", 32.109333, 34.855499, "Israel", "","tmp"),
                            new Place("Dan Tlv Hotel", 32.109333, 34.855499, "Yarkon 22","Tel Aviv", "http:"),
                            new Place("Hilton Tlv Hotel", 32.109333, 34.855499, "Yarkon 10", "Tel Aviv", "http:"),
                            new Place("Dan Eilat Hotel", 29.55805, 34.94821, "N.Coast p.o 176","Eilat", "http:"),
                            new Place("Leonardo Tiberias Hotel", 32.79221, 35.53124,"Habanim St.1", "Tiberias","http:")};



                    for (Place place : places)
                    {
                        if (!placesData.contains(place))
                            placesData.add(place);
                    }
                } else if (TextUtils.equals(newText, "REST")) {
                    Toast.makeText(getView().getContext(), "no addition made", Toast.LENGTH_LONG);

                } else {
                    Toast.makeText(getView().getContext(), "no addition made", Toast.LENGTH_LONG);
                }



              if(placesData.size() > 0) {

                  mAdapter = new SearchAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                  mRecyclerView.setAdapter(mAdapter);

                  mAdapter.notifyDataSetChanged();
                  //insertion
                  Gson gson = new Gson();
                  String jsStringInsert = gson.toJson(placesData);
                 // resetPreferences();
                  editor = preferences.edit();
                  editor.putString(JSSTRING, jsStringInsert);
                  Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + jsStringInsert.toString() + "::");
                  editor.apply();
              }
            }
        });




        return fragmentView;
    }


    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("places", placesData);
    }

    public void onRestoreInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // placesData.clear();
        placesData.addAll((ArrayList<Place>) state.getSerializable("places"));
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
        saveMenu = menu;

        //Also you can do this for sub menu
        //menu.getItem(firstItemIndex).getSubMenu().getItem(subItemIndex).setChecked(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Toast toast;
        float radiusDataToBeSaved = 500.0f;


        // Handle item selection
        switch (item.getItemId()) {

            case R.id.reset_favorite:
                placesDao.deleteAllPlaces();
                return true;

            case R.id.reset_pref:
                resetPreferences();
                return true;



            case R.id.itemKm:
                toast = Toast.makeText(getContext(), "Changed to kilometers", Toast.LENGTH_LONG);
                toast.show();
                isKmToBeSaved = true;
                editor = preferences.edit();
                editor.putBoolean("isKm", isKmToBeSaved);
                // We save the changes to the file
                editor.commit();
                item.setCheckable(true);
                item.setChecked(true);
                saveMenu.findItem(R.id.itemMile).setChecked(false);
                saveMenu.findItem(R.id.itemMile).setCheckable(false);
                return true;

            case R.id.itemMile:
                toast = Toast.makeText(getContext(), "Changed to miles", Toast.LENGTH_LONG);
                toast.show();

                isKmToBeSaved = false;
                editor = preferences.edit();
                editor.putBoolean("isKm", isKmToBeSaved);
                // We save the changes to the file
                editor.commit();
                item.setCheckable(true);
                item.setChecked(true);
                saveMenu.findItem(R.id.itemKm).setChecked(false);
                saveMenu.findItem(R.id.itemKm).setCheckable(false);
                return true;


            case R.id.itemRadius1:
                toast = Toast.makeText(getContext(), "Radius set to 1 ", Toast.LENGTH_LONG);
                toast.show();
                // Set to 1000 m
                radiusDataToBeSaved = 1000.0f;
                editor = preferences.edit();
                editor.putFloat("radius", radiusDataToBeSaved);
                // We save the changes to the file
                editor.commit();
                item.setCheckable(true);
                item.setChecked(true);
                saveMenu.findItem(R.id.itemRadius5).setChecked(false);
                saveMenu.findItem(R.id.itemRadius5).setCheckable(false);
                return true;

            case R.id.itemRadius5:
                toast = Toast.makeText(getContext(), "Radius set to 5 ", Toast.LENGTH_LONG);
                toast.show();
                // Set to 5000 m
                radiusDataToBeSaved = 5000.0f;
                editor = preferences.edit();
                editor.putFloat("radius", radiusDataToBeSaved);
                // We save the changes to the file
                editor.commit();
                item.setCheckable(true);
                item.setChecked(true);
                saveMenu.findItem(R.id.itemRadius1).setChecked(false);
                saveMenu.findItem(R.id.itemRadius1).setCheckable(false);
                return true;

            case R.id.itemQuit:
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    public interface IUserFavAction {
        // TODO: Update argument type and name
        void deleteAllFavorites();
    }


    public interface IUserActionsOnMap {
        void onFocusOnLocation(LatLng newLocation, String name);
    }

    class OnLocationListener implements SearchAdapter.Listener {//listener class Recycler   Adapter

        @Override
        public void onLocation(Place place) {
            if (isLongClick == false) {//assures a long click won't activate a short click
                // We extract the item which the user clicked on (in this example i use an array, may as
                // well be using a list of course
                Toast.makeText(getActivity().getApplicationContext(), place.getName() +
                        "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
                LatLng newLocation = new LatLng(place.getLat(), place.getLng());
                parentActivity.onFocusOnLocation(newLocation, place.getName());
            }
            isLongClick = false;
        }
    }


    // implements item long click. Due to problem with the Dao component I had to realize it as a class
    class OnLocationLongListener implements SearchAdapter.LongClickListener {
        @Override
        public boolean onLongLocation(Place place) {
            isLongClick = true;
            if (placesDao == null)
                placesDao = new PlacesDao(getContext());

            Log.e("MyApp", "Place Retreived " + place.getName());
            Log.d("MyApp", "Place " + place.toString2());




             if(!placesDao.isExistentInDB(place)) {
                 Place newPlace = new Place(place.getName(), place.getLat(), place.getLng(), place.getAddress(),place.getCity(), place.getUrlImage());
                 placesDao.addPlace(newPlace);

                 ArrayList <Place> tmpList= new ArrayList<>();
                 tmpList.add(newPlace);

                 Gson gson = new Gson();
                 String FavWrite = gson.toJson(tmpList);
                 editor = preferences.edit();
                 editor.putString(FAVTABLE, FavWrite);
                 Log.d(PlacesConstants.MY_NAME + "JSSTRING PREF   ", "test pref" + FavWrite.toString() + "::");
                 editor.apply();
                 Toast.makeText(getActivity().getApplicationContext(), "Adding to Favorites: " + place.getName()
                         , Toast.LENGTH_LONG).show();
             }
             else
                 Toast.makeText(getActivity().getApplicationContext(), "Already Exists In Favorites",  Toast.LENGTH_LONG).show();

            //todo add preferences
            return false;
        }

    }

    public void resetPreferences() {
        editor = preferences.edit();//default values
        // editor.putFloat(RADIUS, 0f);
        //  editor.putString(TEXT, "");
        editor.putString(JSSTRING, "");
        editor.apply();

    }

}
