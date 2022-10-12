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

import com.example.instantlike.InteractionUtilisateur.MessageEntreUtilisateur;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MPAdapter extends RecyclerView.Adapter<MPAdapter.ViewHolder> {

    private ArrayList<String> nomUtiliseur, iconUtilisateur, idUtilisateurMp;
    private Context context;
    private TextView nom;
    private ImageView icon;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public MPAdapter(Context context, ArrayList<String> iconUtilisateurCom, ArrayList<String> nomUtilisateurCom, ArrayList<String> idUtilisateurMp) {
        this.context = context;
        this.iconUtilisateur = iconUtilisateurCom;
        this.nomUtiliseur = nomUtilisateurCom;
        this.idUtilisateurMp = idUtilisateurMp;
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
    public MPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilisateur_mp, parent, false);
        return new MPAdapter.ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MPAdapter.ViewHolder holder, int position) {
        Picasso.get().load(iconUtilisateur.get(position)).into(icon);
        nom.setText(nomUtiliseur.get(position));
    }


    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return iconUtilisateur.size();
    }

    /**
     * méthode pour définir tout les élément de la view que nous allons utiliser
     * est potentiellement mettre des évenement pour chaque item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View Itemview) {
            super(Itemview);
            icon = Itemview.findViewById(R.id.iconMp);
            nom = Itemview.findViewById(R.id.nomMpUtilisateur);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MessageEntreUtilisateur.class);
                    intent.putExtra("nom", nomUtiliseur.get(getAdapterPosition()));
                    intent.putExtra("icon", iconUtilisateur.get(getAdapterPosition()));
                    intent.putExtra("id", idUtilisateurMp.get(getAdapterPosition()));
                    intent.putExtra("nbMessage", idUtilisateurMp.size());
                    context.startActivity(intent);
                }
            });
        }
    }
}
