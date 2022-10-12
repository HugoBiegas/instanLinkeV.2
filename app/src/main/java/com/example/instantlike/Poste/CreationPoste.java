package com.example.instantlike.Poste;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.HomePage;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreationPoste extends AppCompatActivity {

    private Button retour, poster;
    private Bitmap image;
    private ImageView imagePoste;
    private Uri photoUri;
    private String uuid;
    private EditText titre, descriptions;
    private Button ajoutImage;
    private FirebaseAuth mAuth;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;

    /**
     * méthode qui verifie que la personne et connecter et la redirige si non
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
        setContentView(R.layout.activity_poste);
        iniActyvity();
    }

    /**
     * méthode pour gérer les donnée envoiller par la view MainActivity
     */
    private void extraDonnée() {
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            String photoPath = extra.getString("image");
            image = BitmapFactory.decodeFile(photoPath);
            photoUri = extra.getParcelable("uri");
            imagePoste.setImageBitmap(image);
            ajoutImage.setVisibility(View.INVISIBLE);
        } else ajoutImageTel();
    }

    /**
     * méthode d'inisialisations des variable de la view
     * et de la mise en place des appelle de méthode
     */
    private void iniActyvity() {
        retour = findViewById(R.id.retour);
        poster = findViewById(R.id.poster);
        titre = findViewById(R.id.titre);
        ajoutImage = findViewById(R.id.ajoutImage);
        descriptions = findViewById(R.id.descriptions);
        imagePoste = findViewById(R.id.imagePoste);
        progressBar = findViewById(R.id.progressBarPoste);
        //Prendre instance de firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        extraDonnée();
        retourHome();
        enregistrerImage();
    }

    /**
     * méthode pour avoir le bouton pour ajouter une image
     * si on ne passe pas par l'appareille photo
     * avec gestions des droit (doit avoir la permisions de récupérer des donnée du téléphone)
     */
    private void ajoutImageTel() {
        ajoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (ActivityCompat.checkSelfPermission(poste.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent, 1);
                //} else {
                //demander la permisions
                //    if (!ActivityCompat.shouldShowRequestPermissionRationale(poste.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                //         String[] permissions = {Manifest.permission.MANAGE_EXTERNAL_STORAGE};
                //afficher une demande de permisions
                //         ActivityCompat.requestPermissions(poste.this, permissions, 2);
                //    } else {
                //afficher un message que la permisiions est obligatoir
                //    }
                // }
            }
        });
    }

    /**
     * redéfinitions de la méthode onActivityResult qui permet d'avoir un retour sur l'inportations de l'image choisi
     * et traitement de cette image (récupérations du chemin de l'image et récupérations de l'image par la suite)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // verifier qu'une image est selectionner
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectImage = data.getData();
            String[] fillePathColum = {MediaStore.Images.Media.DATA};
            // curseur d'accer au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectImage, fillePathColum, null, null, null);
            //positions sur la premier ligne
            cursor.moveToFirst();
            //récupérations chemin préci de l'image
            int columIndex = cursor.getColumnIndex(fillePathColum[0]);
            String imgPath = cursor.getString(columIndex);
            cursor.close();
            //récupérations de l'image
            Bitmap image2 = BitmapFactory.decodeFile(imgPath);
            Toast.makeText(this, imgPath, Toast.LENGTH_SHORT).show();
            Glide.with(this /* context */).load(image2).into(imagePoste);
            ajoutImage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * méthode pour le btn pour revenir a la page d'acceuil
     */
    private void retourHome() {
        retour.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                finish();
            }
        });
    }

    /**
     * méthode pour ajouter un poste
     * envoi de l'image dans le storage firebase
     * envoi du titre et de la descriptions dans la bd temps réel
     */
    private void enregistrerImage() {
        poster.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si il y as une image on l'envoie
                if (null != imagePoste.getDrawable() && titre.getText().length() != 0 && descriptions.getText().length() != 0) {
                    //Poster l'image sur le storage
                    posterImage(photoUri);
                    //rajouter dans firebase le titre et le commentaire
                    ajoutBDFirestore(titre.getText().toString(), descriptions.getText().toString());
                    likeIni();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    finish();
                } else
                    Toast.makeText(CreationPoste.this, "saisisez une image ,un titre et une descriptions", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * ajouter  l'image dans firestore
     *
     * @param Titre
     * @param desc
     */
    private void ajoutBDFirestore(String Titre, String desc) {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("images").document(uuid);
        Map<String, Object> donnée = new HashMap<>();
        donnée.put("Titre", Titre);
        donnée.put("Descriptions", desc);
        donnée.put("Like", 0);
        donnée.put("DatePoste", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        donnée.put("UserPoste", userID);
        documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: Les données son créer");
            }
        });

    }

    /**
     * initialisations des like pour une image
     */
    private void likeIni() {
        DocumentReference documentReference = fStore.collection("like").document(uuid);
        Map<String, Object> donnée = new HashMap<>();
        donnée.put("nbLike", 0);
        documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: Les données son créer");
            }
        });
    }

    /**
     * méthode permettent de rendre chaque image unique
     * donc pour les différentier avec un clée unique
     *
     * @param imageUri
     */
    private void posterImage(Uri imageUri) {
        uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING tock
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + uuid);
        mImageRef.putFile(imageUri);
    }

}