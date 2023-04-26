package com.sajawal.imdb.MovieSlider

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sajawal.imdb.R
import com.sajawal.imdb.Search.SearchResult
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MovieAdapter(var movies: List<Movie>, private val listner:OnItemClickListener, private val intent_check:Int): RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        holder.movieTitle.text = movies.get(position).title
        holder.movieYear.text = movies.get(position).year
        val picasso = Picasso.get()
        picasso.load(movies.get(position).image).resize(192, 264)
            .into(holder.movieCover, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.setVisibility(View.GONE)
                }

                override fun onError(e: Exception) {
                    Log.d("check", "Picasso error")
                }
            });
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var movieCover: ImageView
        var movieTitle: TextView
        var movieYear: TextView
        lateinit var progressBar: ProgressBar

        init {
            movieCover = view.findViewById(R.id.movieImageView)
            movieTitle = view.findViewById(R.id.movieName)
            movieYear = view.findViewById(R.id.movieYear)
            progressBar = view.findViewById(R.id.progressBarMV)
            view.setOnClickListener(this);

        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listner.onItemClick(movies.get(position),intent_check)
            }
        }
    }

    fun updateList(newLs: List<Movie>) {
        movies = newLs
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(clicked_item: Movie, check:Int)
    }
}
