package com.project.itai.FindAPlace.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.project.itai.FindAPlace.R;
import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.dao.PlacesDao;
import com.project.itai.FindAPlace.adapters.FavoriteAdapter;
import com.project.itai.FindAPlace.utils.MyApp;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IUserFavAction} interface
 * to handle interaction events.
 */
public class FavoritesFrag extends Fragment {
    private IUserActionsOnMap parentActivity;
    protected PlacesDao placesDao;
    private ArrayList<Place> placesData;

    private RecyclerView recyclerView;
    private FavoriteAdapter mAdapter;
    private View fragmentView;
    private boolean isLongClick = false; //assures a long click won't activate a short click






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placesDao = new PlacesDao(getContext());
        placesData = new ArrayList<>();//initializes array list, stays empty until the Search Button

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.favorite_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager - vertical type recycling
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //coupling mAdapter with RecyclerAdapter
        mAdapter = new FavoriteAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
        recyclerView.setAdapter(mAdapter);

        Button loadFavs = fragmentView.findViewById(R.id.load_favs);
        loadFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                placesData = placesDao.getAllPlaces();
                if (!(placesData.size() > 0))
                    Toast.makeText(v.getContext(), "Favorites are Empty", Toast.LENGTH_LONG).show();

                else {
                    //updates the view
                    mAdapter = new FavoriteAdapter(placesData, new OnLocationListener(), new OnLocationLongListener());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

        // Erase the favcrites from the DAO
        Button deleteFav = fragmentView.findViewById(R.id.delete_favs);
        deleteFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placesDao != null) {
                    placesDao.deleteAllPlaces();
                    placesData.clear();
                    mAdapter.notifyDataSetChanged();
                } else {
                    //placesDao has to be regenerated in this case
                    placesDao = new PlacesDao(MyApp.getContext());
                    placesDao.deleteAllPlaces();
                    placesData.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }

        });
        return fragmentView;
    }



    //implements built in listener inside RecyclerAdapter
    private class OnLocationListener implements FavoriteAdapter.Listener {

        @Override
        public void onLocation(Place place) {
            if (isLongClick == false) {   //assures a long click won't activate a short click
                Toast.makeText(getActivity().getApplicationContext(), place.getName() +
                        "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
                LatLng newLocation = new LatLng(place.getLat(), place.getLng());
                parentActivity.onFocusOnLocation(newLocation, place.getName());
            }
            isLongClick = false;

        }
    }
    //implements built in listener inside RecyclerAdapter
    private class OnLocationLongListener implements FavoriteAdapter.LongClickListener {
        @Override
        public boolean onLongLocation(Place place) {
            isLongClick = true;//assures a long click won't activate a short click
            if (placesDao == null)
                placesDao = new PlacesDao(getContext());

            Log.e("ITAI", "Found place in Adapter ");
            //informs details of erased entry
            Toast.makeText(getActivity().getApplicationContext(), "Erasing:" + place.getName() +
                    "\n lat: " + place.getLat() + " lng: " + place.getLng(), Toast.LENGTH_LONG).show();
            if (place != null) {
                long idToErase = placesDao.getPlaceId(place); // deletePlace requires the id of the object
                placesDao.deletePlace(idToErase);
                placesData.remove(place);
                mAdapter.notifyDataSetChanged();
            }
            return false;
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.9
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

//prior to onCreate
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

//post onDestroy
    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;

    }

    public interface IUserActionsOnMap {
        void onFocusOnLocation(LatLng newLocation, String name);
    }


    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("places", placesData);

    }


}



