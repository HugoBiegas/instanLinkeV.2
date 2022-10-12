package com.example.instantlike;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class ToolbarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
    }

}