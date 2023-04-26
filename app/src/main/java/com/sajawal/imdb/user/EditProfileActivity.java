package com.sajawal.imdb.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.sajawal.imdb.LauncherActivity;
import com.sajawal.imdb.MainActivity;
import com.sajawal.imdb.R;
import com.sajawal.imdb.SearchActivity;
import com.sajawal.imdb.WatchListActivity;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editUserName, editPassword, editConfirmPassword;
    Button saveEditedChanges, changePass;
    FirebaseAuth firebaseAuth_login;
    DocumentReference documentReference;
    FirebaseFirestore fstore;
    String UserId;
    FirebaseUser user;
    BottomNavigationView bottomNav;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth_login = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = firebaseAuth_login.getCurrentUser();
        UserId = firebaseAuth_login.getCurrentUser().getUid();
        documentReference = fstore.collection("/users").document(UserId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Register Views
        editUserName = findViewById(R.id.inputUsername);
        //editEmail = findViewById(R.id.inputEmail);
        editPassword = findViewById(R.id.inputPassword);
        editConfirmPassword = findViewById(R.id.inputConformPassword);
        bottomNav = findViewById(R.id.bottom_nav);
        saveEditedChanges = findViewById(R.id.btnEditProfileRegister);
        changePass = findViewById(R.id.btnChangePass);
        logout = findViewById(R.id.logout);


        ShowUserData();
        configureBottomNav();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String oldPass = editPassword.getText().toString();
                String newPass = editConfirmPassword.getText().toString();
                String email = user.getEmail();
                if(oldPass.isEmpty()){
                    editPassword.setError("Old Password can't be empty");
                    return;
                }
                if(newPass.length() < 6){
                    editConfirmPassword.setError("New Password should be atleast 6 characters long");
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            /*if(!newPass.equals(newConfirmPass)){
                                editConfirmPassword.setError("Passwords don't match");
                                return;
                            }*/
                            user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditProfileActivity.this, "Password Reset successfully", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileActivity.this, "Failed to reset Password", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(EditProfileActivity.this, "Reset Failed: Wrong Password!", Toast.LENGTH_LONG).show();
                        }
                    }
                });



            }
        });

        //after displaying previous data of LOGGED IN user update it
        saveEditedChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String name = editUserName.getText().toString();
                if(name.isEmpty()){
                    editUserName.setError("Username can't be empty");
                    return;
                }
                //String email = editEmail.getText().toString();
                //String password = editPassword.getText().toString();
                //String cpassword = editConfirmPassword.getText().toString();

                //Store updated fields back to FIRESTORE DATABSE
                Map<String,Object> updatedUser = new HashMap<>();
                updatedUser.put("U_Name",name);
                //updatedUser.put("U_Email",email);
                //updatedUser.put("U_Password",password);
                //updatedUser.put("U_CPassword",cpassword);

                //it will update values of altered fields and merge those changes with previous valuesgi
                documentReference.set(updatedUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfileActivity.this,"User Credential Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void ShowUserData() {
        DocumentReference documentReference = fstore.collection("/users").document(UserId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //Log.d("Check", value.getString("U_Name"));
                if(value != null){
                    editUserName.setText(value.getString("U_Name"));
                }
                //editEmail.setText(value.getString("U_Email"));
                //editPassword.setText(value.getString("U_Password"));
                //editConfirmPassword.setText(value.getString("U_CPassword"));
            }
        });
        /*Intent getIntent = getIntent();
        String newName = getIntent.getStringExtra("UName");
        String newEmail = getIntent.getStringExtra("UEmail");
        String newPass = getIntent.getStringExtra("UPass");
        String newCPass = getIntent.getStringExtra("UCPass");*/

        //update the data
        /*editUserName.setText(docu);
        editEmail.setText(newEmail);
        editPassword.setText(newPass);
        editConfirmPassword.setText(newCPass);*/


    }

    private void configureBottomNav() {
        bottomNav.setSelectedItemId(R.id.profile);
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
                }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.profile);
    }
}