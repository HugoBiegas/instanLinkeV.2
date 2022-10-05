package com.example.instantlike;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.ImageAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.Poste.CreationPoste;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomePage extends AppCompatActivity {
    // vidéo pour l'appareile photo : https://www.youtube.com/watch?v=8890GpBwn9w
    // vidéo pour les liste view : https://www.youtube.com/watch?v=KY5vOVNqkGM
    private static final int RETOUR_PHOTO = 1;
    private Button adImage, poste;
    private String photoPath = null;
    private Uri photoUir;
    private FirebaseAuth mAuth;
    private final ArrayList<String> imageListUri = new ArrayList<>();
    private final ArrayList<String> imageListName = new ArrayList<>();
    private ArrayList<String> imageName = new ArrayList<>();
    private ArrayList<String> titreImage = new ArrayList<>();
    private ArrayList<String> descImage = new ArrayList<>();


    public void onStart() {
        super.onStart();
        // Check si l'user est connecté
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        titreDescNomImage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniActivity();
    }

    /**
     * méthode d'inisialisations des variable de la view
     * et de la mise en place des appelle de méthode
     */
    private void iniActivity() {
        adImage = findViewById(R.id.adImageBtn);
        poste = findViewById(R.id.poste);
        recupérationImage();
        adPoste();
        imageScrol();
    }

    /**
     * méthode pour mettre a jour le recyclerVieuw qui affiche tout les postes
     * avec une boucle pour récupérer le titre et la descriptions de la bd temps réel
     * avec une boucle pour récupérer tout les images dans le storage firebase
     */
    private void imageScrol() {
        //bar de progrations de la conections a firebase
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        //créations du recycler
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        progressBar.setVisibility(View.VISIBLE);
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // on fait une boucle pour stocker les images une par une
                for (StorageReference fileRef : listResult.getItems()) {
                    //actualisations pour avoir un chiffre différent a chaque foi
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //on récupére uri qui est le lien ou trouver les données
                            imageListName.add(fileRef.getName());
                            imageListUri.add(uri.toString());
                            Log.d("item", uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //on ajouter tout les image dans le recycler
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void titreDescNomImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //récupérations du nom de l'image
                                imageName.add(document.getId());
                                //récupérations des titre
                                String titre = document.getData().toString();
                                titre = titre.substring(titre.indexOf("Titre=") + 6);
                                if (titre.indexOf(",") == -1)
                                    titre = titre.substring(0, titre.indexOf("}"));
                                else
                                    titre = titre.substring(0, titre.indexOf(","));
                                titreImage.add(titre);
                                //récupérations des descriptions
                                String desc = document.getData().toString();
                                desc = desc.substring(desc.indexOf("Descriptions=") + 13);
                                if (desc.indexOf(",") == -1)
                                    desc = desc.substring(0, desc.indexOf("}"));
                                else
                                    desc = desc.substring(0, desc.indexOf(","));
                                descImage.add(desc);
                            }
                            final RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(HomePage.this));

                            ImageAdapter adapter = new ImageAdapter(imageListUri, imageListName, HomePage.this, titreImage, descImage, imageName);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Toast.makeText(HomePage.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * méthode pour aller ajouter un poste tout en supriment les event listeneur
     */
    private void adPoste() {
        poste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreationPoste.class));
                finish();
            }
        });
    }

    /**
     * méthode pour créer l'appareille photo en cliquent sur le btn
     */
    private void recupérationImage() {
        adImage.setOnClickListener(new Button.OnClickListener() {
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
                photoUir = FileProvider.getUriForFile(HomePage.this, HomePage.this.getApplicationContext().getPackageName() + ".provider", photoFile);
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
}