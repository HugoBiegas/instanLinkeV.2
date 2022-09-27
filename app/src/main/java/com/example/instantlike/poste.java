package com.example.instantlike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class poste extends AppCompatActivity {

    Button retour, poster;
    Bitmap image;
    ImageView imagePoste;
    Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poste);
        iniActyvity();
    }
    private void extraDonnée(){
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if(extra != null) {
            String photoPath = extra.getString("image");
            image = BitmapFactory.decodeFile(photoPath);
            photoUri = extra.getParcelable("uri");
            Toast.makeText(this, photoUri.toString(), Toast.LENGTH_SHORT).show();
            imagePoste.setImageBitmap(image);
        }
    }


    private void iniActyvity(){
        retour = findViewById(R.id.retour);
        poster = findViewById(R.id.poster);
        imagePoste = findViewById(R.id.imagePoste);
        extraDonnée();
        retourHome();
        enregistrerImage();
    }
    private void retourHome(){
        retour.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
    private void enregistrerImage(){
        posterImage(photoUri);
    }

    private void posterImage(Uri imageUri){
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + uuid);
        mImageRef.putFile(imageUri);
    }

}