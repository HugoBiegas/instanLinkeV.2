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

import com.example.instantlike.MessageEntreUtilisateur;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MPAdapter extends RecyclerView.Adapter<MPAdapter.ViewHolder> {

    private ArrayList<String> nomUtilisateur, iconUtilisateur, idUtilisateurMp;
    private Context context;

    public MPAdapter(Context context, ArrayList<String> iconUtilisateurCom, ArrayList<String> nomUtilisateurCom, ArrayList<String> idUtilisateurMp) {
        this.context = context;
        this.iconUtilisateur = iconUtilisateurCom;
        this.nomUtilisateur = nomUtilisateurCom;
        this.idUtilisateurMp = idUtilisateurMp;
    }

    @NonNull
    @Override
    public MPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilisateur_mp, parent, false);
        return new MPAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MPAdapter.ViewHolder holder, int position) {
        Picasso.get().load(iconUtilisateur.get(position)).into(holder.icon);
        holder.nom.setText(nomUtilisateur.get(position));
    }

    @Override
    public int getItemCount() {
        return nomUtilisateur.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView nom;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iconMp);
            nom = itemView.findViewById(R.id.nomMpUtilisateur);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MessageEntreUtilisateur.class);
                    intent.putExtra("nom", nomUtilisateur.get(getAdapterPosition()));
                    intent.putExtra("icon", iconUtilisateur.get(getAdapterPosition()));
                    intent.putExtra("id", idUtilisateurMp.get(getAdapterPosition()));
                    intent.putExtra("nbMessage", idUtilisateurMp.size());
                    context.startActivity(intent);
                }
            });
        }
    }
}
