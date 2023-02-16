package com.example.instantlike.InteractionUtilisateur;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageEntreUtilisateur extends AppCompatActivity {

    private static final String COLLECTION_MP = "MP";
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_DATE = "date";

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
     * Vérifie si l'utilisateur est connecté.
     */
    public void onStart() {
        super.onStart();
        // Vérifie si l'utilisateur est connecté.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
     * Récupère les paramètres passés par RoomActivity.
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
     * Écoute les modifications de la collection MP dans Firestore.
     */
    private void testMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(COLLECTION_MP);
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Error", error);
                    return;
                }
                if (!value.isEmpty()) {
                    // Efface toutes les listes pour éviter les doublons.
                    messageEnvoy.clear();
                    dateMessage.clear();
                    droitOuGauche.clear();
                    iniMessages();
                }
            }
        });
    }

    /**
     * Initialise les messages.
     */
    private void iniMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_MP)
                .whereEqualTo(FieldPath.documentId(), currentUser.getUid() + ":" + idUtilisateur)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String[] idParts = id.split(":");
                                String UtilisateurMessage = idParts[1];

                                remplirMessages(document, UtilisateurMessage);
                            }
                        } else {
                            Log.d("Error", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void remplirMessages(QueryDocumentSnapshot document, String UtilisateurMessage) {
        messageEnvoy = (ArrayList<String>) document.get(FIELD_MESSAGE);
        dateMessage = (ArrayList<String>) document.get(FIELD_DATE);

        // Afficher les messages à droite ou à gauche suivant le destinataire.
        for (int i = 0; i < messageEnvoy.size(); i++) {
            if (UtilisateurMessage.equals(currentUser.getUid())) {
                // Droite.
                droitOuGauche.add(true);
            } else {
                // Gauche.
                droitOuGauche.add(false);
            }
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewMP);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageEntreUtilisateur.this));
        MessageUtilisateur adapter = new MessageUtilisateur(MessageEntreUtilisateur.this, messageEnvoy, dateMessage, droitOuGauche);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Envoie un message à la collection MP dans Firestore.
     */
    private void envoyerMessage() {
        envoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(message.getText())) {
                    Toast.makeText(MessageEntreUtilisateur.this, "Écrivez un message.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String date = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss").format(new Date());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection(COLLECTION_MP).document(currentUser.getUid() + ":" + idUtilisateur);

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Si la conversation existe déjà, ajouter le message.
                                messageEnvoy.add(message.getText().toString());
                                dateMessage.add(date);
                                documentReference.update(FIELD_MESSAGE, messageEnvoy, FIELD_DATE, dateMessage)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                message.setText("");
                                                Log.d("Update", "items array successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Update", "Error updating items array", e);
                                            }
                                        });
                            } else {
                                // Sinon, créer la conversation et ajouter le message.
                                Map<String, Object> donnees = new HashMap<>();
                                donnees.put(FIELD_MESSAGE, Collections.singletonList(message.getText().toString()));
                                donnees.put(FIELD_DATE, Collections.singletonList(date));
                                documentReference.set(donnees)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                message.setText("");
                                                Log.d("TAG", "onSuccess: Les données sont créées.");
                                            }
                                        });
                            }
                        } else {
                            Log.d("Error", "get failed with ", task.getException());
                        }
                    }
                });

                newMessage++;
            }
        });
    }
}