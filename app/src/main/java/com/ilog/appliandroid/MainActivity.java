package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mGreetingTextView;
    private EditText mNameEditText;
    private Button mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vérifie qu'on a bien toutes les permissions requises
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        final Button userButton = (Button) findViewById(R.id.main_button_user);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateUser.class);
                startActivity(intent);
            }
        });

        final Button mailsButton = (Button) findViewById(R.id.main_button_mails);
        mailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipientsList.class);
                startActivity(intent);
            }
        });

        //partie sur le bouton d'envoie
        final Button sendButton = (Button) findViewById(R.id.main_button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //en gros ca marche si on ouvre d'abord la view de la liste des numéros qui permet de se synchroniser avec la database.
                //normalement ca va se régler ensuite
                ArrayList<Recipient> recipients = Recipient.recipientArrayList;
                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < recipients.size(); i ++){
                    //ça prend en compte que ceux que je viens de créer là là
                    String message = "Ceci est un test";
                    String number = recipients.get(i).getNumero();
                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(number, null, message, null, null);
                }
            }
        });
    }
}