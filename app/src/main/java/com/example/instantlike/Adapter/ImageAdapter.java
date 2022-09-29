package com.example.instantlike.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.InfoPoste;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageList, titre, descriptions;
    private Context context;
    private TextView titreView, descriptionsView;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public ImageAdapter(ArrayList<String> imageList, Context context, ArrayList<String> titre, ArrayList<String> descriptions) {
        this.imageList = imageList;
        this.context = context;
        this.titre = titre;
        this.descriptions = descriptions;
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
        //créations du recycleur avec tout les image
        Picasso.get().load(imageList.get(position)).into(holder.imageView);
        titreView.setText(titre.get(position));
        descriptionsView.setText(descriptions.get(position));
    }

    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * méthode pour définir tout les éléments de la view que nous allons utiliser
     * est ici mettre une évenement pour chaque clique sur un item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //récupérations de l'image
            imageView = itemView.findViewById(R.id.imageView);
            titreView = itemView.findViewById(R.id.titre);
            descriptionsView = itemView.findViewById(R.id.descriptions);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InfoPoste.class);
                    intent.putExtra("image", imageList.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

        }
    }
}