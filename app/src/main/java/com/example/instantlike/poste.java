package com.example.instantlike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class poste extends AppCompatActivity {

    Button retour, poster;
    Bitmap image;
    ImageView imagePoste;
    Uri photoUri;
    String uuid;
    EditText titre, descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poste);
        iniActyvity();
    }

    private void extraDonnée() {
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            String photoPath = extra.getString("image");
            image = BitmapFactory.decodeFile(photoPath);
            photoUri = extra.getParcelable("uri");
            imagePoste.setImageBitmap(image);
        }
    }


    private void iniActyvity() {
        retour = findViewById(R.id.retour);
        poster = findViewById(R.id.poster);
        titre = findViewById(R.id.titre);
        descriptions = findViewById(R.id.descriptions);
        imagePoste = findViewById(R.id.imagePoste);
        extraDonnée();
        retourHome();
        enregistrerImage();
    }

    private void retourHome() {
        retour.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void enregistrerImage() {
        poster.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si il y as une image on l'envoie
                if (null != imagePoste.getDrawable() && titre.getText().length() != 0 && descriptions.getText().length() != 0) {
                    posterImage(photoUri);
                    //rajouter dans firebase le titre et le commentaire
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("images/" + uuid + "/descriptions");
                    myRef.setValue(descriptions.getText().toString());
                    myRef = database.getReference("images/" + uuid + "/Titre");
                    myRef.setValue(titre.getText().toString());
                    myRef = database.getReference("images/"+ uuid+"/actu");
                    myRef.setValue(" 1");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else
                    Toast.makeText(poste.this, "sisisez une image ou une vidéo,un titre et une descriptions", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void posterImage(Uri imageUri) {
        uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING tock
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + uuid);
        mImageRef.putFile(imageUri);
    }

}