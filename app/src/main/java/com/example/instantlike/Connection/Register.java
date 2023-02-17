package com.example.instantlike.Connection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.instantlike.HomePage;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

public class Register extends AppCompatActivity {
    //Initialisation des variables
    private EditText mEmail, mUser, mPassword, mCPassword;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Button adIimage;
    private ImageView imageView;
    private Uri uri;
    private Boolean passage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iniActivity();
        //Recherche des id sur le layout register
    }

    private void iniActivity() {
        mEmail = findViewById(R.id.editTextEmail);
        mUser = findViewById(R.id.editTextLogin);
        mPassword = findViewById(R.id.editTextPassword);
        mCPassword = findViewById(R.id.editTextCPassword);
        mRegisterBtn = findViewById(R.id.buttonLogin);
        mLoginBtn = findViewById(R.id.createText);
        adIimage = findViewById(R.id.ajoutImageCreationCompt);
        imageView = findViewById(R.id.iconUtilisateurCréationCompte);

        //Prendre instance de firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        BtnRegister();
        BtnDejatConnecter();
        BtnAdImage();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Vérifier qu'une image a été sélectionnée et que l'action est "OK"
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Obtenir l'URI de l'image sélectionnée
            Uri selectedImage = data.getData();
            // Obtenir le chemin du fichier pour l'image sélectionnée
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            // Charger l'image à partir du fichier
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            // Afficher l'image dans l'ImageView
            imageView.setImageBitmap(bitmap);
            // Enregistrer l'URI de l'image pour l'envoyer à Firebase Storage
            uri = Uri.fromFile(new File(filePath));
            passage = true;
        }
    }


    private void BtnAdImage() {
        adIimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(Register.this, permissions)) {
                    Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryintent, 1);
                } else {
                    // afficher une demande de permissions
                    EasyPermissions.requestPermissions(Register.this, "Access for storage", 101, permissions);
                }
            }
        });
    }
    private void BtnDejatConnecter() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
    /**
     * Envoie une image sélectionnée par l'utilisateur à Firebase Storage
     * pour l'associer au compte utilisateur créé.
     *
     * @param imageUri L'URI de l'image sélectionnée.
     */
    private void envoiImage(Uri imageUri) {
        // Récupère la référence à l'emplacement de stockage pour l'icône de l'utilisateur
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("Icone/" + userID);

        // Télécharge l'image à l'emplacement spécifié
        mImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Si l'envoi de l'image a réussi, affiche un message de succès.
                        Log.d("Register", "envoiImage: Image envoyée avec succès.");
                        Toast.makeText(Register.this, "Image de profil mise à jour", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Si l'envoi de l'image a échoué, affiche un message d'erreur.
                        Log.e("Register", "envoiImage: Échec de l'envoi de l'image. Erreur : " + e.getMessage());
                        Toast.makeText(Register.this, "Erreur lors de l'envoi de l'image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void BtnRegister() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Attribution des variables (gauche) avec les champ d'inputs (droite)
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String username = mUser.getText().toString().trim();

                //Controle d'erreurs
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email requis");
                    return;
                }
                if (username.equals("")) {
                    mUser.setError("nom incorecte");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Mot de passe requis");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Mot de passe trop faible");
                    return;
                }

                String passc = mCPassword.getText().toString();
                if (!password.equals(passc)) {
                    mPassword.setError("Confirmer votre mdp svp !!");
                    return;
                }
                if(null != imageView.getDrawable()){
                    progressBar.setVisibility(View.VISIBLE);
                    mRegisterBtn.setEnabled(false);

                    // Vérification si le nom d'utilisateur est unique
                    fStore.collection("users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        // Nom d'utilisateur unique
                                        fAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(Register.this, taskAuth -> {
                                                    if (taskAuth.isSuccessful()) {
                                                        Toast.makeText(Register.this, "Compte créé!", Toast.LENGTH_SHORT).show();
                                                        userID = fAuth.getCurrentUser().getUid();
                                                        if (passage)
                                                            envoiImage(uri);
                                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("username", username);
                                                        user.put("email", email);
                                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "onSuccess: Le profil a été créé pour " + userID);
                                                            }
                                                        });
                                                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(Register.this, "Error! " + taskAuth.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        mRegisterBtn.setEnabled(true);
                                                    }
                                                });
                                    } else {
                                        // Nom d'utilisateur déjà pris
                                        mUser.setError("Nom d'utilisateur déjà pris !");
                                        progressBar.setVisibility(View.GONE);
                                        mRegisterBtn.setEnabled(true);
                                    }
                                } else {
                                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    mRegisterBtn.setEnabled(true);
                                }
                            });
                } else {
                    Toast.makeText(Register.this, "Ajouter une image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    /**
     * Vérifie si l'adresse email est valide
     *
     * @param email l'adresse email à vérifier
     * @return vrai si l'adresse email est valide, faux sinon
     */
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Vérifie si le mot de passe respecte les critères de sécurité
     *
     * @param password le mot de passe à vérifier
     * @return vrai si le mot de passe respecte les critères de sécurité, faux sinon
     */
    private boolean isPasswordValid(CharSequence password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        return Pattern.matches(regex, password);
    }
}