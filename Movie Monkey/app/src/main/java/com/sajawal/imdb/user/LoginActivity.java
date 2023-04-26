package com.sajawal.imdb.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.sajawal.imdb.MainActivity;
import com.sajawal.imdb.R;
import com.sajawal.imdb.SearchActivity;
import com.sajawal.imdb.WatchListActivity;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    EditText LoginEmail,LoginPassword;
    Button LoginBtn ;
    FirebaseAuth firebaseAuth_login;
    FirebaseFirestore fstore;
    TextView forgetPasswordview;
    Button register;
    ViewGroup progressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideProgressingView();
        // Register Views
        register = findViewById(R.id.textViewSignUp);
        LoginEmail = findViewById(R.id.inputEmail);
        LoginPassword = findViewById(R.id.inputPassword);
        forgetPasswordview = (TextView )findViewById(R.id.forgotPassword);
        LoginBtn = findViewById(R.id.btnlogin);

        //get instance for firebase object
        firebaseAuth_login = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        /*if(firebaseAuth_login.getUid() != null){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sajawal.imdb.user.RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(LoginActivity.this, com.sajawal.imdb.user.RegisterActivity.class));
            }
        });

        /*//Goto Edit your Profile
        Button editProfileButton = findViewById(R.id.editLogin);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, com.sajawal.imdb.user.EditProfileActivity.class));
            }
        });*/

        forgetPasswordview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, com.sajawal.imdb.user.ForgotPasswordActvity.class));
            }
        });
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressingView();
                //Extract Data
                String Login_Email = LoginEmail.getText().toString();
                String Login_Password = LoginPassword.getText().toString();

                //validate the use data
                if (Login_Email.isEmpty()){
                    LoginEmail.setError("Email is Missing");
                    LoginEmail.requestFocus();
                    hideProgressingView();
                    return;
                }
                if (Login_Password.isEmpty()){
                    LoginPassword.setError("Password is Missing");
                    LoginPassword.requestFocus();
                    hideProgressingView();
                    return;
                }

                firebaseAuth_login.signInWithEmailAndPassword(Login_Email,Login_Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //when login is successfull intent to required activuty
                        //currently moving it to main

                        Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressingView();
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



    }

    public void showProgressingView() {
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.progressbar_layout, null);
            View v = this.findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);

    }

    public void hideProgressingView() {
        View v = this.findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
    }
}
