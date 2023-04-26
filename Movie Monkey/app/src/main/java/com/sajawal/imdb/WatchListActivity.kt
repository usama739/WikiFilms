package com.sajawal.imdb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sajawal.imdb.MovieSlider.Movie
import com.sajawal.imdb.WatchList.WatchListAdapter
import com.sajawal.imdb.user.EditProfileActivity

class WatchListActivity : AppCompatActivity(),  WatchListAdapter.OnItemClickListener{
    private lateinit var watchList: RecyclerView
    private lateinit var watchListAdapter: WatchListAdapter
    private lateinit var mAdView:AdView
    private var fAuth: FirebaseAuth
    private var fStore : FirebaseFirestore
    val userWatchList: DocumentReference
    private lateinit var bottomNav : BottomNavigationView

    init{
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userWatchList = fStore.collection("/watchlists").document(fAuth.getUid().toString())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_list)
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        registerViews()
        configureBottomNav()
        setRecyclerView()
    }

    private fun registerViews(){
        watchList = findViewById(R.id.watchListRecyclerView)
        bottomNav = findViewById(R.id.bottom_nav)

    }
    private fun setRecyclerView(){
        watchList.layoutManager = LinearLayoutManager(this@WatchListActivity, LinearLayoutManager.HORIZONTAL, false)
        watchListAdapter = WatchListAdapter(arrayListOf<String>(),this)
        watchList.adapter = watchListAdapter
    }

    private fun configureRecyclerView(){
        var movies = arrayListOf<String>()


        //Display Watchlist
        userWatchList.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val doc = documentSnapshot.data
                for (key in doc!!.keys) {
                    if(doc[key] == true) {
                        movies.add(key)
                    }
                }
                watchListAdapter.updateList(movies)
            }
        }
        userWatchList.get().addOnFailureListener{
            Toast.makeText(this, "Failed to Fetch WatchList", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureBottomNav(){
        bottomNav.selectedItemId = R.id.watchlist

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    intent = Intent(this@WatchListActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.search -> {
                    intent = Intent(this@WatchListActivity, SearchActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.profile -> {
                    intent = Intent(this@WatchListActivity, EditProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }

            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.watchlist
        configureRecyclerView()
    }

    override fun onItemClick(clicked_item: String) {
        val intent = Intent(this@WatchListActivity, Movie_Detail::class.java)
        intent.putExtra("Id", clicked_item)
        startActivity(intent)
    }
}