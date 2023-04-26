package com.sajawal.imdb.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sajawal.imdb.R;

public class ForgotPasswordActvity extends AppCompatActivity {
    EditText forgotPassEmail;
    Button resetpassword;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_actvity);

        forgotPassEmail = findViewById(R.id.inputforgetPasswordEmail);
        resetpassword = findViewById(R.id.btnResetPassword);
       firebaseAuth = FirebaseAuth.getInstance();

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPassEmail.getText().toString();

                //check if user has entered correct email
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    forgotPassEmail.setError("Enter Valid Email");
                    forgotPassEmail.requestFocus();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActvity.this,"Check Email To Reset Password",Toast.LENGTH_LONG).show();
                            //navigate activity back to login page
                            startActivity(new Intent(ForgotPasswordActvity.this,LoginActivity.class));
                        }
                        else{
                            Toast.makeText(ForgotPasswordActvity.this,"Failed To Reset Password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                /*firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPasswordActvity.this,"Check Email To Reset Password",Toast.LENGTH_LONG).show();
                        //navigate activity back to login page
                        startActivity(new Intent(ForgotPasswordActvity.this,LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActvity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });*/
            }
        });
    }
}