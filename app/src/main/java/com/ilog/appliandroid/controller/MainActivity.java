package com.ilog.appliandroid.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ilog.appliandroid.R;

import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
    // Attributs concernant l'affichage du xml
    TextView mUserName, mXAxis, mYAxis, mZAxis, mCurrentAcc, mPrevAcc, mAcceleration, mMaxAcc, mChute, mLocation;
    Button mGetLocation;

    // Attributs concernant la détection de chute
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private double maxAcceleration = 0;
    private boolean isFallen = false;

    // Attributs concernant la position GPS
    FusedLocationProviderClient mFusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            mXAxis.setText("x: " + x);
            mYAxis.setText("y: " + y);
            mZAxis.setText("z: " + z);

            accelerationCurrentValue = Math.sqrt(x*x + y*y + z*z);
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            // maxAcceleration n'est pas nécessaire pour le code final, il sert simplement lors des tests
            if (accelerationCurrentValue > maxAcceleration) {
                maxAcceleration = accelerationCurrentValue;
            }

            // Détection de la chute
            if(accelerationCurrentValue > 27) {
                isFallen = true;
            }

            // MàJ des TextView
            mCurrentAcc.setText("Current = " + accelerationCurrentValue);
            mPrevAcc.setText("Previous = " + accelerationPreviousValue);
            mAcceleration.setText("Acceleration change = " + changeInAcceleration);
            mMaxAcc.setText("Max Acc = " + maxAcceleration);
            mChute.setText("Chute: " + isFallen);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Référenciation des éléments visuels
        mUserName = findViewById(R.id.user_name_textview);
        mXAxis = findViewById(R.id.x_axis_textview);
        mYAxis = findViewById(R.id.y_axis_textview);
        mZAxis = findViewById(R.id.z_axis_textview);
        mCurrentAcc = findViewById(R.id.current_accel_textview);
        mPrevAcc = findViewById(R.id.previous_accel_textview);
        mAcceleration = findViewById(R.id.acceleration_textview);
        mMaxAcc = findViewById(R.id.max_accel_textview);
        mChute = findViewById(R.id.chute_textview);
        mLocation = findViewById(R.id.location_textview);
        mGetLocation = findViewById(R.id.get_location_button);

        // Instanciation de l'accéléromètre
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Instanciation du GPS
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                // SUPPRIMER LA LIGNE SUIVANTE
                isFallen = false;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder mGeocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> mAdresses = null;
                                try {
                                    mAdresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    mLocation.setText("Latitude: " + mAdresses.get(0).getLatitude() + " " + mAdresses.get(0).getLongitude()
                                            + "\n  Adress: " + mAdresses.get(0).getAddressLine(0) + " " + mAdresses.get(0).getLocality());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }
                    });
        }
        else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}