package com.devspark.metro;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.devspark.metro.util.IntPreference;
import com.devspark.metro.util.Locations;
import com.devspark.metro.util.SparseArrays;
import com.devspark.metro.widget.MetroMapView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String STATE_MAP_VIEW = "map_view";
    private static final String STATE_CITY = "city";

    private MetroMapView mMetroMapView;
    private IntPreference mCityIdPref;
    private GoogleApiClient mGoogleApiClient;

    private SparseArrayCompat<City> mCities;
    private City mCurrentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMetroMapView = (MetroMapView) findViewById(R.id.map);
        FloatingActionButton settingsButton = (FloatingActionButton) findViewById(R.id.fab_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCityChooser();
            }
        });

        mCityIdPref = new IntPreference(getPreferences(MODE_PRIVATE), "city_id");
        initCities();
        buildGoogleApiClient();

        if (savedInstanceState != null) {
            ImageViewState mapState = (ImageViewState) savedInstanceState.getSerializable(STATE_MAP_VIEW);
            mCurrentCity = savedInstanceState.getParcelable(STATE_CITY);
            mMetroMapView.showMap(mCurrentCity, mapState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentCity == null) mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_MAP_VIEW, mMetroMapView.getState());
        outState.putParcelable(STATE_CITY, mCurrentCity);
        super.onSaveInstanceState(outState);
    }

    private void initCities() {
        mCities = new SparseArrayCompat<>(2);
        mCities.append(City.ID_MSK, City.createMoscow(this));
        mCities.append(City.ID_SPB, City.createSpb(this));
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void showCityChooser() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_city)
                .setItems(getCitiesNames(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectCity(mCities.valueAt(which));
                    }
                })
                .create()
                .show();
    }

    private String[] getCitiesNames() {
        String[] names = new String[mCities.size()];
        for (int i = 0; i < mCities.size(); i++) {
            names[i] = mCities.valueAt(i).name;
        }
        return names;
    }

    private void selectCity(City city) {
        if (mCurrentCity != null && mCurrentCity.id == city.id) {
            return;
        }
        mCurrentCity = city;
        mMetroMapView.showMap(city);
        if (!mCityIdPref.isSet() || mCityIdPref.get() != city.id) {
            mCityIdPref.set(city.id);
        }
    }

    private City getDefaultCity() {
        return mCities.get(mCityIdPref.get());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        City currentCity = getDefaultCity();
        if (lastLocation != null) {
            currentCity = Locations.nearestCity(lastLocation, SparseArrays.asList(mCities), currentCity);
        }
        selectCity(currentCity);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        selectCity(getDefaultCity());
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}