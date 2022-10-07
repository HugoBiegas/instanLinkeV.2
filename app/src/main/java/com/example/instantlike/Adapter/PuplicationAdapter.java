package com.example.instantlike.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instantlike.R;

import java.util.ArrayList;

public class PuplicationAdapter extends RecyclerView.Adapter<PuplicationAdapter.ViewHolder> {

    private ArrayList<String> iconListUri, date, like;
    private Context context;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public PuplicationAdapter(ArrayList<String> iconListUri, Context context, ArrayList<String> date, ArrayList<String> like) {
        this.iconListUri = iconListUri;
        this.context = context;
        this.date = date;
        this.like = like;
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
    public PuplicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new PuplicationAdapter.ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull PuplicationAdapter.ViewHolder holder, int position) {
        //Picasso.get().load(iconListUri.get(position)).into(holder.icon);

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

        private ImageView icon;
        private TextView Date, Like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iconUtilisateurPublications);
            Date = itemView.findViewById(R.id.PublicationsPosteDate);
            Like = itemView.findViewById(R.id.LikePublication);
        }
    }
}
