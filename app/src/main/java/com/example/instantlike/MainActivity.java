package com.example.instantlike;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//https://www.youtube.com/watch?v=8890GpBwn9w
 //https://www.youtube.com/watch?v=KY5vOVNqkGM
    private static final int RETOUR_PHOTO = 1;
    ImageView imagePoster;
    Button adImage, poste;
    String photoPath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniActivity();

    }
    // initialistations du post
    private void iniActivity(){
        adImage = findViewById(R.id.adImageBtn);
        poste = findViewById(R.id.poste);
        imagePoster = findViewById(R.id.imageEnvoiller);
        recupérationImage();
        adPoste();
    }
    private void adPoste(){
        poste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), poste.class));
                finish();
            }
        });
    }

    private  void recupérationImage(){
        adImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                adimage();
            }
        });
    }
    private void adimage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                Uri photoUir = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUir);
                startActivityForResult(intent, RETOUR_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == RETOUR_PHOTO && resultCode == RESULT_OK){
            Intent intent = new Intent(getApplicationContext(), poste.class);//créations de la page Game
            intent.putExtra("image", photoPath);//on donne en extrat la valeur de la roomName pour savoir si la personne et un gest ou l'host
            startActivity(intent);//on lance l'activiter
            finish();
        }
    }
}