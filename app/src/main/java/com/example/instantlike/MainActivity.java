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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.ImageAdapter;
import com.example.instantlike.Connection.Login;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // vidéo pour l'appareile photo : https://www.youtube.com/watch?v=8890GpBwn9w
    // vidéo pour les liste view : https://www.youtube.com/watch?v=KY5vOVNqkGM
    private static final int RETOUR_PHOTO = 1;
    private Button adImage, poste;
    private String photoPath = null;
    private Uri photoUir;
    private final ArrayList<String> titreList = new ArrayList<>();
    private final ArrayList<String> descList = new ArrayList<>();
    private ArrayList<DatabaseReference> StopEventListener = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
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
        final ArrayList<String> imageList = new ArrayList<>();
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final ImageAdapter adapter = new ImageAdapter(imageList, this, titreList, descList);
        //créations du recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        progressBar.setVisibility(View.VISIBLE);
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                double p;
                // on fait une boucle pour stocker les images une par une
                for (StorageReference fileRef : listResult.getItems()) {
                    //actualisations pour avoir un chiffre différent a chaque foi
                    p = Math.random();
                    //mise en place des écouteur pour les titre et descriptions
                    //DatabaseReference myRef = database.getReference("images/" + fileRef.getName());
                    //StopEventListener.add(myRef);
                    //TitreAd(myRef);
                    //DescAd(myRef);
                    //actualisations pour lire les données
                    //myRef = database.getReference("images/" + fileRef.getName() + "/actu");
                    //myRef.setValue(" " + p);
                    //lesser le temps de traitement pour la BD relative
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //on récupére uri qui est le lien ou trouver les données
                            imageList.add(uri.toString());
                            Log.d("item", uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //on ajouter tout les image dans le recycler
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    /**
     * methode pour mettre en place le listener pour l'affichage du titre
     *
     * @param myRef
     */
    private void TitreAd(DatabaseReference myRef) {
        myRef.addValueEventListener(TitreListerner());
    }

    /**
     * méthode pour pouvoir par la suite remouve le listeneur
     * avec concaténation de la chaine pour récupérer le titre exacte
     *
     * @return
     */
    private ValueEventListener TitreListerner() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chaine = dataSnapshot.getValue().toString();
                chaine = chaine.substring(chaine.indexOf("Titre") + 6);
                chaine = chaine.substring(0, chaine.indexOf(","));
                titreList.add(chaine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    /**
     * méthode pour mettre en place le listeneur pour l'affichage de la descriptions
     *
     * @param myRef
     */
    private void DescAd(DatabaseReference myRef) {
        myRef.addValueEventListener(DescListerner());
    }

    /**
     * méthode pour pouvoir par la suite suprimer le listeneur
     * avec concaténation de la chaine pour récupérer la descriptions exacte
     *
     * @return
     */
    private ValueEventListener DescListerner() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chaine = dataSnapshot.getValue().toString();
                chaine = chaine.substring(chaine.indexOf("descriptions") + 13);
                chaine = chaine.substring(0, chaine.indexOf(","));
                descList.add(chaine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    /**
     * méthode pour aller ajouter un poste tout en supriment les event listeneur
     */
    private void adPoste() {
        poste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enlever les évent lister
                for (int i = 0; i < StopEventListener.size(); i++) {
                    StopEventListener.get(i).removeEventListener(TitreListerner());
                    StopEventListener.get(i).removeEventListener(DescListerner());
                }
                startActivity(new Intent(getApplicationContext(), Poste.class));
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
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                photoUir = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".provider", photoFile);
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
            //enlever les évent lister
            for (int i = 0; i < StopEventListener.size(); i++) {
                StopEventListener.get(i).removeEventListener(TitreListerner());
                StopEventListener.get(i).removeEventListener(DescListerner());
            }
            Intent intent = new Intent(getApplicationContext(), Poste.class);//créations de la page Game
            intent.putExtra("image", photoPath);//on donne en extrat la valeur de la roomName pour savoir si la personne et un gest ou l'host
            intent.putExtra("uri", photoUir);
            startActivity(intent);//on lance l'activiter
            finish();
        }
    }
}