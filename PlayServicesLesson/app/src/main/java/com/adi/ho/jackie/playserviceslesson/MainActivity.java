package com.adi.ho.jackie.playserviceslesson;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    GoogleApiClient mGoogleApiClient;
    Tracker mTracker;
    TextView lattitudeText;
    TextView lontitudeText;
    private GoogleMap mMap;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Handles authentication of play services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* AppCompatActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
                .addApi(LocationServices.API)
                .build();

        //map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        lattitudeText = (TextView) findViewById(R.id.lat);
        lontitudeText = (TextView) findViewById(R.id.lon);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        //Detects if user has google play services, add whatever here to show
        Log.i("Main", "" + connectionResult.getErrorMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = String.valueOf(mLastLocation.getLatitude());
                longitude = String.valueOf(mLastLocation.getLongitude());

                lattitudeText.setText("Latitude: " + latitude);
                lontitudeText.setText("Longitude: " + longitude);

                LatLng nyc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Marker newYork = mMap.addMarker(new MarkerOptions().position(nyc)
                            .title("NYC"));
                    mMap.setMyLocationEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nyc, 7));
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Main", "Connection failed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
