package com.example.instantlike.Poste;

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
import com.example.instantlike.HomePage;
import com.example.instantlike.Profil.ProfilInfo;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InfoPoste extends AppCompatActivity {
    private Button btnR, btnPoster;
    private ImageView imagePoste;
    private String photoPath, nomImage;
    private ArrayList<String> gererCome = new ArrayList<>();
    private ArrayList<String> idUtilisateurCom = new ArrayList<>();
    private EditText commmenter;
    private FirebaseAuth mAuth;
    private String uuid;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private Boolean retourInfo;
    private ArrayList<String> iconUtilisateurCom = new ArrayList<>();
    private ArrayList<String> iconUtilisateurToken = new ArrayList<>();
    private ArrayList<String> nomUtilisateurCom = new ArrayList<>();

    /**
     * verifications que l'utilisateur est bien connecter si non redirections
     */
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
                    //image : personne qui commante
                    DocumentReference documentReference = fStore.collection("images").document(nomImage);
                    String userID = fAuth.getCurrentUser().getUid();

                    documentReference.update("Idcommentaire", FieldValue.arrayUnion(userID), "commentaire", FieldValue.arrayUnion(commmenter.getText().toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            commmenter.setText("");
                            idUtilisateurCom.clear();
                            gererCome.clear();
                            iconUtilisateurCom.clear();
                            iconUtilisateurToken.clear();
                            nomUtilisateurCom.clear();
                            ComeAffichage();
                            Log.d("Update", "items array successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Update", "Error updating items array", e);
                        }
                    });
                } else {
                    Toast.makeText(InfoPoste.this, "écriver un commentaire pour le poster", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * affichage de tout les commentaire
     */
    private void ComeAffichage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itemRef = db.collection("images").document(nomImage);
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        gererCome.addAll((ArrayList<String>) document.get("commentaire"));
                        idUtilisateurCom.addAll((ArrayList<String>) document.get("Idcommentaire"));
                        ininom();
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
                }
            }

        });

    }

    /**
     * récupérations des Icon de utilisateur
     */
    private void iniIcon() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (int i = 0; i < idUtilisateurCom.size(); i++) {
                    for (StorageReference fileRef : listResult.getItems()) {
                        String c = fileRef.getName();
                        if (c.contains(idUtilisateurCom.get(i))) {
                            //actualisations pour avoir un chiffre différent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    iconUtilisateurCom.add(uri.toString());
                                    String name = uri.getLastPathSegment();
                                    name = name.substring(name.indexOf("/") + 1);
                                    iconUtilisateurToken.add(name);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful() && iconUtilisateurCom.size() == idUtilisateurCom.size()) {
                                        triIcon();
                                        final RecyclerView recyclerView = findViewById(R.id.commentaire);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(InfoPoste.this));
                                        ComAdapter adapter = new ComAdapter(gererCome, InfoPoste.this, iconUtilisateurCom, nomUtilisateurCom);
                                        recyclerView.setAdapter(adapter);
                                    }

                                }
                            });
                            break;
                        }
                    }
                }
            }
        });
    }

    private void triIcon() {
        String NomIcon;
        ArrayList<String> temps = new ArrayList<>();
        for (int i = 0; i < idUtilisateurCom.size(); i++) {
            NomIcon = idUtilisateurCom.get(i);
            for (int j = 0; j < iconUtilisateurCom.size(); j++) {
                if (NomIcon.equals(iconUtilisateurToken.get(j))) {
                    temps.add(iconUtilisateurCom.get(j));
                    break;
                }
            }
        }
        iconUtilisateurCom.clear();
        iconUtilisateurCom.addAll(temps);
    }

    /**
     * récupérations du nom d'utilisateur
     */
    private void ininom() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < idUtilisateurCom.size(); i++) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(idUtilisateurCom.get(i))) {
                                //date du poste
                                String userName = document.getData().toString();
                                userName = userName.substring(userName.indexOf("username=") + 9);
                                if (userName.indexOf(",") == -1)
                                    userName = userName.substring(0, userName.indexOf("}"));
                                else userName = userName.substring(0, userName.indexOf(","));
                                nomUtilisateurCom.add(userName);
                                break;
                            }
                        }
                    }
                    iniIcon();
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
                if (retourInfo)
                    startActivity(new Intent(getApplicationContext(), ProfilInfo.class));
                else startActivity(new Intent(getApplicationContext(), HomePage.class));
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
            retourInfo = extra.getBoolean("retour");
        }
    }
}