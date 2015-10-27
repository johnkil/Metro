package com.devspark.metro;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.devspark.metro.util.IntPreference;
import com.devspark.metro.util.Locations;
import com.devspark.metro.util.SparseArrays;
import com.devspark.metro.widget.MetroMapView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final int REQUEST_CODE_GOOGLE_API = 1;
    private static final int REQUEST_CODE_LOCATION = 2;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String STATE_MAP_VIEW = "map_view";
    private static final String STATE_CITY = "city";

    private MetroMapView mMetroMapView;
    private IntPreference mCityIdPref;
    private SparseArrayCompat<City> mCities;

    private City mCurrentCity;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMetroMapView = (MetroMapView) findViewById(R.id.map);
        FloatingActionButton settingsButton =
                (FloatingActionButton) findViewById(R.id.fab_settings);
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
            mResolvingError = savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
            if (savedInstanceState.containsKey(STATE_CITY)) {
                mCurrentCity = savedInstanceState.getParcelable(STATE_CITY);
                ImageViewState mapState =
                        (ImageViewState) savedInstanceState.getSerializable(STATE_MAP_VIEW);
                mMetroMapView.showMap(mCurrentCity, mapState);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentCity == null && !mResolvingError) mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        if (mCurrentCity != null) {
            outState.putParcelable(STATE_CITY, mCurrentCity);
            outState.putSerializable(STATE_MAP_VIEW, mMetroMapView.getState());
        }
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

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    private void detectCurrentCity() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        City currentCity = getDefaultCity();
        if (lastLocation != null) {
            currentCity = Locations.nearestCity(
                    lastLocation, SparseArrays.asList(mCities), currentCity);
        }
        selectCity(currentCity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GOOGLE_API) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
                //noinspection ResourceType
                detectCurrentCity();
            } else {
                // Permission was denied or request was cancelled
                selectCity(getDefaultCity());
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION);
        } else {
            // Location permission has been granted.
            detectCurrentCity();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_CODE_GOOGLE_API);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
        selectCity(getDefaultCity());
    }

    /* Show a dialog using GoogleApiAvailability.getErrorDialog() for an error message */
    private void showErrorDialog(int errorCode) {
        GoogleApiErrorDialog dialogFragment = GoogleApiErrorDialog.newInstance(errorCode);
        dialogFragment.show(getSupportFragmentManager(), "error_dialog");
    }

    /* Called from GoogleApiErrorDialog when the dialog is dismissed. */
    private void onDialogDismissed() {
        mResolvingError = false;
    }

    public static class GoogleApiErrorDialog extends DialogFragment {
        private static final String ARG_ERROR_CODE = "error_code";

        public static GoogleApiErrorDialog newInstance(int errorCode) {
            GoogleApiErrorDialog dialogFragment = new GoogleApiErrorDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_ERROR_CODE, errorCode);
            dialogFragment.setArguments(args);
            return dialogFragment;
        }

        public GoogleApiErrorDialog() {}

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = getArguments().getInt(ARG_ERROR_CODE);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    getActivity(), errorCode, REQUEST_CODE_GOOGLE_API);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
}