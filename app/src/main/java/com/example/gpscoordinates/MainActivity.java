package com.example.gpscoordinates;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private TextView output;
    private Button button;
    private LocationManager locationManager;
    private String textCoordinate;
    private final static int FINE_LOCATION_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lock the screen to be in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        checkPermission();
        setup();
    }

    // when page is not shown, stop the GPS/location updates
    @Override
    protected void onPause() {
        super.onPause();
        pauseUpdates();
    }

    private void setup() {
        relativeLayout = findViewById(R.id.mainLayout);
        output = findViewById(R.id.output);
        button = findViewById(R.id.button);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    gpsUtils();
                    output.setText(textCoordinate);
                }
                else
                    buildAlertDialog();
            }
        });
    }

    public void gpsUtils() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);
            getGpsCoordinates();
        }
    }

    public void getGpsCoordinates() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        // get user's GPS location
        @SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if(location == null) {
            final String text = "Cannot get location. Please try again in a few seconds.";
            buildSnackbar(text);
        }
        else {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            textCoordinate = "Latitude: " + String.format("%.6f", latitude) + "\nLongitude: " + String.format("%.6f", longitude);
        }
    }

    // stop location update
    public void pauseUpdates() {
        locationManager.removeUpdates(locationListenerGPS);
    }

    // ask for permission if hasnt been granted
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
        }
    }

    // called after request result has been returned
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //new GpsUtils(this).requestLocationUpdates();
                }
                else {
                    final String text = "Please allow App to access this device's location to properly use the App";
                    buildSnackbar(text);
                }
                return;
        }
    }

    public void buildAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Enable Location");
        alertDialog.setMessage("Your GPS is not enabled. Please enabled it in settings menu.");
        alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void buildSnackbar(String text) {
        Snackbar.make(relativeLayout, text, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // final double userLatitude = location.getLatitude();
            // final double userLongitude = location.getLongitude();
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
}
