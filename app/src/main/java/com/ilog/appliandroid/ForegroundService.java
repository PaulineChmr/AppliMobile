package com.ilog.appliandroid;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ForegroundService extends Service {
    private Context context;
    private final int NOTIFICATION_ID = 1;
    private final String CHANNEL_ID = "100";

    // Attributs concernant la détection de chute
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private boolean isFallen = false;

    // Attributs concernant la position GPS
    FusedLocationProviderClient mFusedLocationProviderClient;

    // Attributs concernant le message à envoyer
    String mLocation;
    String userFName;
    String userLName;
    StringBuffer message = new StringBuffer("ALERTE !\n");


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerationCurrentValue = Math.sqrt(x*x + y*y + z*z);

            // Détection de la chute
            if(accelerationCurrentValue > 20 && !isFallen) {
                getLastLocation();
                isFallen = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, showNotification());
        }

        // Instanciation du GPS
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private Notification showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "Foreground notification",
                            NotificationManager.IMPORTANCE_HIGH));
        }
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Détection de chute")
                .setContentText("En cours d'exécution")
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context, "Veuillez patienter...", Toast.LENGTH_SHORT).show();
        doTask();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doTask() {
        final int[] data = new int[1];
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Instanciation de l'accéléromètre
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "Fin de la surveillance", Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void getLastLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        userFName = sharedPreferences.getString("userFName", "");
        userLName = sharedPreferences.getString("userLName", "");
        message.append(userFName);
        message.append(" ");
        message.append(userLName);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
                                List<Address> mAdresses = null;
                                try {
                                    message.append(" est tombé(e) à cet endroit :");
                                    mAdresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    mLocation = "Latitude: " + mAdresses.get(0).getLatitude() + " \nLongitude: " + mAdresses.get(0).getLongitude()
                                            + "\nAdresse: " + mAdresses.get(0).getAddressLine(0);
                                    ArrayList<Recipient> recipients = Recipient.recipientArrayList;
                                    for (int i = 0; i < recipients.size(); i ++){
                                        if(recipients.get(i).getDeleted() == null) {
                                            String completeMessage = message.toString();
                                            String number = recipients.get(i).getNumero();
                                            SmsManager mySmsManager = SmsManager.getDefault();
                                            mySmsManager.sendTextMessage(number, null, completeMessage, null, null);
                                            mySmsManager.sendTextMessage(number, null, mLocation, null, null);
                                        }
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            // Si une chute est détectée, on peut clore l'application car les messages ont été envoyés
                            stopSelf();
                        }
                    });
        }
    }
}