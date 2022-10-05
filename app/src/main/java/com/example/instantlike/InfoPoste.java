package com.example.instantlike;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.instantlike.Adapter.ComAdapter;
import com.example.instantlike.Connection.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoPoste extends AppCompatActivity {
    private Button btnR, btnPoster;
    private ImageView imagePoste;
    private String photoPath, nomImage;
    private ArrayList<String> gererCome = new ArrayList<>();
    private EditText commmenter;
    private FirebaseAuth mAuth;
    private String uuid;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

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
        cliqueRetour();
        cliquePosterCom();
        ComeAffichage();
    }


    /**
     * méthode pour le btn qui ajoute le commentaitre dans la Bd en fesent a apellle a celle ci.
     * elle vas passer par l'évent listener dédier (méthode recupCom)
     */
    private void cliquePosterCom() {
        btnPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si il y a un commentaire
                if (commmenter.getText().toString().length() != 0) {
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    String userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("commentaire").document(uuid + ":" + userID);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //si il existe on prend les commentaire existant
                                    Map<String, Object> donnée = new HashMap<>();
                                    String data = document.getData().toString();
                                    String concaténations;
                                    int i = 0;
                                    //boucle pour reprendre les commentaire créer par cette personne
                                    while (data.length() != 0) {
                                        //on regarde si il a écrie un commentaire
                                        if (data.indexOf("Com" + i) == -1)
                                            break;
                                        concaténations = data;
                                        if (i < 10)
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 5);
                                        else if (i < 100)
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 6);
                                        else
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 7);

                                        if (concaténations.indexOf(",") == -1)
                                            concaténations = concaténations.substring(0, concaténations.indexOf("}"));
                                        else
                                            concaténations = concaténations.substring(0, concaténations.indexOf(","));
                                        donnée.put("Com" + i, concaténations);
                                        i++;
                                    }
                                    donnée.put("Com" + i, commmenter.getText().toString());
                                    donnée.put("UserCom", userID);
                                    //créations des donnée
                                    documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "onSuccess: Les données son créer");
                                        }
                                    });
                                    commmenter.setText("");
                                } else {
                                    //si le document existe pas
                                    Map<String, Object> donnée = new HashMap<>();
                                    donnée.put("Com0", commmenter.getText().toString());
                                    donnée.put("UserCom", userID);
                                    documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "onSuccess: Les données son créer");
                                        }
                                    });
                                    Log.d(TAG, "Document does not exist!");
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });
                    //faire une pose pour l'envoi des données
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gererCome.clear();
                    //actualiser les coms
                    ComeAffichage();
                } else {
                    Toast.makeText(InfoPoste.this, "écriver un commentaire pour le poster", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ComeAffichage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("commentaire")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //on regarde que se sont bien les commentaire pour cette image
                                if (document.getId().contains(uuid)) {
                                    //récupérations des com
                                    String com = document.getData().toString();
                                    String concaténations;
                                    int i = 0;
                                    while (com.length() != 0) {
                                        //on regarde si il a écrie un commentaire
                                        if (com.indexOf("Com" + i) == -1)
                                            break;
                                        concaténations = com;
                                        if (i < 10)
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 5);
                                        else if (i < 100)
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 6);
                                        else
                                            concaténations = concaténations.substring(concaténations.indexOf("Com" + i + "=") + 7);

                                        if (concaténations.indexOf(",") == -1)
                                            concaténations = concaténations.substring(0, concaténations.indexOf("}"));
                                        else
                                            concaténations = concaténations.substring(0, concaténations.indexOf(","));
                                        i++;
                                        gererCome.add(concaténations);
                                    }
                                }
                                final RecyclerView recyclerView = findViewById(R.id.commentaire);
                                recyclerView.setLayoutManager(new LinearLayoutManager(InfoPoste.this));
                                ComAdapter adapter = new ComAdapter(gererCome, InfoPoste.this);
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(InfoPoste.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * lister pour l'actions de revenir a la page d'acceuil.
     * Et enlevage des écouteurs
     */
    private void cliqueRetour() {
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            uuid = extra.getString("name");
        }
    }
}