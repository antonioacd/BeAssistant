package com.example.beassistant.controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseController {

    private FirebaseFirestore db;
    public HashMap<String, String> listLogUsers;
    public HashMap<String, String> listUsernames;

    public DataBaseController() {
         this.db = FirebaseFirestore.getInstance();
         listLogUsers = new HashMap<>();
         listUsernames = new HashMap<>();
    }

    public void addUser(User user_get){

        /**
         * Create a new user with username, gmail, password and phone number
         */
        Map<String, Object> user = new HashMap<>();
        user.put("username", user_get.getUsername());
        user.put("name", user_get.getName());
        user.put("email", user_get.getEmail());
        user.put("password", user_get.getPassword());

        /**
         * Add a new document with a generated ID
         */
        db.collection("users").document(user_get.getUsername())
                .set(user);
    }

    public void getLogins(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                listLogUsers.put(doc.getString("username"), doc.getString("password"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getUsernames(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                listUsernames.put(doc.getString("username"), doc.getString("email"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
