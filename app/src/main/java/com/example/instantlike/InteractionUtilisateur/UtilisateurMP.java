package com.example.instantlike.InteractionUtilisateur;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.MPAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.HomePage;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.Profil.ProfilInfo;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UtilisateurMP extends AppCompatActivity {

    private ImageButton home, message, profilInfoPoste;
    private androidx.appcompat.widget.Toolbar toolbar;
    private static final int RETOUR_PHOTO = 1;
    private String photoPath;
    private Uri photoUir;
    private ArrayList<String> nomUtilisateurMP = new ArrayList<>();
    private ArrayList<String> idUtilisateurMp = new ArrayList<>();
    private ArrayList<String> iconUtilisateurMP = new ArrayList<>();
    private ArrayList<String> iconUtilisateurMPToken = new ArrayList<>();
    private FirebaseUser currentUser;

    /**
     * r??cup??rations de l'id de l'utilisateur ou redirections a la connection
     */
    public void onStart() {
        super.onStart();
        // Check si l'user est connect??
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilisateur_mp);
        iniActivity();
    }

    /**
     * initialisations des variable est liste des m??thodes utiliser
     */
    private void iniActivity() {
        home = findViewById(R.id.HomeBTNMpUtilisateur);
        message = findViewById(R.id.MessageBTNMpUtilisateur);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNMpUtilisateur);
        toolbar = findViewById(R.id.toolbar);
        photoClique();
        PosteClique();
        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
        utilisateurAMP();
    }


    /**
     * listeneur du clique pour les photo
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
     * m??thode qui mais en place l'appreille photo
     * avec la cr??ations de a ?? z de l'image en cr??ent tout les donn??es de l'image
     */
    private void adimage() {
        //on cr??e l'appareille photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // on regarde si la personne a pris une photo et veux la valider
        if (intent.resolveActivity(getPackageManager()) != null) {
            // on cr??e tout les donn??es corespondent a l'image
            String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                photoUir = FileProvider.getUriForFile(UtilisateurMP.this, UtilisateurMP.this.getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUir);
                startActivityForResult(intent, RETOUR_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * red??finitions de la m??thode onActivityResult qui permet d'avoir un retour sur la capture faite aux pr??alable
     * tout en enlevent tout les ??vent listeneur
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // on regarde si le r??sultat de la photo et un  sucer si oui on peux cr??er un poste
        if (requestCode == RETOUR_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), CreationPoste.class);//cr??ations de la page Game
            intent.putExtra("image", photoPath);//on donne en extrat la valeur de la roomName pour savoir si la personne et un gest ou l'host
            intent.putExtra("uri", photoUir);
            startActivity(intent);//on lance l'activiter
            finish();
        }
    }

    /**
     * listeneur pour aller sur la cr??ations d'un poste
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
     * listeneur pour voir les personne a Mp
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
     * listeneur pour aller sur le profil
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
     * listeneur pour la page d'acueil
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
     * liste des personnes qu'on peux mp
     */
    private void utilisateurAMP() {
        nomUtil();
    }

    /**
     * m??thode qui vas chercher tout les nom des utiliseurs a mp sof soi m??me
     */
    private void nomUtil() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals(currentUser.getUid())) {
                            //date du poste
                            String userName = document.getData().toString();
                            userName = userName.substring(userName.indexOf("username=") + 9);
                            if (userName.indexOf(",") == -1)
                                userName = userName.substring(0, userName.indexOf("}"));
                            else userName = userName.substring(0, userName.indexOf(","));
                            nomUtilisateurMP.add(userName);
                            idUtilisateurMp.add(document.getId());
                        }
                    }
                    //m??thode pour r??cupe les icons
                    iconUtil();
                }
            }
        });
    }

    /**
     * m??thode qui vas chercher tout les icon des utiliseurs a mp sof soi m??me
     */
    private void iconUtil() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (int i = 0; i < idUtilisateurMp.size(); i++) {
                    for (StorageReference fileRef : listResult.getItems()) {
                        String c = fileRef.getName();
                        if (c.contains(idUtilisateurMp.get(i))) {
                            //actualisations pour avoir un chiffre diff??rent a chaque foi
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    iconUtilisateurMP.add(uri.toString());
                                    String name = uri.getLastPathSegment();
                                    name = name.substring(name.indexOf("/") + 1);
                                    iconUtilisateurMPToken.add(name);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful() && iconUtilisateurMP.size() == idUtilisateurMp.size()) {
                                        triIcon();
                                        final RecyclerView recyclerView = findViewById(R.id.recyclerViewMPutilisateur);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(UtilisateurMP.this));
                                        MPAdapter adapter = new MPAdapter(UtilisateurMP.this, iconUtilisateurMP, nomUtilisateurMP, idUtilisateurMp);
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
        for (int i = 0; i < idUtilisateurMp.size(); i++) {
            NomIcon = idUtilisateurMp.get(i);
            for (int j = 0; j < iconUtilisateurMPToken.size(); j++) {
                if (NomIcon.equals(iconUtilisateurMPToken.get(j))) {
                    temps.add(iconUtilisateurMP.get(j));
                    break;
                }
            }
        }
        iconUtilisateurMP.clear();
        iconUtilisateurMP.addAll(temps);
    }


}
