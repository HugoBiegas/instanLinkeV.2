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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {

    private ArrayList<String> comList, iconUtilisateurCom, nomUtilisateurCom;
    private Context context;
    private TextView com, nom;
    private ImageView icon;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public ComAdapter(ArrayList<String> comList, Context context, ArrayList<String> iconUtilisateurCom, ArrayList<String> nomUtilisateurCom) {
        this.comList = comList;
        this.context = context;
        this.iconUtilisateurCom = iconUtilisateurCom;
        this.nomUtilisateurCom = nomUtilisateurCom;
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
        nom.setText(nomUtilisateurCom.get(position));
        Picasso.get().load(iconUtilisateurCom.get(position)).into(icon);
    }


    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return iconUtilisateurCom.size();
    }

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
