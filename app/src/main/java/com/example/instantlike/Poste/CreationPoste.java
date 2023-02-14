package com.example.instantlike.Poste;

import android.Manifest;
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
import com.example.instantlike.Image.ImageData;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class CreationPoste extends AppCompatActivity {

    private Button retour, poster;
    private Bitmap image;
    private ImageView imagePoste;
    private Uri photoUri;
    private String uuid;
    private EditText titre, descriptions;
    private Button ajoutImage;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;

    /**
     * Vérifie que la personne est connectée et la redirige si non
     */
    public void onStart() {
        super.onStart();
        // Vérifie si l'utilisateur est connecté
        FirebaseUser currentUser = fAuth.getCurrentUser();
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
     * Initialise les variables de la vue et appelle les méthodes nécessaires
     */
    private void iniActyvity() {
        retour = findViewById(R.id.retour);
        poster = findViewById(R.id.poster);
        titre = findViewById(R.id.titre);
        ajoutImage = findViewById(R.id.ajoutImage);
        descriptions = findViewById(R.id.descriptions);
        imagePoste = findViewById(R.id.imagePoste);
        progressBar = findViewById(R.id.progressBarPoste);
        // Prend l'instance de Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        extraDonnée();
        retourHome();
        enregistrerImage();
    }

    /**
     * Méthode pour gérer les données envoyées par la vue MainActivity
     */
    private void extraDonnée() {
        Bundle extra = getIntent().getExtras(); // Récupère l'extra envoyé par MainActivity
        if (extra != null) {
            String photoPath = extra.getString("image");
            image = BitmapFactory.decodeFile(photoPath);
            photoUri = extra.getParcelable("uri");
            imagePoste.setImageBitmap(image);
            ajoutImage.setVisibility(View.INVISIBLE);
        } else {
            ajoutImageTel();
        }
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
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(CreationPoste.this, permissions)) {
                    Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryintent, 1);
                } else {
                    // afficher une demande de permissions
                    EasyPermissions.requestPermissions(CreationPoste.this, "Access for storage", 101, permissions);
                }
            }
        });
    }

    /**
     * Méthode appelée lorsque l'utilisateur revient de la galerie après avoir sélectionné une image.
     * Cette méthode récupère l'URI de l'image sélectionnée et l'affiche dans l'ImageView imagePoste.
     *
     * @param requestCode Le code de requête envoyé à la galerie (dans notre cas, 1)
     * @param resultCode  Le code de résultat (RESULT_OK si l'utilisateur a sélectionné une image)
     * @param data        Les données renvoyées par la galerie (contenant l'URI de l'image sélectionnée)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Récupération de l'URI de l'image sélectionnée
            photoUri = data.getData();

            // Affichage de l'image dans l'ImageView imagePoste
            Glide.with(this)
                    .load(photoUri)
                    .into(imagePoste);

            // Masquage du bouton ajoutImage
            ajoutImage.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Méthode appelée lorsqu'on clique sur le bouton retour.
     * Cette méthode termine l'activité en cours et retourne à l'activité HomePage.
     */
    private void retourHome() {
        retour.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fermeture de l'activité en cours et retour à l'activité HomePage
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.putExtra("passe", true);
                startActivity(intent);
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
                // Vérifier que l'image, le titre et la description sont saisis
                if (null != imagePoste.getDrawable() && titre.getText().length() != 0 && descriptions.getText().length() != 0) {
                    //Poster l'image sur le storage
                    posterImage(photoUri, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            // Ajouter les données dans Firestore
                            ajoutBDFirestore(titre.getText().toString(), descriptions.getText().toString());
                            // Ajouter les données dans ImageData
                            new ImageData(uri.toString(),uuid+".jpg", titre.getText().toString(), descriptions.getText().toString(),uuid+".jpg",firebaseUser.getDisplayName());
                            // Retourner à la page HomePage
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            intent.putExtra("passe", true);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(CreationPoste.this, "Saisissez une image, un titre et une description", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Ajoute l'image dans Firestore
     *
     * @param Titre Le titre de l'image
     * @param desc La description de l'image
     */
    private void ajoutBDFirestore(String Titre, String desc) {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("images").document(uuid);
        Map<String, Object> donnée = new HashMap<>();
        donnée.put("Titre", Titre);
        donnée.put("Descriptions", desc);
        donnée.put("DatePoste", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        donnée.put("UserPoste", userID);
        donnée.put("Like", Arrays.asList());
        donnée.put("commentaire", Arrays.asList());
        donnée.put("Idcommentaire", Arrays.asList());

        documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: Les données sont créées");
            }
        });
    }

    /**
     * Envoie l'image dans Firebase Storage
     *
     * @param imageUri L'URI de l'image
     * @param onSuccessListener Le listener à exécuter après que l'image soit postée
     */
    private void posterImage(Uri imageUri, final OnSuccessListener<Uri> onSuccessListener) {
        uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING tock
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("images/" + uuid);
        mImageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mImageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
            }
        });
    }

}