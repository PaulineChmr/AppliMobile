package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class RecipientsList extends AppCompatActivity{

    private ListView recipientListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients_list);
        initWidgets();
        loadFromDBToMemory();
        setRecipientAdapter();
        setOnClickListener();
    }

    private void initWidgets(){
        recipientListView = findViewById(R.id.recipientListView);
    }

    private void loadFromDBToMemory() {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.populateRecipientListArray();
    }


    private void setRecipientAdapter(){
        RecipientAdapter recipientAdapter = new RecipientAdapter(getApplicationContext(), Recipient.recipientArrayList);
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
}

/*public class RecipientsList extends AppCompatActivity {

    private ArrayList<String> mails;
    private ArrayAdapter<String> mailsAdapter;
    private ListView recipientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients_list);
        // ADD HERE
        recipientListView = (ListView) findViewById(R.id.lvItems);
        mails = new ArrayList<String>();
        mailsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mails);
        recipientListView.setAdapter(mailsAdapter);
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
}*/