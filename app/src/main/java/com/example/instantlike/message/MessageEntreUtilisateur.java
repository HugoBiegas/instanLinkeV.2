package com.example.instantlike.message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instantlike.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MessageEntreUtilisateur extends AppCompatActivity {

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;
private String idOther;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_entre_utilisateur);
        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Utilisateur non connecté, retour à l'activité de connexion
            finish();
            return;
        }
        extraDonnée();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mMessageListView = findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        ArrayList<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages, idOther, mFirebaseUser.getUid());
        mMessageListView.setAdapter(mMessageAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mMessageEditText.getText().toString())) {
                    String messageText = mMessageEditText.getText().toString();
                    String messageSender = idOther;
                    String messageReceveur = mFirebaseUser.getUid();
                    String messageId = UUID.randomUUID().toString(); // Choisissez un ID personnalisé pour chaque message ici
                    String dateMessage =  DateFormat.getDateTimeInstance().format(new Date());
                    Message message = new Message(messageText, messageSender,messageReceveur,dateMessage);
                    MyFirebaseDatabase database = new MyFirebaseDatabase();
                    database.addMessage(messageId, message);
                    mMessageEditText.setText("");
                } else {
                    Toast.makeText(MessageEntreUtilisateur.this, "Veuillez entrer un message", Toast.LENGTH_SHORT).show();
                }
            }
        });



        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Récupération des messages depuis la base de données Firebase et ajout à l'adaptateur de message
                String text = dataSnapshot.child("text").getValue(String.class);
                String sender = dataSnapshot.child("sender").getValue(String.class);
                String receveur = dataSnapshot.child("receveur").getValue(String.class);
                String date = dataSnapshot.child("date").getValue(String.class);
                if ((sender.equals(idOther) && receveur.equals(mFirebaseUser.getUid())) || (sender.equals(mFirebaseUser.getUid()) && receveur.equals(idOther))) {
                    Message message = new Message(text, sender,receveur, date);
                    mMessageAdapter.add(message);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Suppression de l'écouteur de base de données Firebase lors de la fermeture de l'activité
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
        }
    }
    private void extraDonnée() {
        Bundle extra = getIntent().getExtras(); // Récupère l'extra envoyé par MainActivity
        if (extra != null)
            idOther = extra.getString("id");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérez les clics sur les éléments de la barre d'action ici
        switch (item.getItemId()) {
            case android.R.id.home:
                // Appuyez sur le bouton de retour : fermez cette activité
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
