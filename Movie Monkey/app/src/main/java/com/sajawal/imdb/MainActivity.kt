package com.sajawal.imdb

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.sajawal.imdb.MovieSlider.APIResponse
import com.sajawal.imdb.MovieSlider.Movie
import com.sajawal.imdb.MovieSlider.MovieAdapter
import com.sajawal.imdb.MovieSlider.RetrofitInstance
import com.sajawal.imdb.Slider.SliderAdapter
import com.sajawal.imdb.Slider.SliderItem
import com.sajawal.imdb.user.EditProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


val SLIDER_ITEMS = 5

class MainActivity : AppCompatActivity(),MovieAdapter.OnItemClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var sliderHandler: Handler
    private lateinit var trendingMovieAdapter: MovieAdapter
    private lateinit var trendingMovies: RecyclerView
    private lateinit var upComingMovieAdapter: MovieAdapter
    private lateinit var upComingMovies: RecyclerView
    private lateinit var topMoviesAdapter: MovieAdapter
    private lateinit var topMovies: RecyclerView
    private lateinit var executiveCompaniesAdapter: MovieAdapter
    private lateinit var executiveCompanies: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var list: ArrayList<Movie>
    private var movieList = ArrayList<SliderItem>()
    private var movieService = RetrofitInstance.movieService
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var clicks:Int=0;
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        databaseReference=FirebaseDatabase.getInstance().getReference("/Production_Companies")

        list = arrayListOf<Movie>()

        setUpSlider()
        registerViews()
        setupAutomaticSlideShow()
        setUpTrendingMovies()
        setUpComingMovies()
        setUpTopMovies()
        setUpExecutiveCompanies();
        configureBottomNav()


    }

    private fun registerViews() {
        viewPager = findViewById(R.id.slider)
        trendingMovies = findViewById(R.id.trendingMovies)
        upComingMovies = findViewById(R.id.comingSoonMovies)
        topMovies = findViewById(R.id.topMoviesList)
        bottomNav = findViewById(R.id.bottom_nav)
        executiveCompanies=findViewById(R.id.executivecompaniesList)
    }

    private fun setupAutomaticSlideShow() {
        sliderAdapter = SliderAdapter(movieList)
        viewPager.adapter = sliderAdapter

        viewPager.setClipToPadding(false)
        viewPager.setClipChildren(false)
        viewPager.setOffscreenPageLimit(2)
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {

            override fun transformPage(page: View, position: Float) {
                val r: Float = 1.toFloat() - Math.abs(position)
                page.setScaleY(0.85f + r * 0.15f);
            }
        })

        viewPager.setPageTransformer(compositePageTransformer)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler = Handler(Looper.getMainLooper())
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    private val sliderRunnable =
        Runnable { viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % 5) }

    private fun setUpTrendingMovies() {

        val movies: Call<APIResponse> = movieService.getTrendingMovies()
        movies.enqueue(object: Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if(response.isSuccessful){
                    trendingMovies.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
                    val movieResponse = response.body()
                    if(movieResponse != null){
                        trendingMovieAdapter = MovieAdapter(movieResponse.items,this@MainActivity,0)
                        trendingMovies.adapter = trendingMovieAdapter
                    }
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Unable to Connect to Server", Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun setUpComingMovies() {

        val movies: Call<APIResponse> = movieService.getUpComingMovies()
        movies.enqueue(object: Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if(response.isSuccessful){
                    upComingMovies.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
                    val movieResponse = response.body()
                    if(movieResponse != null){
                        upComingMovieAdapter = MovieAdapter(movieResponse.items,this@MainActivity,0)
                        upComingMovies.adapter = upComingMovieAdapter
                    }
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Unable to Connect to Server", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setUpTopMovies() {

        val movies: Call<APIResponse> = movieService.getTopMovies()
        movies.enqueue(object: Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if(response.isSuccessful){
                    topMovies.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
                    val movieResponse = response.body()
                    if(movieResponse != null){
                        topMoviesAdapter = MovieAdapter(movieResponse.items,this@MainActivity,0)
                        topMovies.adapter = topMoviesAdapter
                    }
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Unable to Connect to Server", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setUpExecutiveCompanies(){
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    list.clear()
                    for(production_snapshot in p0.children){

                        var executiveProductionCompany=production_snapshot.getValue(Executive_Production_Company::class.java)
                        if (executiveProductionCompany != null && production_snapshot!=null ) {
                            val movie= production_snapshot.getKey()?.let { Movie(it,executiveProductionCompany.getName(),"",executiveProductionCompany.getLogo()) }
                            list.add(movie!!)
                        }else{
                            Log.d("check", "failure")
                            Toast.makeText(
                                this@MainActivity,
                                "Can't retrieve Executive Companies Please Try again Later",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }
                    }
                    executiveCompanies.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
                    executiveCompaniesAdapter = MovieAdapter(list,this@MainActivity,1)
                    executiveCompanies.adapter = executiveCompaniesAdapter
                    executiveCompanies.adapter!!.notifyDataSetChanged()

                }else{
                    Log.d("check", "failure")
                    Toast.makeText(
                        this@MainActivity,
                        "Can't retrieve Executive Companies Please Try again Later",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("check", "failure")
                Toast.makeText(
                    this@MainActivity,
                    "Can't retrieve Executive Companies Please Try again Later",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        })
    }


            private fun configureBottomNav(){
        bottomNav.selectedItemId = R.id.home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.search -> {
                    intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.watchlist -> {
                    intent = Intent(this@MainActivity, WatchListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }

                R.id.profile -> {
                    intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.home
    }

    override fun onItemClick(clicked_item: Movie,check:Int) {
        clicks += 1
        if (clicks % 2 == 0) {
            AdManagerInterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                AdManagerAdRequest.Builder().build(),
                object : AdManagerInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError?.message?.let { Log.d("check", it) }
                        mAdManagerInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                        Log.d("check", "Ad was loaded.")
                        mAdManagerInterstitialAd = interstitialAd
                    }
                })
            mAdManagerInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("check", "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        Log.d("check", "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("check", "Ad showed fullscreen content.")
                        mAdManagerInterstitialAd = null
                    }
                }

            if (mAdManagerInterstitialAd != null) {
                mAdManagerInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }

        }
        if (check == 0) {
            val intent = Intent(this@MainActivity, Movie_Detail::class.java)
            intent.putExtra("Id", clicked_item.id)
            startActivity(intent)
        } else {
            val intent = Intent(this@MainActivity, Production_Activity::class.java)
            intent.putExtra("Id", clicked_item.id)
            startActivity(intent)

        }
    }

    private fun setUpSlider(){
        movieList.add(SliderItem(R.drawable.image4))
        movieList.add(SliderItem(R.drawable.image5))
        movieList.add(SliderItem(R.drawable.image6))
        movieList.add(SliderItem(R.drawable.image7))
        movieList.add(SliderItem(R.drawable.image8))
    }
}




    /*override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }*/
