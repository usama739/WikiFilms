package com.sajawal.imdb.user;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sajawal.imdb.MainActivity;
import com.sajawal.imdb.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.sajawal.imdb.R;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
     FirebaseAuth mAuth; //instance for firebase

    private TextView registerLogo;
    private EditText Username, Email, Password, ConfirmPassword;
    private Button RegisterUser;

    FirebaseFirestore fstore; //instance for firestore
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Register the View Items being Used
        Username = findViewById(R.id.inputUsername);
        Email = findViewById(R.id.inputEmail);
        Password = findViewById(R.id.inputPassword);
        ConfirmPassword = findViewById(R.id.inputConformPassword);
        RegisterUser = findViewById(R.id.btnRegister);
        registerLogo = findViewById(R.id.logo);



        mAuth = FirebaseAuth.getInstance(); //instance of object of firebase
        fstore = FirebaseFirestore.getInstance();

        TextView btn=findViewById(R.id.alreadyHaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        //Listeners for View Used in this Activity
        RegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //store values of each field in Strings
                String userName = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String confirmPassword = ConfirmPassword.getText().toString();

                if (userName.isEmpty()==true){
                    Username.setError("UserName is Required");
                    Username.requestFocus();
                    return;
                }
                if(email.isEmpty()==true){
                    Email.setError("Email is Required");
                    Email.requestFocus();
                    return;
                }
                if(password.isEmpty()==true){
                    Password.setError("Password is Required");
                    Password.requestFocus();
                    return;
                }
                if(confirmPassword.isEmpty()==true){
                    ConfirmPassword.setError("Password is Required");
                    ConfirmPassword.requestFocus();
                    return;
                }

                //Firebase doesnot accept a password less than 6 character long
                if(password.length()<6){
                    Password.setError("Password should have atleast 6 Characters");
                    Password.requestFocus();
                    return;
                }
               // if(confirmPassword.length()<6){
                 //   ConfirmPassword.setError("Password Should Have Atleast 6 Characters");
                   // ConfirmPassword.requestFocus();
                   // return;
                //}
                if(!password.equals(confirmPassword)){
                    ConfirmPassword.setError("Password Donot Match");
                    ConfirmPassword.requestFocus();
                    return;
                }

                //to check if email entered has valid format and "@" and "."
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Email.setError("Provide Valid Email");
                    Email.requestFocus();
                    return;
                }

                Toast.makeText(RegisterActivity.this,"User Data Validated",Toast.LENGTH_LONG).show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserID = mAuth.getCurrentUser().getUid();
                        //create a new collection for user's data
                        DocumentReference documentReference = fstore.collection("/users").document(UserID);
                        //Data Storage using Hash Map
                        Map<String,Object> newUser = new HashMap<>();
                        newUser.put("U_Name",userName);
                        //newUser.put("U_Email",email);
                        //newUser.put("U_Password",password);
                        //newUser.put("U_CPassword",confirmPassword);

                        documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"onSuccess : User Profile is Created Successfully for User "+UserID);

                            }
                        });

                        //make intent to the activity where you want to go
                        //currently making it move to Main after success

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }


        });

    }
}
