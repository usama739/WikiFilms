package com.sajawal.imdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sajawal.imdb.user.EditProfileActivity;
import com.squareup.picasso.Picasso;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Movie_Detail extends AppCompatActivity {
    BottomNavigationView bottomNav;
    RecyclerView recyclerView;
    ImageView banner;
    WebView webView;
    Button AddRating;
    Button addToWatchList;
    Button deleteFromWatchList;
    ImageView playbutton;
    TextView imagetext;
    TextView releaseDate;
    TextView description;
    TextView director;
    TextView award;
    String Id;
    ProgressBar progressBar;
    Movie_Detail_API movie_detail_api;
    RecyclerViewAdapterMD recyclerViewAdapterMD;
    String URl;

    RatingBar ratingBar;
    TextView ratingText;

    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    DocumentReference userWatchList;
    DocumentReference movieRatings;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        URl="about:blank";

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userWatchList = fStore.collection("/watchlists").document(firebaseAuth.getUid());

        //get movie ID from intent
        Intent intent= getIntent();
        Id=intent.getStringExtra("Id");

        // FireStore Reference to movie
        movieRatings = fStore.collection("/ratings").document(Id);

        //setting ids
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        banner=(ImageView) findViewById(R.id.MainBanner);
        webView=(WebView) findViewById(R.id.webView);
        playbutton=(ImageView) findViewById(R.id.imageView2);
        imagetext=(TextView) findViewById(R.id.image_text);
        releaseDate=(TextView) findViewById(R.id.ReleaseDateValue);
        description=(TextView) findViewById(R.id.DescriptionValue);
        director=(TextView) findViewById(R.id.DirectorValue);
        progressBar=(ProgressBar)findViewById(R.id.progressBarMovieDetail);
        award=(TextView) findViewById(R.id.ProductionCompaniesValue);
        bottomNav = findViewById(R.id.bottom_nav);
        addToWatchList = (Button)findViewById(R.id.AddToWatchList);
        deleteFromWatchList = (Button)findViewById(R.id.deleteFromWatchList);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_d);
        ratingText = (TextView) findViewById(R.id.rating_d);

        setRating();
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager lm=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        //Customizing +/- WatchList Button
       userWatchList.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                   if(documentSnapshot.contains(Id)) {
                       if (documentSnapshot.getBoolean(Id)) {
                           //addToWatchList.setEnabled(false);
                           addToWatchList.setVisibility(View.GONE);
                           //addToWatchList.setText("In WatchList");
                           //addToWatchList.setTextColor(addToWatchList.getContext().getResources().getColor(R.color.item_color));
                           //addToWatchList.setBackgroundColor();
                           Map<String, Object> doc = documentSnapshot.getData();
                           for(String key: doc.keySet()){
                               Log.d("Movie: ", key);
                           }
                       }
                       else{
                           deleteFromWatchList.setVisibility(View.GONE);
                       }
                   }
                   else{
                       deleteFromWatchList.setVisibility(View.GONE);
                   }
               }
               else{
                   deleteFromWatchList.setVisibility(View.GONE);
               }
           }
       });

       //calling API
        recyclerView.setLayoutManager(lm);
        Retrofit retrofit=new Retrofit.Builder().baseUrl("https://imdb-api.com/en/API/Title/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        movie_detail_api=retrofit.create(Movie_Detail_API.class);
        GetMovieDetailsFromAPI();

        configureBottomNav();




        AddRating=findViewById(R.id.AddRating);
        AddRating.setOnClickListener(view -> openGetRatingDialog());

        addToWatchList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                HashMap<String, Object> movie = new HashMap<String, Object>();
                movie.put(Id, true);
                userWatchList.set(movie, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //addToWatchList.setEnabled(false);
                        //addToWatchList.setText("In WatchList");
                        //addToWatchList.setTextColor(addToWatchList.getContext().getResources().getColor(R.color.item_color));
                        addToWatchList.setVisibility(View.GONE);
                        deleteFromWatchList.setVisibility(View.VISIBLE);
                        Toast.makeText(Movie_Detail.this, "Added to Watch List", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Movie_Detail.this, "Failed to add to Watchlist", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

       deleteFromWatchList.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               HashMap<String, Object> movie = new HashMap<String, Object>();
               movie.put(Id, false);
               userWatchList.set(movie, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       //addToWatchList.setEnabled(false);
                       //addToWatchList.setText("In WatchList");
                       //addToWatchList.setTextColor(addToWatchList.getContext().getResources().getColor(R.color.item_color));
                       addToWatchList.setVisibility(View.VISIBLE);
                       deleteFromWatchList.setVisibility(View.GONE);
                       Toast.makeText(Movie_Detail.this, "Deleted from Watch List", Toast.LENGTH_SHORT).show();

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(Movie_Detail.this, "Failed to delete from Watchlist", Toast.LENGTH_SHORT).show();
                   }
               });

           }
       });
    }



    public void GetMovieDetailsFromAPI(){
        Call<Movie_Detail_Model> call= movie_detail_api.getMovieDetails(Id);
        call.enqueue(new Callback<Movie_Detail_Model>() {
            @Override
            public void onResponse(Call<Movie_Detail_Model> call, Response<Movie_Detail_Model> response) {
                if(!response.isSuccessful() || response.body().getTrailer()==null){
                    Toast.makeText(Movie_Detail.this, "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
                    Log.d("check","API ERROR");
                    return;
                }

                Movie_Detail_Model movie_detail_model= response.body();
                Log.d("check",movie_detail_model.getTrailer().getThumbnailUrl() );

                Picasso.get().load(movie_detail_model.getTrailer().getThumbnailUrl()).into(banner, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d("check","Picasso error");
                    }
                });
                Log.d("check",movie_detail_model.getTrailer().getLinkEmbed());
                imagetext.setText(movie_detail_model.getFullTitle());
                recyclerViewAdapterMD= new RecyclerViewAdapterMD(Movie_Detail.this,movie_detail_model.getActorList());
                recyclerView.setAdapter(recyclerViewAdapterMD);
                releaseDate.setText(movie_detail_model.getReleaseDate());
                description.setText(movie_detail_model.getPlot());
                director.setText(movie_detail_model.getDirectors());
                String awards_edited=movie_detail_model.getAwards();
                if(!awards_edited.isEmpty()) {
                    awards_edited = awards_edited.replace('|', '\n');
                    award.setText(awards_edited);
                }
                banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        webView.setWebViewClient(new WebViewClient());
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.loadUrl(movie_detail_model.getTrailer().getLinkEmbed());
                        URl=movie_detail_model.getTrailer().getLinkEmbed();
                        banner.setVisibility(View.INVISIBLE);
                        playbutton.setVisibility(View.INVISIBLE);
                        imagetext.setVisibility(View.INVISIBLE);
                        webView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call<Movie_Detail_Model> call, Throwable t) {
                Log.d("check","API ERROR");
                Toast.makeText(Movie_Detail.this, "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openGetRatingDialog(){
        GetRatingDialog getRatingDialog=new GetRatingDialog();
        Bundle bundle = new Bundle();
        bundle.putString("MOVIE_ID", Id);
        bundle.putString("USER_ID", firebaseAuth.getUid());
        getRatingDialog.setArguments(bundle);
        getRatingDialog.show(getSupportFragmentManager(),"Get Rating Dialog");
        Log.d("Check", "Hello");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    private void configureBottomNav() {
       bottomNav.setSelectedItemId(R.id.invisible);
       bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Intent intent;
               switch(item.getItemId())
               {
                   case R.id.search:
                       intent = new Intent(getApplicationContext(), SearchActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       overridePendingTransition(0,0);
                       return true;
                   case R.id.home:
                       intent = new Intent(getApplicationContext(), MainActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                       overridePendingTransition(0,0);
                       return true;

                   case R.id.watchlist:
                       intent = new Intent(getApplicationContext(), WatchListActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                       overridePendingTransition(0,0);
                       return true;

                   case R.id.profile:
                       intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                       overridePendingTransition(0,0);
                       return true;
               }

               return false;
           }
       });
    }
    @Override
    public void onPause() {
        super.onPause();
        webView.loadUrl("about:blank");
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.loadUrl(URl);
    }

    public void setRating(){
        movieRatings.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                float ratingSum = 0;
                int count = 0;
                if(documentSnapshot.exists()){
                    Map<String, Object> movieData = documentSnapshot.getData();
                    for(String key: movieData.keySet()) {
                        ratingSum += (Long)movieData.get(key);
                        count++;
                    }
                }
                float rating = 0;
                if(count!= 0){
                   rating = ratingSum/count;
                }

                ratingBar.setRating(rating);
                ratingText.setText(Float.toString(rating));
            }
        });
    }
}