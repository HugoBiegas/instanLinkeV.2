package com.example.instantlike.InteractionUtilisateur;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Adapter.MPAdapter;
import com.example.instantlike.Connection.Login;
import com.example.instantlike.HomePage;
import com.example.instantlike.Poste.CreationPoste;
import com.example.instantlike.Profil.ProfilInfo;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UtilisateurMP extends AppCompatActivity {

    private static final int RETOUR_PHOTO = 1;
    private static final int HOME_BTN_ID = R.id.HomeBTNMpUtilisateur;
    private static final int MESSAGE_BTN_ID = R.id.MessageBTNMpUtilisateur;
    private static final int INFO_PROFIL_BTN_ID = R.id.InfoPorofilBTNMpUtilisateur;
    private ImageButton homeBtn, messageBtn, profilInfoPosteBtn;
    private androidx.appcompat.widget.Toolbar toolbar;
    private String photoPath;
    private Uri photoUir;
    private ArrayList<String> nomUtilisateurMP = new ArrayList<>();
    private ArrayList<String> idUtilisateurMp = new ArrayList<>();
    private ArrayList<String> iconUtilisateurMP = new ArrayList<>();
    private ArrayList<String> iconUtilisateurMPToken = new ArrayList<>();
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    public void onStart() {
        super.onStart();
        checkUser();
    }

    private void checkUser() {
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
        setContentView(R.layout.activity_utilisateur_mp);
        initViews();
        setListeners();
        displayUserList();
    }

    private void initViews() {
        homeBtn = findViewById(HOME_BTN_ID);
        messageBtn = findViewById(MESSAGE_BTN_ID);
        profilInfoPosteBtn = findViewById(INFO_PROFIL_BTN_ID);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBarMPUser);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        homeBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        });

        messageBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), UtilisateurMP.class));
            finish();
        });

        profilInfoPosteBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProfilInfo.class));
            finish();
        });

        ImageButton photoBtn = findViewById(R.id.action_photo);
        photoBtn.setOnClickListener(view -> {
            openCamera();
        });

        ImageButton posteBtn = findViewById(R.id.action_poste);
        posteBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), CreationPoste.class));
            finish();
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                photoUir = FileProvider.getUriForFile(UtilisateurMP.this, UtilisateurMP.this.getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUir);
                startActivityForResult(intent, RETOUR_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RETOUR_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), CreationPoste.class);
            intent.putExtra("image", photoPath);
            intent.putExtra("uri", photoUir);
            startActivity(intent);
            finish();
        }
    }
    private void displayUserList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!document.getId().equals(currentUser.getUid())) {
                        String userName = document.getString("username");
                        nomUtilisateurMP.add(userName);
                        idUtilisateurMp.add(document.getId());
                    }
                }
                getIcons();
            }
        });
    }

    private void getIcons() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        storageReference.listAll().addOnSuccessListener(listResult -> {
            for (int i = 0; i < idUtilisateurMp.size(); i++) {
                for (StorageReference fileRef : listResult.getItems()) {
                    String fileName = fileRef.getName();
                    if (fileName.contains(idUtilisateurMp.get(i))) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            iconUtilisateurMP.add(uri.toString());
                            String name = uri.getLastPathSegment();
                            name = name.substring(name.indexOf("/") + 1);
                            iconUtilisateurMPToken.add(name);
                        }).addOnCompleteListener(task -> {
                            if (task.isSuccessful() && iconUtilisateurMP.size() == idUtilisateurMp.size()) {
                                sortIcons();
                                progressBar.setVisibility(View.GONE);
                                RecyclerView recyclerView = findViewById(R.id.recyclerViewMPutilisateur);
                                recyclerView.setLayoutManager(new LinearLayoutManager(UtilisateurMP.this));
                                MPAdapter adapter = new MPAdapter(UtilisateurMP.this, iconUtilisateurMP, nomUtilisateurMP, idUtilisateurMp);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                        break;
                    }
                }
            }
        });
    }

    private void sortIcons() {
        ArrayList<String> tempIcons = new ArrayList<>();
        for (int i = 0; i < idUtilisateurMp.size(); i++) {
            String id = idUtilisateurMp.get(i);
            for (int j = 0; j < iconUtilisateurMPToken.size(); j++) {
                if (id.equals(iconUtilisateurMPToken.get(j))) {
                    tempIcons.add(iconUtilisateurMP.get(j));
                    break;
                }
            }
        }
        iconUtilisateurMP.clear();
        iconUtilisateurMP.addAll(tempIcons);
    }

}
