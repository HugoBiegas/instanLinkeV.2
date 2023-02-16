package com.example.instantlike.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantlike.R;

import java.util.ArrayList;

public class MessageUtilisateur extends RecyclerView.Adapter<MessageUtilisateur.ViewHolder> {

    private ArrayList<String> messageUtilisateur;
    private ArrayList<String> dateMessage;
    private ArrayList<Boolean> droitOuGauche;
    private Context context;

    /**
     * initialise les variables quand on appelle la clase avec les paramétres données
     */
    public MessageUtilisateur(Context context, ArrayList<String> messageUtilisateur, ArrayList<String> dateMessage, ArrayList<Boolean> droitOuGauche) {
        this.context = context;
        this.messageUtilisateur = messageUtilisateur;
        this.droitOuGauche = droitOuGauche;
        this.dateMessage = dateMessage;
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
    public MessageUtilisateur.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mp, parent, false);
        return new MessageUtilisateur.ViewHolder(view);
    }

    /**
     * méthode permettent d'intéragir avec l'item de cette occurrence du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MessageUtilisateur.ViewHolder holder, int position) {
        if (droitOuGauche.get(position)) {
            holder.texteDroite.setText(messageUtilisateur.get(position));
            holder.texteGauche.setVisibility(View.GONE);
        } else {
            holder.texteGauche.setText(messageUtilisateur.get(position));
            holder.texteDroite.setVisibility(View.GONE);
        }
        holder.date.setText(dateMessage.get(position));
    }

    /**
     * récupérations de la dimentions du recycleur
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return messageUtilisateur.size();
    }

    /**
     * méthode pour définir tous les éléments de la vue que nous allons utiliser
     * et potentiellement mettre des événements pour chaque item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView texteDroite;
        TextView texteGauche;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            texteDroite = itemView.findViewById(R.id.texteDroite);
            texteGauche = itemView.findViewById(R.id.texteGauche);
            date = itemView.findViewById(R.id.date);
        }
    }
}