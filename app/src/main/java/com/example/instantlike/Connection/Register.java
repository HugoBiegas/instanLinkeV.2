package com.example.instantlike.Connection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instantlike.MainActivity;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    //Initialisation des variables
    private EditText mEmail, mUser, mPassword, mCPassword;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Recherche des id sur le layout register
        mEmail = findViewById(R.id.editTextEmail);
        mUser = findViewById(R.id.editTextLogin);
        mPassword = findViewById(R.id.editTextPassword);
        mCPassword = findViewById(R.id.editTextCPassword);
        mRegisterBtn = findViewById(R.id.buttonLogin);
        mLoginBtn = findViewById(R.id.createText);

        //Prendre instance de firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);


        //Partie enregistrement du compte avec le bouton mRegisterBtn
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
                if (username.contains("victoir") || username.contains("defaite") || username.equals("")) {
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
                            startActivity(new Intent(getApplicationContext(), com.example.instantlike.MainActivity.class));
                        } else {
                            //si cela a échoué pour une quelquonque raison
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            mRegisterBtn.setEnabled(true);
                        }
                    }
                });
            }
        });

        //Button switch connexion
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}