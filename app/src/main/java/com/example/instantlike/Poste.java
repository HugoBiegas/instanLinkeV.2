package com.example.instantlike;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instantlike.Connection.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class Poste extends AppCompatActivity {

    private Button retour, poster;
    private Bitmap image;
    private ImageView imagePoste;
    private Uri photoUri;
    private String uuid;
    private EditText titre, descriptions;
    private Button ajoutImage;
    private ProgressBar progressBar;
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
        } else
            ajoutImageTel();
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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                    posterImage(photoUri);
                    //rajouter dans firebase le titre et le commentaire
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("images/" + uuid + "/descriptions");
                    myRef.setValue(descriptions.getText().toString());
                    myRef = database.getReference("images/" + uuid + "/Titre");
                    myRef.setValue(titre.getText().toString());
                    myRef = database.getReference("images/" + uuid + "/actu");
                    myRef.setValue(" 1");

                    //mettre une bare de chargement pour le temps d'uplode
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else
                    Toast.makeText(Poste.this, "sisisez une image ou une vidéo,un titre et une descriptions", Toast.LENGTH_SHORT).show();
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