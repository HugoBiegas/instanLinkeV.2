package com.example.instantlike;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instantlike.Adapter.ComAdapter;
import com.example.instantlike.Connection.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoPoste extends AppCompatActivity {
    private Button btnR, btnPoster;
    private ImageView imagePoste;
    private String photoPath, nomImage;
    private ArrayList<String> gererCome = new ArrayList<>();
    private EditText commmenter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Boolean addCom = true, chercheCom = false;
    private FirebaseAuth mAuth;

    public void onStart() {
        super.onStart();
        // Check si l'user est connecté
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_poste);
        initActivity();
    }

    /**
     * méthode d'inisialisations des variable de la view
     * et de la mise en place des appelle de méthode
     */
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

    /**
     * méthode pour créer l'écouteur des commentaire
     */
    private void ecouteurCom() {
        //mise en place de l'écouteur
        DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage);
        recupeCom(myRef);
    }

    /**
     * méthode pour mettre a jour le recycleurView avec tout les commentaire existant dans la bd
     */
    private void lesCom() {
        double p = Math.random();
        //créations de l'appelle pour récupérer les commentaires
        DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage + "/actu");
        myRef.setValue(" " + p);
    }

    /**
     * méthode pour le btn qui ajoute le commentaitre dans la Bd en fesent a apellle a celle ci.
     * elle vas passer par l'évent listener dédier (méthode recupCom)
     */
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

    /**
     * méthode pour récupérer des donners de la BD temps réel et les mettre a jour
     */
    private void recupeCom(DatabaseReference myRef) {
        myRef.addValueEventListener(BdTempsRel());
    }

    /**
     * méthode qui créer l'évent listeneur
     *
     * @return
     */
    private ValueEventListener BdTempsRel() {
        return new ValueEventListener() {
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
        };
    }

    /**
     * lister pour l'actions de revenir a la page d'acceuil.
     * Et enlevage des écouteurs
     */
    private void cliqueRetour() {
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference("commentaireImage/" + nomImage);
                myRef.removeEventListener(BdTempsRel());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    /**
     * récupérations des extrat envoiller
     * ces a dire l'image en uri
     */
    private void extrat() {
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            photoPath = extra.getString("image");
            nomImage = Uri.parse(photoPath).getPath();
            nomImage = nomImage.substring(nomImage.indexOf("images/") + 7);
            Glide.with(this /* context */).load(photoPath).into(imagePoste);
        }
    }
}