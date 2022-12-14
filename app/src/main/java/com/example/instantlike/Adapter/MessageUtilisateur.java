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
    private TextView nomD, date, nomG;


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
     * méthode permettent d'intéragire avec l'item de cette ocurent du recycleur
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MessageUtilisateur.ViewHolder holder, int position) {
        if (droitOuGauche.get(position)) {
            nomD.setText(messageUtilisateur.get(position));
        } else {
            nomG.setText(messageUtilisateur.get(position));
        }
        date.setText(dateMessage.get(position));
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
     * méthode pour définir tout les élément de la view que nous allons utiliser
     * est potentiellement mettre des évenement pour chaque item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View Itemview) {
            super(Itemview);
            nomD = Itemview.findViewById(R.id.texteDroite);
            date = Itemview.findViewById(R.id.date);
            nomG = Itemview.findViewById(R.id.texteGauche);
        }
    }
}
