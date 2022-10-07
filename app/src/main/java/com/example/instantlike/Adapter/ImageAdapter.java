package com.example.instantlike.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instantlike.HomePage;
import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageListUri, imageListName, titre, descriptions, imageName, iconList, nomUster;
    private Context context;
    private TextView titreView, descriptionsView, nomUtilisateur;
    private ImageButton Like, partage;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public ImageAdapter(ArrayList<String> imageListUri, ArrayList<String> imageListName, Context context, ArrayList<String> titre, ArrayList<String> descriptions, ArrayList<String> imageName, ArrayList<String> iconList, ArrayList<String> nomUster) {
        this.imageListUri = imageListUri;
        this.imageListName = imageListName;
        this.context = context;
        this.titre = titre;
        this.descriptions = descriptions;
        this.imageName = imageName;
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
        Picasso.get().load(iconList.get(position)).into(holder.Icone);
        //créations du recycleur avec tout les image
        Picasso.get().load(imageListUri.get(position)).into(holder.imageView);
        String testeNomImage = imageListName.get(position);
        int i;
        for (i = 0; i < imageName.size(); i++) {
            if (imageName.get(i).equals(testeNomImage))
                break;
        }
        titreView.setText(titre.get(i));
        descriptionsView.setText(descriptions.get(i));
        nomUtilisateur.setText(nomUster.get(position));
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

        ImageView imageView, Icone;

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InfoPoste.class);
                    intent.putExtra("image", imageListUri.get(getAdapterPosition()));
                    intent.putExtra("name", imageListName.get(getAdapterPosition()));
                    intent.putExtra("retour", false);
                    context.startActivity(intent);
                }
            });
            Like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "ces liker !", Toast.LENGTH_SHORT).show();
                }
            });
            partage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "ces partager !", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}