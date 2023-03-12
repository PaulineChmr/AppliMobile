package com.ilog.appliandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RecipientAdapter extends ArrayAdapter<Recipient> {
    public RecipientAdapter(Context context, List<Recipient> recipients){
        super(context, 0, recipients);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Recipient recipient = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipient_cell, parent, false);
        }
        TextView prenom = convertView.findViewById(R.id.cellPrenom);
        TextView nom = convertView.findViewById(R.id.cellNom);
        TextView numero = convertView.findViewById(R.id.cellNumero);

        prenom.setText(recipient.getPrenom());
        nom.setText(recipient.getNom());
        numero.setText(recipient.getNumero());

        return convertView;
    }
}
