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

import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {

    private ArrayList<String> iconListUri, date, imageListName;
    private ArrayList<Integer> like;
    private Context context;
    private TextView datePoste, likePoste;


    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public PublicationAdapter(ArrayList<String> iconListUri, Context context, ArrayList<String> date, ArrayList<Integer> like, ArrayList<String> imageListName) {
        this.iconListUri = iconListUri;
        this.context = context;
        this.date = date;
        this.like = like;
        this.imageListName = imageListName;
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
    public PublicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itempublication, parent, false);
        return new PublicationAdapter.ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull PublicationAdapter.ViewHolder holder, int position) {
        trie(holder, position);
    }

    private void trie(@NonNull PublicationAdapter.ViewHolder holder, int position) {
        for (int j = 0; j < imageListName.size(); j++) {
            if (iconListUri.get(position).contains(imageListName.get(j))) {
                Picasso.get().load(iconListUri.get(position)).into(holder.iconView);
                datePoste.setText(date.get(j));
                likePoste.setText("Like : " + like.get(j));
                break;
            }
        }
    }

    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return iconListUri.size();
    }

    /**
     * méthode pour définir tout les éléments de la view que nous allons utiliser
     * est ici mettre une évenement pour chaque clique sur un item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconUtilisateurPublications);
            datePoste = itemView.findViewById(R.id.PublicationsPosteDate);
            likePoste = itemView.findViewById(R.id.LikePublication);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InfoPoste.class);
                    intent.putExtra("image", iconListUri.get(getAdapterPosition()));
                    intent.putExtra("name", imageListName.get(getAdapterPosition()));
                    intent.putExtra("retour", true);
                    context.startActivity(intent);
                }
            });
        }
    }
}
