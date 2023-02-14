package com.example.instantlike.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageData {
    private String imageUri;
    private String imageName;
    private String titre;
    private String description;
    private String iconUri;
    private String nomUtilisateur;
    private int likeCount;
    private boolean isLiked;
    static private List<ImageData> images = new ArrayList<>();


    public ImageData(String imageUri, String imageName, String titre, String description, String iconUri, String nomUtilisateur) {
        this.imageUri = imageUri;
        this.imageName = imageName;
        this.titre = titre;
        this.description = description;
        this.iconUri = iconUri;
        this.nomUtilisateur = nomUtilisateur;
        images.add(this);
    }

    static public List<ImageData> getimages() {return images;}

    public String getImageUri() {
        return imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUri() {
        return iconUri;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }



}