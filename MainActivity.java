package com.project.itai.FindAPlace.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import com.project.itai.FindAPlace.beans.Place;
import com.project.itai.FindAPlace.controllers.ControllersFactory;
import com.project.itai.FindAPlace.dao.PlacesDao;
import com.project.itai.FindAPlace.fragments.FavoritesFrag;
import com.project.itai.FindAPlace.fragments.Fragment1;
import com.project.itai.FindAPlace.R;
import com.project.itai.FindAPlace.fragments.Fragment1;
import com.project.itai.FindAPlace.fragments.IUserActionsOnMap;
import com.project.itai.FindAPlace.receiver.PowerConnectionReceiver;
import com.google.android.gms.maps.model.LatLng;
import android.support.v7.app.ActionBar;


public class MainActivity extends AppCompatActivity implements FavoritesFrag.IUserActionsOnMap, Fragment1.IUserActionsOnMap {
    private PowerConnectionReceiver receiver;
    private IUserActionsOnMap userActionsController;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private PlacesDao placesDao = new PlacesDao(this);

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isKmToBeSaved = true;//preferences
    //todo I might want to change to regular SharePref


    // the proposed keys to needed preferences values
    public final String PREF_NAME = "sharedPreferences";
    public final String TEXT = "Shared_Text";
    public final String RADIUS = "radius";
    public final String JSSTRING = "json_table_backup";
    Menu saveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //will create default values for S Pref
        defaultPrefData();//to be expanded in need

        userActionsController = ControllersFactory.createUserInteractionsController(this);


        //Getting a tablet or a mobile controller the factory hides the decision making.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        receiver = new PowerConnectionReceiver();
        registerReceiver(receiver, filter);


        float radiusDataToBeSaved = 500.0f;
        editor = preferences.edit();//opens the editor
        editor.putFloat(RADIUS, radiusDataToBeSaved);
        editor.apply();
        //commit data to editor


        //==06092018
        // Runtime permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If no permission to location ask permission from the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return;
        }


        //  ViewPager and a PagerAdapter
        viewPager = findViewById(R.id.pager); //fragment layout into pager layout
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // We get a reference to the shared preferences object (the file name will be based on the MainActivity.class)

    }

  /*  //todo override removed
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        saveMenu = menu;

        //Also you can do this for sub menu
        //menu.getItem(firstItemIndex).getSubMenu().getItem(subItemIndex).setChecked(true);

        return true;
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
                toast = Toast.makeText(this, "Changed to kilometers", Toast.LENGTH_LONG);
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
                toast = Toast.makeText(this, "Changed to miles", Toast.LENGTH_LONG);
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
                toast = Toast.makeText(this, "Radius set to 1 ", Toast.LENGTH_LONG);
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
                toast = Toast.makeText(this, "Radius set to 5", Toast.LENGTH_LONG);
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
    }*/

    private void defaultPrefData() {
        editor = preferences.edit();
        editor.putFloat(RADIUS, 0f);//default radius
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments;//class var

        public ScreenSlidePagerAdapter(FragmentManager manager) {
            super(manager);
            fragments = new Fragment[2];
            fragments[0] = new Fragment1();
            fragments[1] = new FavoritesFrag();
        }


        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 2;
        }//pager function
        // end of inner class
    }

    //this function activates the function through whichever controller was created in the factory
    public void onFocusOnLocation(LatLng newLocation, String name) {//name is restricted no google places
        // Carrying out the user's request, using our controller
        userActionsController.onFocusOnLocation(newLocation, name);
    }


    public void resetPreferences()
    {
        editor = preferences.edit();//default values
        editor.putString(TEXT, "");
        editor.putString(JSSTRING, "");
        editor.apply();

    }

    @Override
    //the activity stops being visible
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}

