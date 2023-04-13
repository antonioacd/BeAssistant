package com.example.beassistant.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DataBaseController {

    public void addUser(User user_get){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /**
         * Create a new user with username, gmail, password and phone number
         */
        Map<String, Object> user = new HashMap<>();
        user.put("username", user_get.getUsername());
        user.put("name", user_get.getName());
        user.put("email", user_get.getEmail());
        user.put("phoneNumber", user_get.getNumber());
        user.put("password", user_get.getPassword());

        /**
         * Add a new document with a generated ID
         */
        db.collection("users").document(user_get.getUsername())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }





}
