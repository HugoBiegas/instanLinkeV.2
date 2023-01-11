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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    private void BtnAdImage() {
        adIimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(Register.this, permissions)) {
                    Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryintent, 1);
                } else {
                        //afficher une demande de permisions
                        EasyPermissions.requestPermissions(Register.this, "Access for storage", 101, permissions);
                }
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
    @RequiresApi(api = Build.VERSION_CODES.M)
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
            imageView.setImageIcon(Icon.createWithBitmap(image2));
            //on retrouve l'uri avec le path et on le stock pour le maitre dans la bd
            File f = new File(imgPath);
            uri = Uri.fromFile(f);
            passage = true;
        }
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
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Si le compte est créer on affiche un toast, on va rentrer ensuite avec son userid dans la bd et dans la collection "users" introduire l'username et l'email pour nous les
                            //réutiliser plus tard. Ensuite on redirige vers Main
                            //Sachant que FirebaseAuth n'a besoin que de l'email et du mdp
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Compte créer!", Toast.LENGTH_SHORT).show();
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
                                        Log.d("TAG", "onSuccess: Le profil a été créer pour" + userID);
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), HomePage.class));
                                finish();
                            } else {
                                //si cela a échoué pour une quelquonque raison
                                Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                mRegisterBtn.setEnabled(true);
                            }
                        }
                    });
                }else{
                    Toast.makeText(Register.this, "ajouter une image", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * méthode permettent de rendre chaque image unique
     * donc pour les différentier avec un clée unique
     *
     * @param imageUri
     */
    private void envoiImage(Uri imageUri) {
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("Icone/" + userID);
        mImageRef.putFile(imageUri);
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


}