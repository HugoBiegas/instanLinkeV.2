package com.example.instantlike.Profil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.PublicationAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.HomePage;
import com.example.instantlike.InteractionUtilisateur.UtilisateurMP;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfilInfo extends AppCompatActivity {

    private static final int RETOUR_PHOTO = 1;
    private ImageButton home, message, profilInfoPoste;
    private TextView nom, publications;
    private ImageView icon;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private int nbPoste = 0, nbSuivi = 0, nbFollow = 0;
    private ArrayList<String> imageListUri = new ArrayList<>();
    private  ArrayList<String> NomImagePoste = new ArrayList<>();
    private  ArrayList<String> DatePoste = new ArrayList<>();
    private  ArrayList<Integer> LikePoste = new ArrayList<>();
    private androidx.appcompat.widget.Toolbar toolbar;
    private String photoPath;
    private Uri photoUir;
    private Button deconnections;


    /**
     * verificaitons si l'utilisateur est connecter
     */
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
        toolbar = findViewById(R.id.toolbar);
        photoClique();
        PosteClique();
        iniActivity();
    }

    /**
     * clique pour prendre une photo
     */
    private void photoClique() {
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
                photoUir = FileProvider.getUriForFile(ProfilInfo.this, ProfilInfo.this.getApplicationContext().getPackageName() + ".provider", photoFile);
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

    /**
     * clique pour faire un poste
     */
    private void PosteClique() {
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
        home = findViewById(R.id.HomeBTNInfoProfil);
        message = findViewById(R.id.MessageBTNInfoProfil);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNInfoProfil);
        icon = findViewById(R.id.iconUtilisateurProfil);
        nom = findViewById(R.id.NomUtilisateurProfil);
        publications = findViewById(R.id.PublicationsUtilisateurProfil);
        deconnections = findViewById(R.id.buttonDéconnecter);
        deconnectionsUser();
        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
        publicationNB();
        iconUtilisateur();
        nomUtilisateur();

    }

    /**
     * bouton pour déconnecter l'utilisateur
     */
    private void deconnectionsUser() {
        deconnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    /**
     * affichage des publications de l'utilisateur
     */
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
                        if (fileRef.getName().equals(NomImagePoste.get(i))) {
                            //actualisations pour avoir un chiffre différent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //on récupére uri qui est le lien ou trouver les données
                                    imageListUri.add(uri.toString());
                                    Log.d("item", uri.toString());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    miseEnForme();
                                }
                            });
                            break;
                        }
                    }
                }
            }
        });
    }
    private void miseEnForme(){
        if (imageListUri.size() == NomImagePoste.size()){
            final RecyclerView recyclerView = findViewById(R.id.recyclerViewUtilisateurInfo);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProfilInfo.this));
            PublicationAdapter adapter = new PublicationAdapter(imageListUri, ProfilInfo.this, DatePoste, LikePoste, NomImagePoste);
            recyclerView.setAdapter(adapter);
        }

    }


    /**
     * récupére le nombre de publications de la personne + tout les imfo utilise des publications
     */
    private void publicationNB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userposte = document.getData().toString();
                        userposte = userposte.substring(userposte.indexOf("UserPoste=") + 10);
                        if (userposte.indexOf(",") == -1)
                            userposte = userposte.substring(0, userposte.indexOf("}"));
                        else userposte = userposte.substring(0, userposte.indexOf(","));
                        //récupérations du nom de l'image
                        if (userposte.equals(currentUser.getUid())) {
                            nbPoste++;
                            NomImagePoste.add(document.getId());
                            //nblike
                            String likeposte = document.getData().toString();
                            likeposte = likeposte.substring(likeposte.indexOf("Like=[") + 6);
                            likeposte = likeposte.substring(0,likeposte.indexOf("]"));
                            int max = likeposte.length();
                            int cpt = 0;
                            for (int i = 0; i < max; i++) {
                                if (likeposte.length() ==0){
                                    break;
                                }else{
                                    if (likeposte.indexOf(",") == -1 ){
                                        cpt++;
                                        break;
                                    }else{
                                        likeposte = likeposte.substring(0, likeposte.indexOf(","));
                                        cpt++;
                                    }
                                }

                            }
                            LikePoste.add(cpt);

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
                    PublicationUtilisateur();
                    publications.setText("Publications : " + nbPoste);
                } else {
                    Toast.makeText(ProfilInfo.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    /**
     * réucpére le nom de l'utilisateur
     */
    private void nomUtilisateur() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //récupérations du nom de l'image
                        if (document.getId().equals(currentUser.getUid())) {
                            String nomUser = document.getData().toString();
                            nomUser = nomUser.substring(nomUser.indexOf("username=") + 9);
                            if (nomUser.indexOf(",") == -1)
                                nomUser = nomUser.substring(0, nomUser.indexOf("}"));
                            else nomUser = nomUser.substring(0, nomUser.indexOf(","));
                            nom.setText(nomUser);
                        }
                    }
                } else {
                    Toast.makeText(ProfilInfo.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * récupére l'icon de l'utilisateur
     */
    private void iconUtilisateur() {
        //créations du recycler
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileRef : listResult.getItems()) {
                    if (fileRef.getName().contains(currentUser.getUid())) {
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

    /**
     * permet d'ouvrire les mp possible
     */
    private void cliquemessage() {
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UtilisateurMP.class));
                finish();
            }
        });
    }

    /**
     * permet d'avoir les info des post
     */
    private void cliqueProfilInfoPost() {
        profilInfoPoste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfilInfo.class));
                finish();
            }
        });
    }

    /**
     * permet d'aller a la page d'aceuil
     */
    private void cliqueHome() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
        });
    }
}