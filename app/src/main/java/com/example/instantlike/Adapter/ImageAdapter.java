package com.example.instantlike.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final ArrayList<String> imageListUriStorage;
    private final ArrayList<String> imageListNameStorage;
    private final ArrayList<String> titre;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> imageNameFirebase;
    private final ArrayList<String> iconList;
    private final ArrayList<String> nomUster;
    private final Context context;
    private TextView titreView, descriptionsView, nomUtilisateur, likeNbActu;
    private ImageButton Like, partage;
    private Button follow;
    private ImageView imageView, Icone;


    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     * Constructeur
     */
    public ImageAdapter(ArrayList<String> imageListUri, ArrayList<String> imageListName, Context context, ArrayList<String> titre, ArrayList<String> descriptions, ArrayList<String> imageName, ArrayList<String> iconList, ArrayList<String> nomUster) {
        this.imageListUriStorage = imageListUri;
        this.imageListNameStorage = imageListName;
        this.imageNameFirebase = imageName;
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
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Picasso.get().load(iconList.get(position)).into(Icone);
        titreView.setText(titre.get(position));
        descriptionsView.setText(descriptions.get(position));
        nomUtilisateur.setText(nomUster.get(position));
        String NomImage = imageNameFirebase.get(position);
        int i;
        for (i = 0; i < imageListNameStorage.size(); i++) {
            if (NomImage.equals(imageListNameStorage.get(i))) {
                Picasso.get().load(imageListUriStorage.get(i)).into(imageView);
                break;
            }
        }
        iniLike(position,i);
        //iniFollow(position);
    }

/*    private void iniFollow(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(imageListName.get(position))) {
                            //date du poste
                            String userSuivi = document.getData().toString();
                            userSuivi = userSuivi.substring(userSuivi.indexOf("UserPoste=") + 10);
                            if (userSuivi.indexOf(",") == -1)
                                userSuivi = userSuivi.substring(0, userSuivi.indexOf("}"));
                            else userSuivi = userSuivi.substring(0, userSuivi.indexOf(","));

                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser userFollow = mAuth.getCurrentUser();
                            DocumentReference docRef = fStore.collection("followSuivi").document(userFollow.getUid() + ":" + userSuivi);

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists())
                                                follow.setText("UnFollow");
                                        }
                                    }
                                });
                            break;
                        }
                    }
                } else {
                    Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }*/

    private void iniLike(int position, int i) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int cpt = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().contains(imageListNameStorage.get(i)))
                            cpt++;
                    }
                    likeNbActu.setText(cpt + " Likes");
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

    /**
     * méthode pour définir tout les éléments de la view que nous allons utiliser
     * est ici mettre une évenement pour chaque clique sur un item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //récupérations de l'image
            imageView = itemView.findViewById(R.id.imageViewpPoste);
            nomUtilisateur = itemView.findViewById(R.id.NomUtilisateurPost);
            Icone = itemView.findViewById(R.id.iconUtilisateurPost);
            titreView = itemView.findViewById(R.id.titre);
            descriptionsView = itemView.findViewById(R.id.descriptions);
            Like = itemView.findViewById(R.id.LikeBTNPost);
            partage = itemView.findViewById(R.id.partagePost);
            follow = itemView.findViewById(R.id.btnPostFollow);
            likeNbActu = itemView.findViewById(R.id.nbLike);

            /*follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(imageListName.get(getAdapterPosition()))) {
                                        //date du poste
                                        String userSuivi = document.getData().toString();
                                        userSuivi = userSuivi.substring(userSuivi.indexOf("UserPoste=") + 10);
                                        if (userSuivi.indexOf(",") == -1)
                                            userSuivi = userSuivi.substring(0, userSuivi.indexOf("}"));
                                        else
                                            userSuivi = userSuivi.substring(0, userSuivi.indexOf(","));
                                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        FirebaseUser userFollow = mAuth.getCurrentUser();
                                        if (!(userFollow.getUid().equals(userSuivi))) {
                                            DocumentReference docRef = fStore.collection("followSuivi").document(userFollow.getUid() + ":" + userSuivi);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Toast.makeText(context, " ces unfollow !", Toast.LENGTH_SHORT).show();
                                                            follow.setText("Follow");
                                                            docRef.delete();
                                                        } else {
                                                            Toast.makeText(context, " ces follow !", Toast.LENGTH_SHORT).show();
                                                            follow.setText("UnFollow");
                                                            Map<String, Object> donnée = new HashMap<>();
                                                            // Update and delete the "capital" field in the document
                                                            docRef.set(donnée).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("TAG", "onSuccess: Les données son créer");
                                                                }
                                                            });
                                                        }
                                                        for (int i = 0; i < getItemCount(); i++) {
                                                            notifyItemChanged(i);
                                                        }
                                                    }
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });*/

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InfoPoste.class);
                    String NomImage = imageNameFirebase.get(getAdapterPosition());
                    int i;
                    for (i = 0; i < imageListNameStorage.size(); i++) {
                        if (NomImage.equals(imageListNameStorage.get(i))) {
                            break;
                        }
                    }
                    intent.putExtra("image", imageListUriStorage.get(i));
                    intent.putExtra("name", imageListNameStorage.get(i));
                    intent.putExtra("retour", false);
                    context.startActivity(intent);
                }
            });

            Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ajouLike();
                }
            });

            partage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareBody = imageListUriStorage.get(getAdapterPosition());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(shareIntent, titre.get(getAdapterPosition())));
                }
            });

        }

        private void ajouLike() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser userid = mAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String NomImage = imageNameFirebase.get(getAdapterPosition());
                        int i;
                        for (i = 0; i < imageListNameStorage.size(); i++) {
                            if (NomImage.equals(imageListNameStorage.get(i))) {
                                break;
                            }
                        }

                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        DocumentReference docRef = fStore.collection("like").document(imageListNameStorage.get(i) + ":" + userid);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("nbLike", 1);
                        docRef.update(updates);
                        notifyItemChanged(i);
                    } else {
                        Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}