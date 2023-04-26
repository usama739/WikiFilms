package com.sajawal.imdb.Search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sajawal.imdb.MovieSlider.MovieAdapter
import com.sajawal.imdb.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class SearchResultAdapter(var results: List<SearchResult>,private val listner: SearchResultAdapter.OnSearchClickListener, private val check_intent:Int): RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultAdapter.ViewHolder, position: Int) {
        holder.searchTitle.text = results.get(position).title
        val picasso = Picasso.get()
        picasso.load(results.get(position).image).resize(192, 264)
            .into(holder.searchCover, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.setVisibility(View.GONE)
                }

                override fun onError(e: Exception) {
                    Log.d("check", "Picasso error")
                }
            });
    }

    override fun getItemCount(): Int {
        return results.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        var searchCover: ImageView
        var searchTitle: TextView
        lateinit var progressBar: ProgressBar
        init{
            searchCover = view.findViewById(R.id.searchImageView)
            searchTitle = view.findViewById(R.id.searchTitle)
            progressBar = view.findViewById(R.id.progressBarSR)
            view.setOnClickListener(this);
        }
        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listner.onSearchClick(results.get(position),check_intent)
            }
        }
    }
    fun updateList(newLs: List<SearchResult>){
        results = newLs
        notifyDataSetChanged()
    }
    interface OnSearchClickListener {
        fun onSearchClick(clicked_item: SearchResult,check:Int)
    }
}