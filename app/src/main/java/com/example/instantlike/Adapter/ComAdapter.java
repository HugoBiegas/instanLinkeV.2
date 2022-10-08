package com.example.instantlike.Adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {

    private ArrayList<String> comList, idUtilisateurCom,nomUtil, iconUtil;
    private Context context;
    private TextView com, nom;
    private ImageView icon;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public ComAdapter(ArrayList<String> comList, Context context, ArrayList<String> idUtilisateurCom) {
        this.comList = comList;
        this.context = context;
        this.idUtilisateurCom = idUtilisateurCom;
    }

    /**
     * méthode permettent de créer le recycleur dans le view avec l'item créer
     * l'item étent un layout qui défini le style de chaque Item du recycleur
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcommentaire, parent, false);
        return new ComAdapter.ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ComAdapter.ViewHolder holder, int position) {
        com.setText(comList.get(position));
        ininom(position);
        iniIcon(position);

    }

    private void iniIcon(int position) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Icone");
        //on vas chercher les images dans la BD
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileRef : listResult.getItems()) {
                    String c = fileRef.getName();
                    if (c.contains(idUtilisateurCom.get(position))) {
                        //actualisations pour avoir un chiffre différent a chaque foi
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(context /* context */).load(uri.toString()).into(icon);                            }
                        });
                        break;
                    }
                }

                // on fait une boucle pour stocker les images une par une
            }
        });
    }

    private void ininom(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(idUtilisateurCom.get(position))) {
                                    //date du poste
                                    String userName = document.getData().toString();
                                    userName = userName.substring(userName.indexOf("username=") + 9);
                                    if (userName.indexOf(",") == -1)
                                        userName = userName.substring(0, userName.indexOf("}"));
                                    else
                                        userName = userName.substring(0, userName.indexOf(","));
                                    Toast.makeText(context, "je passe", Toast.LENGTH_SHORT).show();
                                    nom.setText(userName);
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {return idUtilisateurCom.size();}

    /**
     * méthode pour définir tout les élément de la view que nous allons utiliser
     * est potentiellement mettre des évenement pour chaque item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View Itemview) {
            super(Itemview);
            com = Itemview.findViewById(R.id.unCommentaire);
            nom = Itemview.findViewById(R.id.nomUtilisateurCom);
            icon = Itemview.findViewById(R.id.iconCom);
        }
    }
}
