package com.example.instantlike;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.ImageAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.InteractionUtilisateur.UtilisateurMP;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.Profil.ProfilInfo;
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
    private String photoPath = null;
    private FirebaseAuth mAuth;
    private final ArrayList<String> imageListUriStorage = new ArrayList<>();
    private final ArrayList<String> imageListNameStorage = new ArrayList<>();
    private ArrayList<String> imageNameFirebase = new ArrayList<>();
    private ArrayList<String> titreImage = new ArrayList<>();
    private ArrayList<String> descImage = new ArrayList<>();
    private ArrayList<String> iconListName = new ArrayList<>();
    private ArrayList<String> iconListToken = new ArrayList<>();
    private ArrayList<String> iconList = new ArrayList<>();
    private ArrayList<String> nomUster = new ArrayList<>();
    private ImageButton home, message, profilInfoPoste;
    private FirebaseUser currentUser;
    private Uri photoUir;
    private androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImageAdapter adapter = new ImageAdapter(imageListUriStorage, imageListNameStorage, HomePage.this, titreImage, descImage, iconList, nomUster);


    /**
     * vérificatiosn que l'utilisateur est bien connecter
     */
    public void onStart() {
        super.onStart();
        // Check si l'user est connecté
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        //écouteur pour quand on poste un pote pour restet la fils
        //testMessage();
        photoClique();
        PosteClique();
        iniActivity();
    }


    /**
     * clique pour ouvrire l'appareil photo
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

    /**
     * BTN pour créer un poste
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

    /**
     * méthode d'inisialisations des variable de la view
     * et de la mise en place des appelle de méthode
     */
    private void iniActivity() {
        home = findViewById(R.id.HomeBTNPost);
        message = findViewById(R.id.MessageBTNPost);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNPost);
        progressBar = findViewById(R.id.progressBarMainActiviti);
        progressBar.setVisibility(View.VISIBLE);

        titreDescNomImage();

        //méthode pour la bar en bat
        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
    }

    /**
     * afficher tout les personne posible a mp
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
     * permet d'aller sur les informations du profil
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
     * permet de se rendre sur la page d'aceuil
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

    /**
     * méthode pour mettre a jour le recyclerVieuw qui affiche tout les postes
     * avec une boucle pour récupérer le titre et la descriptions de la bd temps réel
     * avec une boucle pour récupérer tout les images dans le storage firebase
     */
    private void imageScrol() {
        //bar de progrations de la conections a firebase
        //créations du recycler
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
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
                            imageListNameStorage.add(fileRef.getName());
                            imageListUriStorage.add(uri.toString());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful() && imageListNameStorage.size() == iconList.size()){
                                //trieImage();
                                //trieIcon();
                                progressBar.setVisibility(View.GONE);
                                recyclerView = findViewById(R.id.recyclerView);
                                LinearLayoutManager manager = new LinearLayoutManager(HomePage.this);
                                recyclerView.setLayoutManager(manager);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(adapter);
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     * récupére les icon des utilisateur
     */

    private void trieIcon() {
        String NomIcon;
        ArrayList<String> temps = new ArrayList<>();
        for (int i = 0; i < iconListToken.size(); i++) {
            NomIcon = iconListToken.get(i);
            for (int j = 0; j < iconListName.size(); j++) {
                if (NomIcon.equals(iconListName.get(j))) {
                    temps.add(iconList.get(j));
                    break;
                }
            }
        }
        iconList.clear();
        iconList.addAll(temps);
    }

    private void trieImage() {
        String NomImage;
        ArrayList<String> tempsURI = new ArrayList<>();
        ArrayList<String> tempsName = new ArrayList<>();
        for (int i = 0; i < imageNameFirebase.size(); i++) {
            NomImage = imageNameFirebase.get(i);
            for (int j = 0; j < imageNameFirebase.size(); j++) {
                if (NomImage.equals(imageListNameStorage.get(j))) {
                    tempsURI.add(imageListUriStorage.get(j));
                    tempsName.add(imageListNameStorage.get(j));
                    break;
                }
            }
        }
        imageListUriStorage.clear();
        imageListNameStorage.clear();
        imageListUriStorage.addAll(tempsURI);
        imageListNameStorage.addAll(tempsName);
    }

    /**
     * récupére les descriptions titre et nom des image du poste
     */
    private void titreDescNomImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //récupérations du nom de l'image
                        imageNameFirebase.add(document.getId());
                        //récupérations de l'userPoste
                        iconListToken.add(document.getData().get("UserPoste").toString());
                        //récupérations des titre
                        titreImage.add(document.getData().get("Titre").toString());
                        //récupérations des descriptions
                        descImage.add(document.getData().get("Descriptions").toString());
                    }
                    nomUtilisateur();
                    iconUtilisateur();
                } else {
                    Toast.makeText(HomePage.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * récupére le nom de l'utilisteur du poste
     */
    private void nomUtilisateur() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //récupérations du nom de l'image
                    String userName;
                    for (int i = 0; i < iconListToken.size(); i++) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userName = document.getId();
                            if (userName.equals(iconListToken.get(i))) {
                                nomUster.add(document.getData().get("username").toString());
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(HomePage.this, "Error getting documents", Toast.LENGTH_SHORT).show();
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
                String c;
                for (int i = 0; i < iconListToken.size(); i++) {
                    for (StorageReference fileRef : listResult.getItems()) {
                        c = fileRef.getName();
                        if (iconListToken.get(i).equals(c)) {
                            //actualisations pour avoir un chiffre différent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //on récupére uri qui est le lien ou trouver les données
                                    iconList.add(uri.toString());
                                    String name = uri.getLastPathSegment();
                                    name = name.substring(name.indexOf("/") + 1);
                                    iconListName.add(name);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    imageScrol();
                                }
                            });
                            break;
                        }
                    }
                }
                // on fait une boucle pour stocker les images une par une
            }
        });

    }

}