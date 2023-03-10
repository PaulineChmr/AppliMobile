package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateUser extends AppCompatActivity {
    private EditText prenom;
    private EditText nom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        prenom = findViewById(R.id.prenom);
        nom = findViewById(R.id.nom);

        final Button buttonSave = (Button) findViewById(R.id.button);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userFName", prenom.getText().toString());
                editor.putString("userLName", nom.getText().toString());
                editor.apply();
                User user = new User(prenom.toString(), nom.toString());
                Intent intent = new Intent(CreateUser.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}