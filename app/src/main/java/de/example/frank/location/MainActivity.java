package de.example.frank.location;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION = 2;

    private TextView textview;
    private String provider;
    private Button button;
    private Button weiter;

    // These are required for GPS services.
    private static LocationManager manager;
    private static LocationListener listener;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.textview);
        button = (Button) findViewById(R.id.button);
        weiter = (Button) findViewById(R.id.weiter);

        // LocationManager-Instanz ermitteln
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // LocationListener-Objekt erzeugen
        listener = new LocationListener() {
            // When turned on or off.
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.d(TAG, "onStatusChanged()");
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged()");
                if (location != null) {
                    String s = "Breite: " + location.getLatitude()
                            + "\nLänge: " + location.getLongitude() + "\n\n";
                    textview.append(s);
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled()");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled()");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        initRights();


    }

    private void initRights() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate request permissions");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);// 10 => requestCode (any number)
                return;
            } else {
                Log.d(TAG, "onCreate - permissions already granted.");
                configureButton();
            }
            Log.d(TAG, "onCreate SDK fits.");
        } else {
            // Rights come from the manifest file and are approved during installation.
            configureButton();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                Log.d(TAG, "onRequestPermissionsResult = 10");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "grantResults contains PERMISSION_GRANTED");
                    configureButton();
                }
                return;
        }

    }

    private void configureButton() {
        Log.d(TAG, "configureButton called");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // provider, minTime (refresh in msec), minDistance (>0 min m meters), locationListener
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.requestLocationUpdates("network", 5000, 0, listener);
                Log.d(TAG, "setOnClickListener manager.requestLocationUpdates");

            }

        });
        weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                intent.putExtra("Lat", manager.getLastKnownLocation("network").getLatitude());
                intent.putExtra("Lon" , manager.getLastKnownLocation("network").getLongitude());
                startActivity(intent);
            }
        });
    }

}