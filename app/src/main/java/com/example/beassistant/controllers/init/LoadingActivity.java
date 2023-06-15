package com.example.beassistant.controllers.init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.example.beassistant.controllers.MainActivity;
import com.example.beassistant.controllers.logins.LoginController;
import com.example.beassistant.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingActivity extends AppCompatActivity {


    private LoginController loginController;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (user != null){
            fillSharedUser();
            return;
        }

        if (acct != null){
            fillGoogleUser(acct);
            return;
        }

        startActivity(new Intent(getApplicationContext(), LoginController.class));
    }

    /**
     * Function to fill the google user
     */
    protected void fillGoogleUser(GoogleSignInAccount acct){
        db.collection("users").document(acct.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // Check if task is successful
                if (!task.isSuccessful()){
                    return;
                }

                DocumentSnapshot doc = task.getResult();

                User user = new User();

                user.setId(acct.getId());
                user.setUsername(acct.getDisplayName() + acct.getId());
                user.setName(acct.getDisplayName());
                user.setImg_reference("/profileImages/default-profile.png");
                user.setEmail(acct.getEmail());
                user.setNumOpiniones(doc.getDouble("numOpiniones").intValue());
                user.setNumSeguidores(doc.getDouble("numSeguidores").intValue());
                user.setNumSeguidos(doc.getDouble("numSeguidos").intValue());

                Shared.myUser = user;

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    protected void fillSharedUser(){

        // Get the current user
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Check if task is successful
                        if (!task.isSuccessful()){
                            return;
                        }

                        DocumentSnapshot doc = task.getResult();

                        User user = new User();

                        user.setId(doc.getId());
                        user.setUsername(doc.getString("username"));
                        user.setName(doc.getString("name"));
                        user.setImg_reference(doc.getString("imgRef"));
                        user.setEmail(doc.getString("email"));
                        user.setNumOpiniones(doc.getDouble("numOpiniones").intValue());
                        user.setNumSeguidores(doc.getDouble("numSeguidores").intValue());
                        user.setNumSeguidos(doc.getDouble("numSeguidos").intValue());

                        Shared.myUser = user;

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
    }
}