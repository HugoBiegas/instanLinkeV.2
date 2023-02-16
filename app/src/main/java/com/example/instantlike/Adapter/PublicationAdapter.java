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

import com.bumptech.glide.Glide;
import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {
    private List<String> iconListUri;
    private List<String> date;
    private List<Integer> like;
    private List<String> imageListName;
    private Context context;

    public PublicationAdapter(List<String> iconListUri, Context context, List<String> date, List<Integer> like, List<String> imageListName) {
        this.iconListUri = iconListUri;
        this.context = context;
        this.date = date;
        this.like = like;
        this.imageListName = imageListName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itempublication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(iconListUri.get(position), date.get(position), like.get(position), imageListName.get(position));
    }

    @Override
    public int getItemCount() {
        return iconListUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView datePoste;
        private TextView likePoste;

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

        public void bind(String iconUri, String dateStr, int likeCount, String imageName) {
            Glide.with(context).load(iconUri).into(iconView);
            datePoste.setText(dateStr);
            likePoste.setText("Like : " + likeCount);
        }
    }
}
