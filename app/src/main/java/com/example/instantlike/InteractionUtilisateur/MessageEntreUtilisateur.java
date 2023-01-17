package com.example.instantlike.InteractionUtilisateur;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.MessageUtilisateur;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageEntreUtilisateur extends AppCompatActivity {

    private String nom, icon, idUtilisateur;
    private int newMessage;
    private EditText message;
    private ImageButton envoi;
    private ArrayList<String> messageEnvoy = new ArrayList<>();
    private ArrayList<String> dateMessage = new ArrayList<>();
    private ArrayList<Boolean> droitOuGauche = new ArrayList<>();
    private ArrayList<Boolean> droitOuGaucheBase = new ArrayList<>();
    private FirebaseUser currentUser;

    /**
     * verifications si l'utilisateur et connecter
     */
    public void onStart() {
        super.onStart();
        // Check si l'user est connecté
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
        setContentView(R.layout.activity_message);
        iniActiviti();
    }

    private void iniActiviti() {
        envoi = findViewById(R.id.envoyBTN);
        message = findViewById(R.id.messageMP);
        // Récupérer une référence à la barre d'outils dans la vue
        Toolbar toolbar = findViewById(R.id.toolbar_retour);
        // Configurer la barre d'outils pour qu'elle utilise le bouton "Retour" par défaut
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Ajouter un gestionnaire d'événement qui permet de faire un retour en arrière lorsque
        // le bouton "Retour" est cliqué
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Faire un retour en arrière dans l'historique de navigation
                onBackPressed();
            }
        });
        extrat();
        envoyerMessage();
        testMessage();
    }

    /**
     * récupérations des extrats ces a dire nom icon et idUtilisateur qui se fait mp
     */
    private void extrat() {
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            nom = extra.getString("nom");
            icon = extra.getString("icon");
            idUtilisateur = extra.getString("id");
            newMessage = extra.getInt("nbMessage");

        }
    }

    /**
     * listeneur qui actualisa a chaque changement de la collection MP de firebase
     */
    private void testMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("MP");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    //clear de tout les listes pour pas avoir de message en double
                    messageEnvoy.clear();
                    dateMessage.clear();
                    droitOuGauche.clear();
                    iniMassage();
                }
            }
        });
    }

    /**
     * initialisations des messages
     */
    private void iniMassage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MP").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //récupérations de tout les messages dans le désordre
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().contains(currentUser.getUid()) && document.getId().contains(idUtilisateur)) {
                            String UtilisateurMessage = document.getId();
                            UtilisateurMessage = UtilisateurMessage.substring(UtilisateurMessage.indexOf(":") + 1);

                            if (document.getId().contains(currentUser.getUid() + ":" + idUtilisateur))

                                remplirMessage(currentUser.getUid() + ":" + idUtilisateur, UtilisateurMessage);

                            if (document.getId().contains(idUtilisateur + ":" + currentUser.getUid()))

                                remplirMessage(idUtilisateur + ":" + currentUser.getUid(), UtilisateurMessage);
                        }
                    }

                }
            }
        });
    }

    private void remplirMessage(String document, String UtilisateurMessage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itemRef = db.collection("MP").document(document);
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        messageEnvoy = (ArrayList<String>) document.get("message");
                        //afficher le message a droit ou a gauche suivant la le destinataire
                        for (int i = 0; i < messageEnvoy.size(); i++) {
                            if (UtilisateurMessage.contains(currentUser.getUid()))
                                //droite
                                droitOuGauche.add(true);
                            else
                                //gauche
                                droitOuGauche.add(false);
                        }
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                remplirDate(document);
            }
        });
    }

    private void remplirDate(String document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itemRef = db.collection("MP").document(document);
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dateMessage = (ArrayList<String>) document.get("date");
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final RecyclerView recyclerView = findViewById(R.id.recyclerViewMP);
                recyclerView.setLayoutManager(new LinearLayoutManager(MessageEntreUtilisateur.this));
                MessageUtilisateur adapter = new MessageUtilisateur(MessageEntreUtilisateur.this, messageEnvoy, dateMessage, droitOuGauche);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    /**
     * listeneur pour envoyer un message avec tout les données de base : date et le message
     */
    private void envoyerMessage() {
        envoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().toString().length() != 0) {
                    //créations du message dans la BD
                    String date = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss").format(new Date());
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = fStore.collection("MP").document(currentUser.getUid() + ":" + idUtilisateur);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                messageEnvoy.add(message.getText().toString());
                                dateMessage.add(date);
                                //update la date
                                documentReference.update("message", messageEnvoy, "date", dateMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        message.setText("");
                                        Log.d("Update", "items array successfully updated!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Update", "Error updating items array", e);
                                    }
                                });
                            } else {
                                Map<String, Object> donnée = new HashMap<>();
                                donnée.put("message", Arrays.asList(message.getText().toString()));
                                donnée.put("date", Arrays.asList(date));
                                documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        message.setText("");
                                        Log.d("TAG", "onSuccess: Les données son créer");
                                    }
                                });
                            }
                        }
                    });
                    newMessage++;
                } else {
                    Toast.makeText(MessageEntreUtilisateur.this, "écriver un message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}