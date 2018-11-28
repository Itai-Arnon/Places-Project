package com.project.itai.FindAPlace.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.itai.FindAPlace.R;
import com.project.itai.FindAPlace.activities.MainActivity;
import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.dao.PlacesDao;
import com.project.itai.FindAPlace.adapters.FavoriteAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IUserFavAction} interface
 * to handle interaction events.
 */
public class FavoritesFrag extends Fragment implements Fragment1.IUserFavAction
{
    private IUserActionsOnMap parentActivity;
    protected PlacesDao placesDao;
    private ArrayList<Place> placesData;
    private SharedPreferences preferences;

    private RecyclerView recyclerView;
    private FavoriteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isLongClick = false; //assures a long click won't activate a short click



    public final String PREF_NAME = "sharedPreferences";
    public final String RADIUS = "radius";
    public final String JSSTRING = "json_string:";
    public final String TEXT = "Shared_Text";
    private Menu saveMenu;
    private SharedPreferences.Editor editor;
    private boolean isKmToBeSaved;


    public FavoritesFrag() {
        // Required empty public constructor
    }

    public final String FAVTABLE = "Shared_Text";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   View fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);

       preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.favorite_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager - vertical type recycling
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //todo action import
        Toolbar toolbar = (Toolbar)fragmentView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);



        placesDao = new PlacesDao(getContext());
        placesData = new ArrayList<>();


        Button loadFavs = fragmentView.findViewById(R.id.load_favs);
        loadFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (placesDao!=null) {
                   placesData = placesDao.getAllPlaces();
                   if(!(placesData.size() > 0)) Toast.makeText(v.getContext(),"Favorites are Empty",Toast.LENGTH_LONG).show();
                   mAdapter = new FavoriteAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                   recyclerView.setAdapter(mAdapter);
                   mAdapter.notifyDataSetChanged();
               }
            }
        });


        return fragmentView;
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
                deleteAllFavorites();
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
                toast = Toast.makeText(getContext(), "Radius set to 5", Toast.LENGTH_LONG);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IUserActionsOnMap) {
            this.parentActivity = (IUserActionsOnMap) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }

    public void resetPreferences()
    {
        editor = preferences.edit();//default values
        editor.putString(TEXT, "");
        editor.putString(JSSTRING, "");
        editor.apply();
    }
private class OnLocationLongListener implements FavoriteAdapter.LongClickListener {
    @Override
    public boolean onLongLocation(Place place) {
        isLongClick = true;//assures a long click won't activate a short click
        if (placesDao == null)
            placesDao = new PlacesDao(getContext());

        Log.e("ITAI", "Found place in Adapter ");

        Toast.makeText(getActivity().getApplicationContext(), "Erasing:" + place.getName() +
                "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
        if (place != null) {
            long idToErase = placesDao.getPlaceId(place);
            placesDao.deletePlace(idToErase);
            placesData.remove(place);
            mAdapter.notifyDataSetChanged();
        }
        return false;
    }
}

  private class  OnLocationListener implements FavoriteAdapter.Listener{

    @Override
    public void onLocation(Place place) {
        if(isLongClick == false) {   //assures a long click won't activate a short click
            Toast.makeText(getActivity().getApplicationContext(), place.getName() +
                    "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
            LatLng newLocation = new LatLng(place.getLat(), place.getLng());
            parentActivity.onFocusOnLocation(newLocation, place.getName());
        }
        isLongClick = false;

    }
}

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

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


    public interface IUserActionsOnMap {
        void onFocusOnLocation(LatLng newLocation, String name);
    }


    @Override
    public void deleteAllFavorites() {
        if (placesDao == null)
            placesDao = new PlacesDao(getContext());
            placesData = placesDao.getAllPlaces();
        if (placesData.size() > 0) {
            placesDao.deleteAllPlaces();
            placesData.clear();
            mAdapter = new FavoriteAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
           recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }


}