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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.MessageUtilisateur;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ArrayList<String> messageEnvoyBase = new ArrayList<>();
    private ArrayList<Date> dateMessageBase = new ArrayList<>();
    private ArrayList<Boolean> droitOuGaucheBase = new ArrayList<>();
    private ArrayList<String> dateMessageRéel = new ArrayList<>();
    private FirebaseUser currentUser;

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
        extrat();
        envoyerMessage();
        testMessage();
    }

    private void extrat() {
        Bundle extra = getIntent().getExtras();//récuper l'extrat envoiller par roomActivity
        if (extra != null) {
            nom = extra.getString("nom");
            icon = extra.getString("icon");
            idUtilisateur = extra.getString("id");
            newMessage = extra.getInt("nbMessage");

        }
    }

    private void testMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("MP");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    messageEnvoy.clear();
                    dateMessage.clear();
                    droitOuGauche.clear();
                    iniMassage();
                }
            }
        });
    }

    private void iniMassage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MP")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //récupérations de tout les messages dans le désordre
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().contains(currentUser.getUid()) && document.getId().contains(idUtilisateur)) {
                                    //date du poste
                                    String message = document.getData().toString();
                                    message = message.substring(message.indexOf("message=") + 8);
                                    if (message.indexOf(",") == -1)
                                        message = message.substring(0, message.indexOf("}"));
                                    else
                                        message = message.substring(0, message.indexOf(","));
                                    messageEnvoyBase.add(message);

                                    String date = document.getData().toString();
                                    date = date.substring(date.indexOf("date=") + 5);
                                    if (date.indexOf(",") == -1)
                                        date = date.substring(0, date.indexOf("}"));
                                    else
                                        date = date.substring(0, date.indexOf(","));
                                    try {
                                        dateMessageBase.add(new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss").parse(date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    dateMessageRéel.add(date);

                                    String c = document.getId();
                                    c = c.substring(c.indexOf(":") + 1);
                                    if (c.contains(currentUser.getUid()))
                                        //droite
                                        droitOuGaucheBase.add(true);
                                    else
                                        //gauche
                                        droitOuGaucheBase.add(false);
                                }
                            }
                            Toast.makeText(MessageEntreUtilisateur.this, ""+dateMessageBase.get(0), Toast.LENGTH_SHORT).show();
                            //remetre dans le bonne hordre les messages
                            int positions;
                            Date ini;
                            //trier par date de publications
                            while (dateMessageBase.size() != 0) {
                                positions = 0;
                                ini = dateMessageBase.get(0);
                                if (dateMessageBase.size() != 1){
                                    for (int i = 1; i < dateMessageBase.size(); i++) {
                                        if (dateMessageBase.get(i).before(ini)) {
                                            ini = dateMessageBase.get(i);
                                            positions = i;
                                        }
                                    }
                                }

                                //remplie les messages
                                messageEnvoy.add(messageEnvoyBase.get(positions));
                                droitOuGauche.add(droitOuGaucheBase.get(positions));
                                dateMessage.add(dateMessageRéel.get(positions));

                                //suprimer les messages
                                dateMessageBase.remove(positions);
                                droitOuGaucheBase.remove(positions);
                                messageEnvoyBase.remove(positions);
                                dateMessageRéel.remove(positions);
                            }
                            final RecyclerView recyclerView = findViewById(R.id.recyclerViewMP);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageEntreUtilisateur.this));
                            MessageUtilisateur adapter = new MessageUtilisateur(MessageEntreUtilisateur.this, messageEnvoy, dateMessage, droitOuGauche);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }

    private void envoyerMessage() {
        envoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().toString().length() != 0) {
                    //créations du message dans la BD
                    String date = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss").format(new Date());
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = fStore.collection("MP").document(currentUser.getUid() + ":" + idUtilisateur + "::" + date);
                    newMessage++;
                    Map<String, Object> donnée = new HashMap<>();
                    donnée.put("message", message.getText().toString());
                    donnée.put("date", date);
                    documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess: Les données son créer");
                        }
                    });
                    message.setText("");
                } else {
                    Toast.makeText(MessageEntreUtilisateur.this, "écriver un message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}