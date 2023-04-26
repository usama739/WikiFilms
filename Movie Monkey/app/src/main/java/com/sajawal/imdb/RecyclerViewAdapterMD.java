package com.sajawal.imdb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterMD extends RecyclerView.Adapter<RecyclerViewAdapterMD.ViewHolder> {
    private Context context;
    private List<Actor> actors;
    String[] name;
    int[] photo;
    public RecyclerViewAdapterMD(Context context, List<Actor> actors) {
        this.context=context;
       this.actors=new ArrayList<>(actors);
    }


    @NonNull
    @Override
    public RecyclerViewAdapterMD.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Actor actor = actors.get(position);
        holder.name.setText(actor.getName());
        Picasso.get().load(actor.getImage()).resize(260, 260).into(holder.photo, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onError(Exception e) {
                Log.d("check","Picasso error");
            }
        });
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView photo;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.name);
            photo=(ImageView) itemView.findViewById(R.id.imageView);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBarMD);
        }

    }
}
