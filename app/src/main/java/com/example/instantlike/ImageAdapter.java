package com.example.instantlike;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageList,titre, descriptions;
    private Context context;
    private TextView titreView, descriptionsView;

    //initialise les variables quand on appelle la clase avec les paramétres données
    public ImageAdapter(ArrayList<String> imageList, Context context,ArrayList<String> titre, ArrayList<String> descriptions) {
        this.imageList = imageList;
        this.context = context;
        this.titre = titre;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder,int position) {
        //créations du recycleur avec tout les image
        Picasso.get().load(imageList.get(position)).into(holder.imageView);
        titreView.setText(titre.get(position));
        descriptionsView.setText(descriptions.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

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