package com.ilog.appliandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.hardware.SensorEvent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private ListView recipientListView;
    public String mLocation;

    //partieGaël
    // Attributs concernant la détection de chute
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private double maxAcceleration = 0;
    private boolean isFallen = false;
    private Button resetButton;

    // Attributs concernant la position GPS
    FusedLocationProviderClient mFusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    String userFName;

    String userLName;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerationCurrentValue = Math.sqrt(x*x + y*y + z*z);
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            // maxAcceleration n'est pas nécessaire pour le code final, il sert simplement lors des tests
            if (accelerationCurrentValue > maxAcceleration) {
                maxAcceleration = accelerationCurrentValue;
            }

            // Détection de la chute
            if(accelerationCurrentValue > 27 && isFallen == false) {
                isFallen = true;
                resetButton.setVisibility(View.VISIBLE);
                getLastLocation();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        loadFromDBToMemory();
        setRecipientAdapter();
        setOnClickListener();

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        userFName = sharedPreferences.getString("userFName", "");
        userLName = sharedPreferences.getString("userLName", "");
        //Vérifie qu'on a bien toutes les permissions requises
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        //partieGaël
        // Instanciation de l'accéléromètre
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Instanciation du GPS
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
    //partie sur la liste des contacts

    private void initWidgets(){
        recipientListView = findViewById(R.id.recipientListView);
        resetButton = findViewById(R.id.reset);
        resetButton.setVisibility(View.INVISIBLE);
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.populateRecipientListArray();
    }


    private void setRecipientAdapter(){
        RecipientAdapter recipientAdapter = new RecipientAdapter(getApplicationContext(), Recipient.nonDeleteRecipients());
        recipientListView.setAdapter(recipientAdapter);
    }

    private void setOnClickListener() {
        recipientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Recipient selectedRecipient = (Recipient) recipientListView.getItemAtPosition(position);
                Intent editRecipientIntent = new Intent(getApplicationContext(), NewRecipient.class);
                editRecipientIntent.putExtra(Recipient.RECIPIENT_EDIT_EXTRA, selectedRecipient.getId());
                startActivity(editRecipientIntent);
            }
        });
    }

    public void newRecipient(View view) {
        Intent newRecipientIntent = new Intent(this, NewRecipient.class);
        startActivity(newRecipientIntent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setRecipientAdapter();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //PartieGaël

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private void getLastLocation() {
        String s = "";
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
                                    mLocation = "Latitude: " + mAdresses.get(0).getLatitude() + " " + mAdresses.get(0).getLongitude()
                                            + "\n  Adress: " + mAdresses.get(0).getAddressLine(0) + " " + mAdresses.get(0).getLocality();
                                    ArrayList<Recipient> recipients = Recipient.recipientArrayList;
                                    String s = mLocation;
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
                                    for (int i = 0; i < recipients.size(); i ++){
                                        if(recipients.get(i).getDeleted() == null) {
                                            //String message = "ALERTE ! " + userFName + " " + userLName + " est tombé(e) à cet endroit : " + s;
                                            String fullName = userFName.concat(" ".concat(userLName));

                                            String message = "ALERTE ! Je suis tombé(e) \n".concat(fullName.concat("\n".concat(s)));
                                            String number = recipients.get(i).getNumero();
                                            SmsManager mySmsManager = SmsManager.getDefault();
                                            mySmsManager.sendTextMessage(number, null, message, null, null);
                                        }
                                    }
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

    public void resetFall(View view) {
        isFallen = false;
        resetButton.setVisibility(View.INVISIBLE);
    }

    public void createUser(View view) {
        Intent newRecipientIntent = new Intent(this, CreateUser.class);
        startActivity(newRecipientIntent);
    }
}