package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class RecipientsList extends AppCompatActivity {

    private ArrayList<String> mails;
    private ArrayAdapter<String> mailsAdapter;
    private ListView lvMails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients_list);
        // ADD HERE
        lvMails = (ListView) findViewById(R.id.lvItems);
        mails = new ArrayList<String>();
        mailsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mails);
        lvMails.setAdapter(mailsAdapter);
        mails.add("First Item");
        mails.add("Second Item");

        final Button buttonSave = (Button) findViewById(R.id.btnAddItem);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientsList.this, NewRecipient.class);
                startActivity(intent);
            }
        });
    }
}