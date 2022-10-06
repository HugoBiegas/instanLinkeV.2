package com.example.instantlike.InteractionUtilisateur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.instantlike.HomePage;
import com.example.instantlike.Profil.ProfilInfo;
import com.example.instantlike.R;

public class UtilisateurMP extends AppCompatActivity {

    private ImageButton home,message,profilInfoPoste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilisateur_mp);
        iniActivity();
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