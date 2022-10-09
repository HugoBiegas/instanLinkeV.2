package com.example.instantlike.InteractionUtilisateur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import com.example.instantlike.HomePage;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.Profil.ProfilInfo;
import com.example.instantlike.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilisateurMP extends AppCompatActivity {

    private ImageButton home,message,profilInfoPoste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilisateur_mp);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photoClique();
        PosteClique();
        iniActivity();
    }

    private androidx.appcompat.widget.Toolbar toolbar;
    private static final int RETOUR_PHOTO = 1;
    private  String photoPath;
    private Uri photoUir;

    private void photoClique(){
        ImageButton photo = findViewById(R.id.action_photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adimage();
            }
        });
    }
    /**
     * méthode qui mais en place l'appreille photo
     * avec la créations de a à z de l'image en créent tout les données de l'image
     */
    private void adimage() {
        //on crée l'appareille photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // on regarde si la personne a pris une photo et veux la valider
        if (intent.resolveActivity(getPackageManager()) != null) {
            // on crée tout les données corespondent a l'image
            String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                photoUir = FileProvider.getUriForFile(UtilisateurMP.this, UtilisateurMP.this.getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUir);
                startActivityForResult(intent, RETOUR_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * redéfinitions de la méthode onActivityResult qui permet d'avoir un retour sur la capture faite aux préalable
     * tout en enlevent tout les évent listeneur
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // on regarde si le résultat de la photo et un  sucer si oui on peux créer un poste
        if (requestCode == RETOUR_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), CreationPoste.class);//créations de la page Game
            intent.putExtra("image", photoPath);//on donne en extrat la valeur de la roomName pour savoir si la personne et un gest ou l'host
            intent.putExtra("uri", photoUir);
            startActivity(intent);//on lance l'activiter
            finish();
        }
    }

    private void PosteClique(){
        ImageButton Poste = findViewById(R.id.action_poste);
        Poste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreationPoste.class));
                finish();
            }
        });
    }



    private void iniActivity() {
        home = findViewById(R.id.HomeBTNMpUtilisateur);
        message = findViewById(R.id.MessageBTNMpUtilisateur);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNMpUtilisateur);
        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
    }
    private void cliquemessage(){
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UtilisateurMP.class));
                finish();
            }
        });
    }
    private void cliqueProfilInfoPost(){
        profilInfoPoste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfilInfo.class));
                finish();
            }
        });
    }

    private void cliqueHome(){
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
        });
    }

}