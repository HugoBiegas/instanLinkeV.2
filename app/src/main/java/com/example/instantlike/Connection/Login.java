package com.example.instantlike.Connection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantlike.MainActivity;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // Initialisation des variables
    private EditText mEmail, mPassword;
    private Button mLoginBtn;
    private TextView mCreateBtn, forgotTextLink;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private String userId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Recherche sur le layout activity_login
        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        mLoginBtn = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        mCreateBtn = findViewById(R.id.createText);
        fAuth = FirebaseAuth.getInstance();
        forgotTextLink = findViewById(R.id.forgotTextLink);
        mAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null)
            userId = fAuth.getCurrentUser().getUid();
        if (userId != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        Source:
        https:
//prograide.com/pregunta/43777/comment-detecter-si-un-utilisateur-est-dej-connecte--firebase
        //Vérification des champs avant d'appuyer sur le bouton
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email requis");
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

                mLoginBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);


                // Authentification de l'utilisateur
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Connexion réussi", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(getApplicationContext(), com.example.instantlike.MainActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Erreur! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            progressBar.setVisibility(View.GONE);
                            mLoginBtn.setEnabled(true);
                        }
                    }
                });
            }
        });


        //Bouton switch layout Creer compte
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });


        //Partie qui va suivre va concerner le reset de mdp
        //Popup quand on va appuyer sur le TextView forgotTextLink du layout login
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Changer de mot de passe ?");
                passwordResetDialog.setMessage("Entrer votre email.");
                passwordResetDialog.setView(resetMail);

                //Si appuie sur Oui, on vérifie que le mot de passe existe dans la bd
                passwordResetDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Lien envoyé sur votre email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Echec d'envoie du lien", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //Si appuie sur Non, on ferme la popup
                passwordResetDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }

    public void updateUI(FirebaseUser account) {

        if (account != null) {
            Toast.makeText(this, "You Signed In successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));

        } else {
            Toast.makeText(this, "You Didnt signed in", Toast.LENGTH_LONG).show();
        }

    }
}