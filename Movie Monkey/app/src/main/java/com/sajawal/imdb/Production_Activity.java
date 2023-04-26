package com.sajawal.imdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sajawal.imdb.user.EditProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;


public class Production_Activity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    RecyclerView recyclerView;
    TextView p_name;
    TextView p_founded;
    ImageView p_logo;
    TextView p_description;
    DatabaseReference databaseReference;
    RecyclerViewAdapterPA recyclerViewAdapterPA;
    Movie_Detail_API movie_detail_api;
    Executive_Production_Company production_companies;
    String Id;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("/Production_Companies");
        setContentView(R.layout.activity_production);
        Intent intent= getIntent();
        Id=intent.getStringExtra("Id");

        //finding view by id
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPA);
        recyclerView.setHasFixedSize(true);
        p_name = findViewById(R.id.Production_name);
        p_logo = findViewById(R.id.Production_logo);
        p_founded = findViewById(R.id.foundedDateValue);
        p_description = findViewById(R.id.production_description_value);
        progressBar=findViewById(R.id.progressBar2);
        bottomNav = findViewById(R.id.bottom_navP);

        configureBottomNav();
        Log.d("check", "RECYCLER VIEW FOUND");
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
        Log.d("check", "aDAPTER SET");

        Log.d("check", "ADAPTER APPLIED");
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://imdb-api.com/API/AdvancedSearch/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        movie_detail_api = retrofit.create(Movie_Detail_API.class);
        databaseReference.child(Id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("check", "getting data");
                    production_companies = dataSnapshot.getValue(Executive_Production_Company.class);
                    p_name.setText(production_companies.getName());
                    p_description.setText(production_companies.getDescription());
                    p_founded.setText(production_companies.getFounded());
                    Picasso.get().load(production_companies.getLogo()).resize(300, 500).into(p_logo, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onError(Exception e) {
                            Log.d("check","Picasso error");
                        }
                    });
                    if (production_companies.getSearch() == null) {
                        Log.d("check", "get search null");
                        return;
                    }
                    Log.d("check", production_companies.getSearch());
                    Call<Production_Movie_Model> call = movie_detail_api.getProductionMovies("feature",production_companies.getSearch());
                    call.enqueue(new Callback<Production_Movie_Model>() {
                        @Override
                        public void onResponse(Call<Production_Movie_Model> call, Response<Production_Movie_Model> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Production_Activity.this, "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
                                Log.d("check", "API ERROR");
                                return;
                            }
                            Production_Movie_Model production_movie_model = response.body();
                            List<Production_Movie> production_movie = production_movie_model.getResults();
                            if (production_movie != null) {
                                Log.d("check", production_movie.get(5).getImage());
                                recyclerViewAdapterPA = new RecyclerViewAdapterPA(Production_Activity.this,production_movie);
                                recyclerView.setAdapter(recyclerViewAdapterPA);
                            } else {
                                Log.d("check", "empty");

                            }
                        }

                        @Override
                        public void onFailure(Call<Production_Movie_Model> call, Throwable t) {
                            Log.d("check", "failure");
                            Toast.makeText(Production_Activity.this, "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
                            return;
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("check", "database reference created");
            }
        });


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
                        intent =new Intent(getApplicationContext(), SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        intent =new Intent(getApplicationContext(), MainActivity.class);
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

}