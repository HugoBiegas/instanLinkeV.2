package com.example.instantlike.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> implements View.OnClickListener {

    private final ArrayList<String> imageListUriStorage;
    private final ArrayList<String> titre;
    private final ArrayList<String> imageListNameStorage;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> iconList;
    private final ArrayList<String> nomUster;
    private final Context context;
    private ArrayList<Integer> cpt = new ArrayList<Integer>();
    private boolean premierPassage = true;


    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     * Constructeur
     */
    public ImageAdapter(ArrayList<String> imageListUri, ArrayList<String> imageListNameStorage, Context context, ArrayList<String> titre, ArrayList<String> descriptions, ArrayList<String> iconList, ArrayList<String> nomUster) {
        this.imageListUriStorage = imageListUri;
        this.imageListNameStorage = imageListNameStorage;
        this.context = context;
        this.titre = titre;
        this.descriptions = descriptions;
        this.iconList = iconList;
        this.nomUster = nomUster;
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
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(imageListUriStorage.get(holder.getAdapterPosition())).into(holder.imageView);
        Picasso.get().load(iconList.get(holder.getAdapterPosition())).into(holder.Icone);
        holder.titreView.setText(titre.get(holder.getAdapterPosition()));
        holder.descriptionsView.setText(descriptions.get(holder.getAdapterPosition()));
        holder.nomUtilisateur.setText(nomUster.get(holder.getAdapterPosition()));
        holder.Like.setImageResource(R.drawable.like);
        iniLike(holder, position);
        holder.partage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = imageListUriStorage.get(holder.getAdapterPosition());
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(shareIntent, titre.get(holder.getAdapterPosition())));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoPoste.class);
                intent.putExtra("image", imageListUriStorage.get(holder.getAdapterPosition()));
                intent.putExtra("name", imageListNameStorage.get(holder.getAdapterPosition()));
                intent.putExtra("retour", false);
                context.startActivity(intent);
            }
        });


        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tout les teste et mise a jour du recycler
                Drawable imagebtn = holder.Like.getDrawable();
                Drawable newDrawable = ContextCompat.getDrawable(context, R.drawable.like);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser userid = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference itemRef = db.collection("images").document(imageListNameStorage.get(holder.getAdapterPosition()));
                int newImageId;
                String newTexte;

                    if (imagebtn.getConstantState().equals(newDrawable.getConstantState())) {
                        cpt.set(holder.getAdapterPosition(), cpt.get(holder.getAdapterPosition()) + 1);
                        newTexte = cpt.get(holder.getAdapterPosition()) + " Likes";
                        updateLike(itemRef, userid);
                        newImageId = R.drawable.liker;
                    } else {
                        Toast.makeText(context, "" + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        cpt.set(holder.getAdapterPosition(), cpt.get(holder.getAdapterPosition()) - 1);
                        newTexte = cpt.get(holder.getAdapterPosition()) + " Likes";
                        deleteLike(itemRef, userid);
                        newImageId = R.drawable.like;

                    }
                holder.Like.setImageResource(newImageId);
                holder.likeNbActu.setText(newTexte);
            }
        });
    }

    private void deleteLike(DocumentReference itemRef, FirebaseUser userid) {
        itemRef.update("Like", FieldValue.arrayRemove(userid.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Update", "items array successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Update", "Error updating items array", e);
            }
        });

    }

    private void updateLike(DocumentReference itemRef, FirebaseUser userid) {
        itemRef.update("Like", FieldValue.arrayUnion(userid.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Update", "items array successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Update", "Error updating items array", e);
            }
        });
    }


    private void iniLike(MyViewHolder holder, int positions) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userid = mAuth.getCurrentUser();
        DocumentReference itemRef = db.collection("images").document(imageListNameStorage.get(holder.getAdapterPosition()));
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> subitems = (ArrayList<String>) document.get("Like");
                        for (int i = 0; i < subitems.size(); i++) {
                            if (subitems.get(i).equals(userid.getUid()))
                                holder.Like.setImageResource(R.drawable.liker);
                        }
                        cpt.add(subitems.size());
                        holder.likeNbActu.setText(subitems.size() + " Likes");
                    } else {
                        Log.d("Error", "No such document");
                    }
                } else {
                    Log.d("Error", "get failed with ", task.getException());
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
    public int getItemCount() {
        return iconList.size();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(context, "cc", Toast.LENGTH_SHORT).show();
    }

    /**
     * méthode pour définir tout les éléments de la view que nous allons utiliser
     * est ici mettre une évenement pour chaque clique sur un item
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titreView, descriptionsView, nomUtilisateur, likeNbActu;
        public ImageButton Like, partage;
        public ImageView imageView, Icone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //récupérations de l'image
            imageView = itemView.findViewById(R.id.imageViewpPoste);
            nomUtilisateur = itemView.findViewById(R.id.NomUtilisateurPost);
            Icone = itemView.findViewById(R.id.iconUtilisateurPost);
            titreView = itemView.findViewById(R.id.titre);
            descriptionsView = itemView.findViewById(R.id.descriptions);
            Like = itemView.findViewById(R.id.LikeBTNPost);
            partage = itemView.findViewById(R.id.partagePost);
            likeNbActu = itemView.findViewById(R.id.nbLike);
        }
    }
}
