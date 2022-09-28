package com.example.instantlike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class InfoPoste extends AppCompatActivity {
    Button btnR;
    ImageView imagePoste;
    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_poste);
        initActivity();
    }

    private void initActivity(){
        btnR = findViewById(R.id.retourPoste);
        imagePoste = findViewById(R.id.imagePoste);
        cliqueRetour();
        extrat();
    }

    private void cliqueRetour(){
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void extrat(){
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            String photoPath = extra.getString("image");
            imagePoste.setImageURI(Uri.parse(photoPath));
        }

    }
}