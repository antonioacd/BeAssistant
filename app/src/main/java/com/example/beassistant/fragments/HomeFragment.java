package com.example.beassistant.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.beassistant.R;
import com.example.beassistant.adapters.HomeRecyclerAdapter;
import com.example.beassistant.controllers.MainActivity;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    View view;
    FloatingActionButton btn_filter;

    ArrayList<String> categories = new ArrayList<String>();
    AutoCompleteTextView select_category;
    ArrayAdapter<String> adapterItems;

    ArrayList<String> brands = new ArrayList<String>();
    AutoCompleteTextView select_brand;
    ArrayAdapter<String> adapterItems02;

    String selected_category = "Todos";
    String selected_brand = "Todos";

    AlertDialog dialog;

    // Creamos las variables necesarias para implementar el recyclerView
    ConstraintLayout constraintLayout;
    RecyclerView rV;
    HomeRecyclerAdapter recAdapter;

    // Declare the data base object
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creamos un objeto del recicler adapter
        recAdapter = new HomeRecyclerAdapter(getContext());

        // Generate the instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btn_filter = (FloatingActionButton) view.findViewById(R.id.btn_filter);

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categories = getCategories();
            }
        });

        //Asignamos a la variable rV el recyclerView
        rV = (RecyclerView) view.findViewById(R.id.recView);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rV.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private ArrayList getCategories(){

        ArrayList<String> auxArray = new ArrayList<>();

        db.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                auxArray.add(document.getId());
                            }
                            filter();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return auxArray;
    }

    private void filter(){

        AlertDialog.Builder ventana = new AlertDialog.Builder(getContext());

        ventana.setTitle("Filtrar");

        View v = getLayoutInflater().inflate(R.layout.filter_layout, null);

        //Select Category
        select_category = v.findViewById(R.id.select_category);

        adapterItems = new ArrayAdapter<String>(getContext(),R.layout.list_item,categories);
        select_category.setAdapter(adapterItems);

        select_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_category = parent.getItemAtPosition(position).toString();
                brands.clear();
                db.collection("/categorias/" + selected_category + "/marcas")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("Result", document.getId());
                                        brands.add(document.getId());
                                    }
                                } else {
                                    Log.d("Result", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        // Select Brand
        select_brand = v.findViewById(R.id.select_brand);

        adapterItems02 = new ArrayAdapter<String>(getContext(),R.layout.list_item02,brands);
        select_brand.setAdapter(adapterItems02);

        select_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_brand = parent.getItemAtPosition(position).toString();
            }
        });

        // Floating Button
        FloatingActionButton btn_check = (FloatingActionButton) v.findViewById(R.id.btn_check);

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recAdapter.productList.clear();

                db.collection("categorias/"+selected_category+"/marcas/"+selected_brand+"/productos")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc: task.getResult()) {
                                Product product = new Product(
                                        doc.getString("id"),
                                        doc.getString("name"),
                                        doc.getString("imgRef"),
                                        doc.getString("brand"),
                                        doc.getString("category"),
                                        doc.getString("type"),
                                        5
                                );
                                recAdapter.productList.add(product);
                                recAdapter.notifyDataSetChanged();
                            }

                            dialog.dismiss();
                        } else {
                            Log.d("Result", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        ventana.setView(v);

        dialog = ventana.create();

        dialog.show();
    }
}