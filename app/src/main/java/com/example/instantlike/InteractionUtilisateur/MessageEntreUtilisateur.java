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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    private void testMessage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("MP");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().contains(currentUser.getUid()) && document.getId().contains(idUtilisateur)) {
                                    //date du poste
                                    String message = document.getData().toString();
                                    message = message.substring(message.indexOf("message=") + 8);
                                    if (message.indexOf(",") == -1)
                                        message = message.substring(0, message.indexOf("}"));
                                    else
                                        message = message.substring(0, message.indexOf(","));
                                    messageEnvoy.add(message);

                                    String date = document.getData().toString();
                                    date = date.substring(date.indexOf("date=") + 5);
                                    if (date.indexOf(",") == -1)
                                        date = date.substring(0, date.indexOf("}"));
                                    else
                                        date = date.substring(0, date.indexOf(","));
                                    dateMessage.add(date);

                                    String c = document.getId();
                                    c = c.substring(c.indexOf(":") + 1);
                                    if (c.equals(currentUser.getUid()))
                                        //droite
                                        droitOuGauche.add(true);
                                    else
                                        //gauche
                                        droitOuGauche.add(false);
                                }
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
                Toast.makeText(MessageEntreUtilisateur.this, "je passe", Toast.LENGTH_SHORT).show();
                if (message.getText().toString().length() != 0){
                    //créations du message dans la BD
                    String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = fStore.collection("MP").document(currentUser.getUid()+":"+idUtilisateur+"::"+date);
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
                }else{
                    Toast.makeText(MessageEntreUtilisateur.this, "écriver un message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}