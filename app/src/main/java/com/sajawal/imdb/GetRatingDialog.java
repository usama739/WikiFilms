package com.sajawal.imdb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class GetRatingDialog extends AppCompatDialogFragment {
    private TextView ratingtextview;
    //private Button addRating;
    private RatingBar ratingBar;
    private RatingBar activityRatingBar;
    private TextView activityTextView;
    private DocumentReference movieRatings;
    FirebaseFirestore fStore;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.get_rating_dialog,null);
        ratingtextview= view.findViewById(R.id.rating_g);
        ratingBar= view.findViewById(R.id.ratingBar_g);
        activityRatingBar = getActivity().findViewById(R.id.ratingBar_d);
        activityTextView = getActivity().findViewById(R.id.rating_d);


        Bundle bundle = getArguments();
        String movieID = bundle.getString("MOVIE_ID");
        String userID = bundle.getString("USER_ID");

        fStore = FirebaseFirestore.getInstance();
        movieRatings = fStore.collection("/ratings").document(movieID);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
               ratingtextview.setText(Integer.toString((int)ratingBar.getRating()));
            }});

        builder.setView(view).setTitle("Give Rating")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(ratingBar.getRating()!=0.0) {

                            storeRating(movieID, userID, (int)ratingBar.getRating());

                            //FOR ONE TIME REVIEW
                            //addRating = getActivity().findViewById(R.id.AddRating);
                            //addRating.setVisibility(View.GONE);

                            //Log.d("Reached", "Store Rating Outside");
                            setRating();


                            Toast.makeText(getActivity(), "Rating Added!",
                                    Toast.LENGTH_LONG).show();
                            //rating code
                        }else{
                            Toast.makeText(getActivity(), "Can't Submit 0 Star Rating!",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

        return builder.create();
    }

    private void storeRating(String movieID, String userID, int rating){
        Log.d("Reached", movieID);
        Log.d("Reached", userID);
        //Log.d("Reached", (String) rating);
        DocumentReference movieRatings  = fStore.collection("ratings").document(movieID);
        HashMap<String, Object> userRating = new HashMap<String, Object>();
        userRating.put(userID, rating);
        movieRatings.set(userRating, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(getActivity(), "Rating Updated Successfully",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to Update Rating",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setRating() {
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

                activityRatingBar.setRating(rating);
                activityTextView.setText(Float.toString(rating));
            }
        });
    }
}
