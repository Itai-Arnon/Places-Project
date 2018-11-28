package com.project.itai.FindAPlace.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.project.itai.FindAPlace.R;
import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.dao.PlacesDao;

import java.util.ArrayList;


import android.support.design.widget.Snackbar;

import android.widget.Toast;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<Place> placesData;
    private Listener listener;
    private LongClickListener longListener;
    //instance of the interface
    // private LongClickListener longListener; //instance of the interface


    public SearchAdapter(ArrayList<Place> placesList, Listener listener, LongClickListener longListener) {
        this.placesData = new ArrayList<>();
        this.placesData = placesList;
        this.listener = listener;
        this.longListener = longListener;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView mItemView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_view, parent, false);

        final ViewHolder VH = new ViewHolder(mItemView);


        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Place place = placesData.get(VH.getAdapterPosition());//in case pData is empty or emptied
                if (listener != null && place!=null)
                    listener.onLocation(place);
            }

        });

        mItemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Place place = placesData.get(VH.getAdapterPosition());//in case pData is empty or emptied
                if (longListener != null  && place!=null)
                    longListener.onLongLocation(place);
                return false;
            }
        });
        return VH;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView placeName;
        TextView address1;
        TextView city;
        CardView cardView = holder.mItemView;
        Place place = placesData.get(position);

        if (place != null) {
            placeName = cardView.findViewById(R.id.PlaceName);
            address1 = cardView.findViewById(R.id.address);
            city = cardView.findViewById(R.id.city);
            placeName.setText(place.getName());
            address1.setText(place.getAddress());
            city.setText(place.getCity());


            /*the image is a const at the moment*/
        }

    }

    @Override
    public int getItemCount() {
        return placesData.size();
    }


    public interface Listener {

        void onLocation(Place place);
    }
    public interface LongClickListener {

        boolean onLongLocation(Place place);
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mItemView;

        public ViewHolder(CardView itemView) {
            super(itemView);
            mItemView = itemView;
        }
    }

}


