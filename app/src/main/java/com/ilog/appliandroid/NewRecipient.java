package com.ilog.appliandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewRecipient extends AppCompatActivity{

    private EditText prenom, nom, numero;
    private Recipient selectedRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipient);
        initWidgets();
        checkForEditRecipient();
    }

    private void initWidgets(){
        prenom = findViewById(R.id.prenom);
        nom = findViewById(R.id.nom);
        numero = findViewById(R.id.numero);
    }

    private void checkForEditRecipient() {
        Intent previousIntent = getIntent();
        int passedRecipientID = previousIntent.getIntExtra(Recipient.RECIPIENT_EDIT_EXTRA, -1);
        selectedRecipient = Recipient.getRecipientForID(passedRecipientID);

        if (selectedRecipient != null){
            prenom.setText(selectedRecipient.getPrenom());
            nom.setText(selectedRecipient.getNom());
            numero.setText(selectedRecipient.getNumero());
        }
    }

    public void saveRecipient(View view){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String pre = String.valueOf(prenom.getText());
        String no = String.valueOf(nom.getText());
        String num = String.valueOf(numero.getText());

        if(selectedRecipient == null) {
            int id = Recipient.recipientArrayList.size();
            Recipient newRecipient = new Recipient(id, pre, no, num);
            Recipient.recipientArrayList.add(newRecipient);
            sqLiteManager.addRecipientToDatabase(newRecipient);
        }
        else{
            selectedRecipient.setPrenom(pre);
            selectedRecipient.setNom(no);
            selectedRecipient.setNumero(num);
            sqLiteManager.updateRecipientInDB(selectedRecipient);
        }

        finish();
    }
}
/*public class NewRecipient extends AppCompatActivity {

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


}*/