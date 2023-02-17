package com.example.instantlike;

import static com.example.instantlike.Image.ImageData.getimages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
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
import com.example.instantlike.Image.ImageData;
import com.example.instantlike.message.UtilisateurMP;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.Profil.ProfilInfo;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePage extends AppCompatActivity {
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
    static List<ImageData> imageDataList = new ArrayList<>();
    private Boolean recycler= false;
    Boolean NewPhoto = false;
    ImageAdapter adapter = new ImageAdapter(imageDataList, HomePage.this);

    private String uriString, titre, description, userPoste,nomUtilisateurPoste,iconUri;

    public void onStart() {
        super.onStart();
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
        photoClique();
        PosteClique();
        iniActivity();
        ecouteurStorage();
    }

    private void iniActivity() {
        home = findViewById(R.id.HomeBTNPost);
        message = findViewById(R.id.MessageBTNPost);
        profilInfoPoste = findViewById(R.id.InfoPorofilBTNPost);
        progressBar = findViewById(R.id.progressBarMainActiviti);
        progressBar.setVisibility(View.VISIBLE);
        extraDonnée();
        if (NewPhoto== true || imageDataList.size() != 0){
            imageDataList.clear();
            setRecyclerView();
        }else
            titreDescNomImage();

        cliquemessage();
        cliqueProfilInfoPost();
        cliqueHome();
    }
    private void extraDonnée() {
        Bundle extra = getIntent().getExtras(); // Récupère l'extra envoyé par MainActivity
        if (extra != null)
            NewPhoto = true;

    }

    private void ecouteurStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    String fileName = item.getName().replace(".jpeg", "");
                    Boolean dejatPrix = true;
                    List<ImageData> data = ImageData.getimages();
                    for (ImageData dataImag: data) {
                        if (dataImag.getImageName().equals(fileName)){
                            dejatPrix = false;
                            break;
                        }
                    }

                    if (dejatPrix){
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String uriString = uri.toString();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("images").document(fileName);

                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String titre = documentSnapshot.getString("Titre");
                                            String description = documentSnapshot.getString("Descriptions");
                                            String userPoste = documentSnapshot.getString("UserPoste");

                                            db.collection("users").document(userPoste).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String nomUtilisateurPoste = documentSnapshot.getString("username");

                                                        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("icone/" + userPoste + ".jpeg");
                                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String iconUri = uri.toString();
                                                                // Utiliser les informations récupérées pour créer un nouvel objet ImageData
                                                                new ImageData(uriString, fileName, titre, description, iconUri, nomUtilisateurPoste);
                                                                Toast.makeText(HomePage.this, "Nouvelle image ajoutée", Toast.LENGTH_SHORT).show();
                                                                // Actualiser l'adapter pour afficher les nouvelles données
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }



    /**
     * Clique pour ouvrir l'appareil photo
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
     * Méthode qui met en place l'appareil photo
     * avec la création de A à Z de l'image en créant toutes les données de l'image
     */
    private void adimage() {
        // On crée l'appareil photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // On regarde si la personne a pris une photo et veut la valider
        if (intent.resolveActivity(getPackageManager()) != null) {
            // On crée toutes les données correspondantes à l'image
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
     * Redéfinition de la méthode onActivityResult qui permet d'avoir un retour sur la capture faite précédemment
     * tout en enlevant tous les événements listeners
     *
     * @param requestCode Le code de la requête envoyée
     * @param resultCode Le code de résultat retourné
     * @param data L'intent retourné contenant les données
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // On regarde si le résultat de la photo est OK, si oui on peut créer un poste
        if (requestCode == RETOUR_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), CreationPoste.class);//création de la page Game
            intent.putExtra("image", photoPath);// On donne en extra la valeur de la roomName pour savoir si la personne est un gest ou l'host
            intent.putExtra("uri", photoUir);
            startActivity(intent);// On lance l'activité
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

    private void cliqueHome() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
        });
    }


    private void imageScrol() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageListNameStorage.add(fileRef.getName());
                            imageListUriStorage.add(uri.toString());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful() && imageListNameStorage.size() == iconList.size() && recycler == false){
                                trieImage();
                                trieIcon();
                                for (int i = 0; i < imageListNameStorage.size(); i++) {
                                    new ImageData(imageListUriStorage.get(i), imageListNameStorage.get(i), titreImage.get(i), descImage.get(i), iconList.get(i), nomUster.get(i));
                                }
                                setRecyclerView();
                                recycler = true;
                            }
                        }
                    });
                }
            }
        });
    }

    private void setRecyclerView(){
            imageDataList.addAll(getimages());
            progressBar.setVisibility(View.GONE);
            recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(HomePage.this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
    }

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
                            });
                            break;
                        }
                    }
                }
                imageScrol();
                // on fait une boucle pour stocker les images une par une
            }
        });
    }
}