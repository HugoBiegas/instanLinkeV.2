package com.example.instantlike;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

    private String text;
    private String sender;
    private String receveur;
    private String date;

    public Message() {}

    public Message(String text, String sender,String receveur, String date) {
        this.text = text;
        this.sender = sender;
        this.date = date;
        this.receveur = receveur;

    }

    public String getreceveur() {
        return receveur;
    }

    public void setreceveur(String text) {
        this.receveur = receveur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String text) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
