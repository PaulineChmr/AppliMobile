package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewRecipient extends AppCompatActivity {

    private EditText prenom;
    private EditText nom;
    private EditText numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipient);

        prenom = findViewById(R.id.prenom);
        nom = findViewById(R.id.nom);
        numero = findViewById(R.id.numero);

        final Button sendButton = (Button) findViewById(R.id.save);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Recipient recipient = new Recipient(prenom.toString(), nom.toString(), numero.toString());
                Intent intent = new Intent(NewRecipient.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


}