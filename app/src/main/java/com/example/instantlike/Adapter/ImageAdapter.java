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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public ImageAdapter(ArrayList<String> imageListUri,ArrayList<String> imageListNameStorage, Context context, ArrayList<String> titre, ArrayList<String> descriptions, ArrayList<String> iconList, ArrayList<String> nomUster) {
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
        iniLike(holder,position);
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
                Drawable newDrawableliker = ContextCompat.getDrawable(context, R.drawable.liker);
                int newImageId;
                String newTexte;
                if (premierPassage && imagebtn.getConstantState().equals(newDrawableliker.getConstantState())){
                    newImageId = R.drawable.like;
                    cpt.set(holder.getAdapterPosition(), (cpt.get(holder.getAdapterPosition())-1));
                    newTexte = cpt.get(holder.getAdapterPosition())+" Likes";
                    premierPassage = false;
                }else{
                    newImageId = (imagebtn.getConstantState().equals(newDrawable.getConstantState())) ? R.drawable.liker : R.drawable.like;
                    if (imagebtn.getConstantState().equals(newDrawable.getConstantState())){
                        cpt.set(holder.getAdapterPosition(), cpt.get(holder.getAdapterPosition())+1);
                        newTexte =cpt.get(holder.getAdapterPosition())+" Likes";
                    }else{
                        cpt.set(holder.getAdapterPosition(), cpt.get(holder.getAdapterPosition())-1);
                        newTexte = cpt.get(holder.getAdapterPosition())+" Likes";
                    }
                }
                holder.Like.setImageResource(newImageId);
                holder.likeNbActu.setText(newTexte);
                // mise a jour de la bd avec les like actuel
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser userid = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = fStore.collection("like").document(userid.getUid() + ":"+imageListNameStorage.get(holder.getAdapterPosition()));
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        Map<String, Object> donnée = new HashMap<>();
                                        donnée.put("nbLike", cpt.get(holder.getAdapterPosition()));
                                        documentReference.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "onSuccess: Les données son créer");
                                            }
                                        });
                                    } else {
                                        documentReference.delete();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void iniLike(MyViewHolder holder, int positions) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userid = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int cpt2=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().contains(":"+imageListNameStorage.get(positions)))
                            cpt2++;
                        if(document.getId().equals(userid.getUid()+":"+imageListNameStorage.get(positions)))
                            holder.Like.setImageResource(R.drawable.liker);
                    }
                    cpt.add(cpt2);
                    holder.likeNbActu.setText(cpt.get(holder.getAdapterPosition()) + " Likes");
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
