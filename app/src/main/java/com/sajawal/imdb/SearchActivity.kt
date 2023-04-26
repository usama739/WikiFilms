package com.sajawal.imdb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.sajawal.imdb.MovieSlider.APIResponse
import com.sajawal.imdb.MovieSlider.Movie
import com.sajawal.imdb.MovieSlider.MovieAdapter
import com.sajawal.imdb.MovieSlider.RetrofitInstance
import com.sajawal.imdb.Search.MovieSearchResult
import com.sajawal.imdb.Search.SearchResult
import com.sajawal.imdb.Search.SearchResultAdapter
import com.sajawal.imdb.user.EditProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class SearchActivity : AppCompatActivity(),SearchResultAdapter.OnSearchClickListener {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var searchBar: android.widget.SearchView
    private lateinit var advancedSettings: ImageButton
    private lateinit var searchMovies : ToggleButton
    private lateinit var searchPHouses : ToggleButton
    private lateinit var advancedSearchBar: LinearLayout
    private lateinit var msearchResultAdapter: SearchResultAdapter
    private lateinit var psearchResultAdapter: SearchResultAdapter
    private lateinit var movieSearchList: RecyclerView
    private lateinit var productionSearchList: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var list: ArrayList<SearchResult>
    private var movieService = RetrofitInstance.movieService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        registerViews()
        configureBottomNav()
        configureAdvancedSearch()
    }

    private fun registerViews(){
        bottomNav = findViewById(R.id.bottom_nav)
        searchBar = findViewById(R.id.searchView)
        advancedSearchBar = findViewById(R.id.advancedSearchBar)
        advancedSettings = findViewById(R.id.imgView)
        searchMovies = findViewById(R.id.searchMovie)
        searchPHouses = findViewById(R.id.searchProduction)
        movieSearchList = findViewById(R.id.searchRecyclerView)
        productionSearchList=findViewById(R.id.searchRecyclerView2)
        databaseReference= FirebaseDatabase.getInstance().getReference("/Production_Companies")
        list= arrayListOf<  SearchResult>()
    }
    private fun configureBottomNav(){
        bottomNav.selectedItemId = R.id.search
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    intent = Intent(this@SearchActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    overridePendingTransition(0,0)
                    startActivity(intent)
                }
                R.id.watchlist -> {
                    intent = Intent(this@SearchActivity, WatchListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.profile -> {
                    intent = Intent(this@SearchActivity, EditProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
            false
        }
    }

    private fun configureAdvancedSearch(){

        advancedSearchBar.visibility = View.GONE
        searchMovies.isChecked = true;
        searchPHouses.isChecked = true;
        advancedSettings.setOnClickListener {
            if(advancedSearchBar.visibility == View.VISIBLE) {
                advancedSearchBar.visibility = View.GONE
            }
            else if(advancedSearchBar.visibility == View.GONE){
                advancedSearchBar.visibility = View.VISIBLE
            }
        }
        movieSearchList.layoutManager = GridLayoutManager(this,3 )
        //var searchList: MutableList<SearchResult> = mutableListOf()
        msearchResultAdapter= SearchResultAdapter(mutableListOf<SearchResult>(),this@SearchActivity,0)
        movieSearchList.adapter = msearchResultAdapter

        productionSearchList.layoutManager = GridLayoutManager(this,3 )
        psearchResultAdapter= SearchResultAdapter(list,this@SearchActivity,1)
        productionSearchList.adapter = psearchResultAdapter

        searchBar.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(searchTerm: String?): Boolean {
                //searchBar.clearFocus()
                Log.d("Search", searchTerm.toString())
                //movieList.clear()
                if(searchMovies.isChecked){
                    movieSearchList.visibility=View.VISIBLE
                    val movies: Call<MovieSearchResult> = movieService.searchMovies(searchTerm.toString())
                    movies.enqueue(object: Callback<MovieSearchResult> {
                        override fun onResponse(call: Call<MovieSearchResult>, response: Response<MovieSearchResult>) {
                            if(response.isSuccessful){
                                val movieResponse = response.body()
                                if(movieResponse != null){
                                    msearchResultAdapter.updateList(movieResponse.results)
                                    //movieList.addAll(movieResponse.items)
                                }

                            }
                        }

                        override fun onFailure(call: Call<MovieSearchResult>, t: Throwable) {
                            Toast.makeText(this@SearchActivity, "Error: Couldn't establish connection with the server", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
                else{
                    movieSearchList.visibility=View.GONE
                }
                if(searchPHouses.isChecked){
                    productionSearchList.visibility=View.VISIBLE
                    Log.d("check","Production checked")
                    databaseReference.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                list.clear()
                                Log.d("check", searchTerm.toString()+"entered on data change")
                                for (production_snapshot in p0.children) {

                                    var executiveProductionCompany =
                                        production_snapshot.getValue(Executive_Production_Company::class.java)
                                    if (executiveProductionCompany != null && production_snapshot != null) {
                                        if(executiveProductionCompany.getName().lowercase().contains(searchTerm.toString().lowercase())
                                            ||executiveProductionCompany.getDescription().lowercase().contains(searchTerm.toString().lowercase())
                                        ) {
                                            val searchResult = production_snapshot.getKey()?.let {
                                                SearchResult(
                                                    it,
                                                    executiveProductionCompany.getLogo(),
                                                    executiveProductionCompany.getName()
                                                )
                                            }
                                            Log.d("check", "added list")
                                            list.add(searchResult!!)
                                        }
                                    } else {
                                        Log.d("check", "failure")
                                        Toast.makeText(
                                            this@SearchActivity,
                                            "Can't search Production Houses Please Try again Later",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return
                                    }
                                }
                            }
                            if(list.isEmpty())
                            {
                                Log.d("check", " productionSearchList.visibility=View.GONE")
                                productionSearchList.visibility=View.GONE
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("check", "failure")
                            Toast.makeText(
                                this@SearchActivity,
                                "Can't search Production Houses Please Try again Later",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                    })
                    psearchResultAdapter.updateList(list)


                }else{
                    productionSearchList.visibility=View.GONE
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })


    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.search
    }

    override fun onSearchClick(clicked_item: SearchResult,check:Int) {
        //Toast.makeText(this, "item$position clicked", Toast.LENGTH_SHORT).show()
        if(check==0) {
            val intent = Intent(this@SearchActivity, Movie_Detail::class.java)
            intent.putExtra("Id", clicked_item.id)
            startActivity(intent)
        }else{
            val intent = Intent(this@SearchActivity, Production_Activity::class.java)
            intent.putExtra("Id", clicked_item.id)
            startActivity(intent)
        }
    }


}