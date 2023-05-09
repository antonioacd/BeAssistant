package com.example.beassistant.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.beassistant.R;
import com.example.beassistant.adapters.HomeRecyclerAdapter;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter(this);

    ArrayList<Product> auxArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        auxArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Get the opinions
        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Get the opinions
                            for (QueryDocumentSnapshot opinionsDocument : task.getResult()) {
                                Log.d("Lista: ",  opinionsDocument.getId());
                                //Get all the products
                                db.collection("categorias/"+ opinionsDocument.getString("productCategory")+"/marcas/"+ opinionsDocument.getString("productBrand")+"/productos")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    // Get the product with the same id
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.getString("id").equals(opinionsDocument.getId())){
                                                            Log.d("Lista: ",  "Entra: " + auxArrayList);
                                                            Product product = new Product(
                                                                    document.getString("id"),
                                                                    document.getString("name"),
                                                                    document.getString("imgRef"),
                                                                    document.getString("brand"),
                                                                    document.getString("category"),
                                                                    document.getString("type"),
                                                                    5
                                                            );
                                                            auxArrayList.add(product);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            Log.d("Lista: ",  "Hola0");
                            /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);*/
                        } else {
                            Log.d("Lista:", "Error getting documents: ", task.getException());
                        }
                    }
                });




    }
}