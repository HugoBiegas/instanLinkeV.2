package com.example.instantlike.Profil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantlike.Adapter.ImageAdapter;
import com.example.instantlike.Adapter.PublicationAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.HomePage;
import com.example.instantlike.InteractionUtilisateur.UtilisateurMP;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;

public class ProfilInfo extends AppCompatActivity {

    private ImageButton home,message,profilInfoPoste;
    private TextView nom,publications,follower,suivi;
    private ImageView icon;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private int nbPoste = 0, nbSuivi=0,nbFollow=0;
    private ArrayList<String> imageListUri = new ArrayList<>(), NomImagePoste= new ArrayList<>(), DatePoste= new ArrayList<>(),LikePoste= new ArrayList<>();


    public void onStart() {
        super.onStart();
        // Check si l'user est connecté
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_info);
        iniActivity();
    }

    private void iniActivity() {
        home = findViewById(R.id.HomeBTNInfoProfil);
        message = findViewById(R.id.MessageBTNInfoProfil);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNInfoProfil);
        icon = findViewById(R.id.iconUtilisateurProfil);
        nom = findViewById(R.id.NomUtilisateurProfil);
        publications = findViewById(R.id.PublicationsUtilisateurProfil);
        follower = findViewById(R.id.FollowerUtilisateurProfil);
        suivi = findViewById(R.id.SuiviUtilisateurProfil);
        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
        iconUtilisateur();
        nomUtilisateur();
        publicationNB();
        followSuivi();

    }
    private void PublicationUtilisateur() {
        //bar de progrations de la conections a firebase
        //créations du recycler
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (int i = 0; i < NomImagePoste.size(); i++) {
                    // on fait une boucle pour stocker les images une par une
                    for (StorageReference fileRef : listResult.getItems()) {
                        if (fileRef.getName().equals(NomImagePoste.get(i))){
                            //actualisations pour avoir un chiffre différent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //on récupére uri qui est le lien ou trouver les données
                                    imageListUri.add(uri.toString());
                                    Log.d("item", uri.toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final RecyclerView recyclerView = findViewById(R.id.recyclerViewUtilisateurInfo);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ProfilInfo.this));
                                    PublicationAdapter adapter = new PublicationAdapter(imageListUri, ProfilInfo.this, DatePoste, LikePoste, NomImagePoste);
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });
    }
    private void followSuivi(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("followSuivi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String followSuivi = document.getId();
                                String Suivi = followSuivi.substring(followSuivi.indexOf(":")+1);
                                String follow = followSuivi.substring(0,followSuivi.indexOf(":"));
                                if (Suivi.equals(currentUser.getUid())){
                                    nbSuivi++;
                                }else if (follow.equals(currentUser.getUid())){
                                    nbFollow++;
                                }
                            }
                            follower.setText("Follower : "+nbFollow);
                            suivi.setText("Suivi : "+nbSuivi);
                        } else {
                            Toast.makeText(ProfilInfo.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void publicationNB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userposte = document.getData().toString();
                                userposte = userposte.substring(userposte.indexOf("UserPoste=") + 10);
                                if (userposte.indexOf(",") == -1)
                                    userposte = userposte.substring(0, userposte.indexOf("}"));
                                else
                                    userposte = userposte.substring(0, userposte.indexOf(","));
                                //récupérations du nom de l'image
                                if(userposte.equals(currentUser.getUid())){
                                    nbPoste++;
                                    NomImagePoste.add(document.getId());
                                    //nb Like
                                    String like = document.getData().toString();

                                    like = like.substring(like.indexOf("Like=") + 5);
                                    if (like.indexOf(",") == -1)
                                        like = like.substring(0, like.indexOf("}"));
                                    else
                                        like = like.substring(0, like.indexOf(","));
                                    LikePoste.add(like);

                                    //date du poste
                                    String dateposte = document.getData().toString();
                                    dateposte = dateposte.substring(dateposte.indexOf("DatePoste=") + 10);
                                    if (dateposte.indexOf(",") == -1)
                                        dateposte = dateposte.substring(0, dateposte.indexOf("}"));
                                    else
                                        dateposte = dateposte.substring(0, dateposte.indexOf(","));
                                    DatePoste.add(dateposte);
                                }
                            }
                            publications.setText("Publications : "+nbPoste);
                            PublicationUtilisateur();
                        } else {
                            Toast.makeText(ProfilInfo.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void nomUtilisateur(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //récupérations du nom de l'image
                                if(document.getId().equals(currentUser.getUid())){
                                    String nomUser = document.getData().toString();
                                    nomUser = nomUser.substring(nomUser.indexOf("username=") + 9);
                                    if (nomUser.indexOf(",") == -1)
                                        nomUser = nomUser.substring(0, nomUser.indexOf("}"));
                                    else
                                        nomUser = nomUser.substring(0, nomUser.indexOf(","));
                                    nom.setText(nomUser);
                                }
                            }
                        } else {
                            Toast.makeText(ProfilInfo.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void iconUtilisateur() {
        //créations du recycler
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                    for (StorageReference fileRef : listResult.getItems()) {
                        if (fileRef.getName().contains(currentUser.getUid().toString())){
                            //actualisations pour avoir un chiffre différent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri.toString()).into(icon);
                                }
                            });
                        }

                    }

                // on fait une boucle pour stocker les images une par une
            }
        });

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