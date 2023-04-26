package com.sajawal.imdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class RecyclerViewAdapterPA extends RecyclerView.Adapter<RecyclerViewAdapterPA.ViewHolder> {
    private Context context;
    List<Production_Movie> production_movie;
    public RecyclerViewAdapterPA(Context context,List<Production_Movie> production_movie ) {
        this.context=context;
        this.production_movie=new ArrayList<>(production_movie);

    }


    @NonNull
    @Override
    public RecyclerViewAdapterPA.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Production_Movie pm = production_movie.get(position);
        holder.name.setText(pm.getTitle());
        Picasso.get().load(pm.getImage()).resize(192, 264).into(holder.photo, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);

            }
            @Override
            public void onError(Exception e) {
                Log.d("check","Picasso error");
            }
        });

    }

    @Override
    public int getItemCount() {
            return production_movie.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView photo;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=(TextView) itemView.findViewById(R.id.movieName);
            photo=(ImageView) itemView.findViewById(R.id.movieImageView);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBarMV);
        }
        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION)
            {
                Intent intent = new Intent(context, Movie_Detail.class);
                intent.putExtra("Id",production_movie.get(position).getId());
                context.startActivity(intent);
            }
        }
    }
}