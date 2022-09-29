package com.example.instantlike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoPoste extends AppCompatActivity {
    Button btnR, btnPoster;
    ImageView imagePoste;
    Bitmap image;
    String photoPath, nomImage;
    ArrayList<String> gererCome = new ArrayList<>();
    EditText commmenter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Boolean addCom = true, chercheCom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_poste);
        initActivity();
    }

    private void initActivity() {
        btnR = findViewById(R.id.retourPoste);
        imagePoste = findViewById(R.id.imagePoste);
        btnPoster = findViewById(R.id.PosterCommentaire);
        commmenter = findViewById(R.id.adcommentaire);
        extrat();
        ecouteurCom();
        cliqueRetour();
        cliquePosterCom();
        lesCom();
    }

    private void ecouteurCom() {
        //mise en place de l'écouteur
        DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage);
        recupeCom(myRef);
    }

    private void lesCom() {
        double p = Math.random();
        //créations de l'appelle pour récupérer les commentaires
        DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage + "/actu");
        myRef.setValue(" " + p);
    }

    private void cliquePosterCom() {
        btnPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commmenter.length() != 0) {
                    addCom = false;
                    double p = Math.random();
                    //déclanchement de lécouteur
                    DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage + "/actu");
                    myRef.setValue(" " + p);
                    //lésser le temps de trétement
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void recupeCom(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //on récupére les valeurs
                String chaine = snapshot.getValue().toString();
                gererCome.clear();
                //récupérer les commentaires
                if (chercheCom == false) {
                    if (chaine.contains("commentaire=")) {
                        chaine = chaine.substring(chaine.indexOf("commentaire=") + 12);
                        //récupérer tout les commentaire
                        while (chaine.contains(";")) {
                            gererCome.add(chaine.substring(0, chaine.indexOf(";")));
                            chaine = chaine.substring(chaine.indexOf(";") + 1);
                        }
                    }
                    final RecyclerView recyclerView = findViewById(R.id.commentaire);
                    final ComAdapter adapter = new ComAdapter(gererCome, getApplicationContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter);
                    chercheCom = true;
                } else

                    //on passe que une fois si la personne a cliquer sur commenter
                    if (addCom == false) {
                        String adCommentaire = "";
                        //on regarde si on as des commentaire si oui on les récupére touse
                        if (chaine.contains("commentaire=")) {
                            chaine = chaine.substring(chaine.indexOf("commentaire=") + 12);
                            //récupérer tout les commentaire
                            while (chaine.contains(";")) {
                                gererCome.add(chaine.substring(0, chaine.indexOf(";")));
                                chaine = chaine.substring(chaine.indexOf(";") + 1);
                            }
                            //on les concaténe
                            for (int i = 0; i < gererCome.size(); i++) {
                                adCommentaire += gererCome.get(i);
                                adCommentaire += ";";
                            }
                        }
                        DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage + "/commentaire");
                        myRef.setValue(adCommentaire + commmenter.getText().toString() + ";");
                        gererCome.clear();
                        addCom = true;
                        //on actualise les commentaires
                        chercheCom = false;
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cliqueRetour() {
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
            photoPath = extra.getString("image");
            nomImage = Uri.parse(photoPath).getPath();
            nomImage = nomImage.substring(nomImage.indexOf("images/") + 7);
            Glide.with(this /* context */).load(photoPath).into(imagePoste);
        }
    }
}