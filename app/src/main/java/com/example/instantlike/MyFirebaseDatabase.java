package com.example.instantlike;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyFirebaseDatabase {
    private DatabaseReference mMessagesDatabaseReference;

    public MyFirebaseDatabase() {
        mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages");
    }

    public void addMessage(String messageId, Message message) {
        mMessagesDatabaseReference.child(messageId).setValue(message);
    }
}


