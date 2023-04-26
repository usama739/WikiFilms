package com.sajawal.imdb.WatchList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sajawal.imdb.MovieSlider.APIResponse
import com.sajawal.imdb.MovieSlider.Movie
import com.sajawal.imdb.MovieSlider.MovieAdapter
import com.sajawal.imdb.MovieSlider.RetrofitInstance
//import com.sajawal.imdb.MovieSlider.RetrofitInstance.movieService
import com.sajawal.imdb.R
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchListAdapter(var watchList: List<String>, private val listner: WatchListAdapter.OnItemClickListener) : RecyclerView.Adapter<WatchListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.watch_list_item, parent, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieId = watchList.get(position)
        val movieService = RetrofitInstance.movieService
        val movie: Call<WatchListItem> = movieService.getMovieDetail(movieId)
        movie.enqueue(object: Callback<WatchListItem> {
            override fun onResponse(call: Call<WatchListItem>, response: Response<WatchListItem>) {
                if(response.isSuccessful){
                    val movieResponse = response.body()
                    if(movieResponse != null){
                        holder.movieTitle.text = movieResponse.fullTitle
                        Picasso.get().load(movieResponse.image).resize(326, 448).into(holder.movieCover,  object :
                            com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                holder.progressBar.setVisibility(View.GONE)
                            }

                            override fun onError(e: Exception) {
                                Log.d("check", "Picasso error")
                            }
                        });
                    }
                }
            }

            override fun onFailure(call: Call<WatchListItem>, t: Throwable) {
                //Toast.makeText(this@MainActivity,"Unable to Connect to Server", Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun getItemCount(): Int {
        return watchList.size
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view),View.OnClickListener {
        var movieCover = view.findViewById<ImageView>(R.id.watchListCover)
        var movieTitle = view.findViewById<TextView>(R.id.watchListTitle)
        //var removeButton = view.findViewById<Button>(R.id.removeMovie)
        lateinit var progressBar: ProgressBar

        init{
            progressBar = view.findViewById(R.id.progressBarWA)
            view.setOnClickListener(this);
        }
        override fun onClick(p0: View?) {

            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listner.onItemClick(watchList.get(position))
            }
        }
    }

    fun updateList(newList: List<String>){
        watchList = newList
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(clicked_item: String)
    }

}