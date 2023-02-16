package com.example.instantlike.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {

    private List<String> comList;
    private List<String> iconUtilisateurCom;
    private List<String> nomUtilisateurCom;
    private Context context;

    public ComAdapter(List<String> comList, Context context, List<String> iconUtilisateurCom, List<String> nomUtilisateurCom) {
        this.comList = comList;
        this.context = context;
        this.iconUtilisateurCom = iconUtilisateurCom;
        this.nomUtilisateurCom = nomUtilisateurCom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcommentaire, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(comList.get(position), iconUtilisateurCom.get(position), nomUtilisateurCom.get(position));
    }

    @Override
    public int getItemCount() {
        return iconUtilisateurCom.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView com;
        private TextView nom;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            com = itemView.findViewById(R.id.unCommentaire);
            nom = itemView.findViewById(R.id.nomUtilisateurCom);
            icon = itemView.findViewById(R.id.iconCom);
        }

        public void bind(String comText, String iconUrl, String nomText) {
            com.setText(comText);
            nom.setText(nomText);
            Glide.with(context).load(iconUrl).into(icon);
        }
    }
}
