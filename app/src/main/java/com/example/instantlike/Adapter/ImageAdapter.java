package com.example.instantlike.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instantlike.Image.ImageData;
import com.example.instantlike.Poste.InfoPoste;
import com.example.instantlike.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> implements View.OnClickListener {

    private List<ImageData> imageDataList;
    private Context context;

    public ImageAdapter(List<ImageData> imageDataList, Context context) {
        this.imageDataList = imageDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageData imageData = imageDataList.get(position);

        Glide.with(context).load(imageData.getImageUri()).into(holder.imageView);
        Glide.with(context).load(imageData.getIconUri()).into(holder.Icone);

        holder.titreView.setText(imageData.getTitre());
        holder.descriptionsView.setText(imageData.getDescription());
        holder.nomUtilisateur.setText(imageData.getNomUtilisateur());

        holder.Like.setImageResource(imageData.isLiked() ? R.drawable.liker : R.drawable.like);
        holder.likeNbActu.setText(imageData.getLikeCount() + " Likes");

        holder.partage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = imageData.getImageUri();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(shareIntent, imageData.getTitre()));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoPoste.class);
                intent.putExtra("image", imageData.getImageUri());
                intent.putExtra("name", imageData.getImageName());
                intent.putExtra("retour", false);
                context.startActivity(intent);
            }
        });

        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean liked = imageData.isLiked();
                updateLike(imageData, !liked);
            }
        });
    }

    private void updateLike(ImageData imageData, boolean like) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference itemRef = db.collection("images").document(imageData.getImageName());

        if (like) {
            itemRef.update("Like", FieldValue.arrayUnion(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageData.setLiked(true);
                    imageData.setLikeCount(imageData.getLikeCount() + 1);
                    notifyItemChanged(imageDataList.indexOf(imageData));
                    Log.d("Update", "items array successfully updated!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Update", "Error updating items array", e);
                }
            });
        } else {
            itemRef.update("Like", FieldValue.arrayRemove(user.getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageData.setLiked(false);
                    imageData.setLikeCount(imageData.getLikeCount() - 1);
                    notifyItemChanged(imageDataList.indexOf(imageData));
                    Log.d("Update", "items array successfully updated!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Update", "Error updating items array", e);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(context, "cc", Toast.LENGTH_SHORT).show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titreView, descriptionsView, nomUtilisateur, likeNbActu;
        public ImageButton Like, partage;
        public ImageView imageView, Icone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewpPoste);
            nomUtilisateur = itemView.findViewById(R.id.NomUtilisateurPost);
            Icone = itemView.findViewById(R.id.iconUtilisateurPost);
            titreView = itemView.findViewById(R.id.titre);
            descriptionsView = itemView.findViewById(R.id.descriptions);
            Like = itemView.findViewById(R.id.LikeBTNPost);
            partage = itemView.findViewById(R.id.partagePost);
            likeNbActu = itemView.findViewById(R.id.nbLike);
        }
    }
}

