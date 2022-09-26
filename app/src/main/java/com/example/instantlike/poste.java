package com.example.instantlike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class poste extends AppCompatActivity {

    Button retour;
    Bitmap image;
    ImageView imagePoste;
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
            imagePoste.setImageBitmap(image);
        }
    }


    private void iniActyvity(){
        retour = findViewById(R.id.retour);
        imagePoste = findViewById(R.id.imagePoste);
        extraDonnée();
        retour.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}