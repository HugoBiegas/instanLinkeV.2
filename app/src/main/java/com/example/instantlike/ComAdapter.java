package com.example.instantlike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder>{

    private ArrayList<String> comList;
    private Context context;
    private TextView textView;
    //initialise les variables quand on appelle la clase avec les paramétres données
    public ComAdapter(ArrayList<String> comList, Context context) {
        this.comList = comList;
        this.context = context;
    }

    @NonNull
    @Override
    public ComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcommentaire, parent, false);
        return new ComAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        textView.setText(comList.get(position));
    }

    @Override
    public int getItemCount() {
        return comList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View Itemview) {
            super(Itemview);
            textView = Itemview.findViewById(R.id.unCommentaire);

        }
    }
}
