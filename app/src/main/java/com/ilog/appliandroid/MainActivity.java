package com.ilog.appliandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_SMS_AND_LOCATION = 123;
    private ListView recipientListView;
    private Button btnStartForegroundService, btnStopForegroundService;
    private Intent foregroundServiceIntent;
    String userFName;
    String userLName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foregroundServiceIntent = new Intent(this, ForegroundService.class);

        initWidgets();
        loadFromDBToMemory();
        setRecipientAdapter();
        setOnClickListener();
        requestSmsAndLocationPermission();

        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        userFName = sharedPreferences.getString("userFName", "");
        userLName = sharedPreferences.getString("userLName", "");

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
    }


    //partie sur la liste des contacts

    private void initWidgets(){
        recipientListView = findViewById(R.id.recipientListView);
        ///Service
        btnStartForegroundService = findViewById(R.id.btnStartForegroundService);
        btnStopForegroundService = findViewById(R.id.btnStopForegroundService);
        btnStartForegroundService.setOnClickListener(this);
        btnStopForegroundService.setOnClickListener(this);
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
    }


    private void requestSmsAndLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Demande la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_SMS_AND_LOCATION);
        }
    }

    // Gère la réponse à la demande de permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS_AND_LOCATION: {
                // Si la demande de permission est annulée, le tableau de résultats est vide.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission accordée
                } else {
                    // Permission refusée
                }
                return;
            }
        }
    }

    public void createUser(View view) {
        Intent newRecipientIntent = new Intent(this, CreateUser.class);
        startActivity(newRecipientIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartForegroundService:
                startService(foregroundServiceIntent);
                break;

            case R.id.btnStopForegroundService:
                stopService(foregroundServiceIntent);
                break;
        }
    }
}
