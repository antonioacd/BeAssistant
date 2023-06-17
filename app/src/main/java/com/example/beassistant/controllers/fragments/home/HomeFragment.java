package com.example.beassistant.controllers.fragments.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.beassistant.adapters.ProductsRecyclerAdapter;
import com.example.beassistant.controllers.barcode.CaptureActivityPortraint;
import com.example.beassistant.controllers.main.MainActivity;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FloatingActionButton btn_filter, btn_scan, btn_refresh;

    ArrayList<String> categories = new ArrayList<String>();
    AutoCompleteTextView select_category;
    ArrayAdapter<String> adapterItems;

    ArrayList<String> brands = new ArrayList<String>();
    AutoCompleteTextView select_brand;
    ArrayAdapter<String> adapterItems02;

    String selected_category = "";
    String selected_brand = "";

    private AlertDialog dialog;

    // Creamos las variables necesarias para implementar el recyclerView
    ConstraintLayout constraintLayout;
    RecyclerView rV;
    ProductsRecyclerAdapter recAdapter;

    // Declare the data base object
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creamos un objeto del recicler adapter
        recAdapter = new ProductsRecyclerAdapter(getContext());

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        getAllProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void scancode() {

        // Iniciar el escaneo del código de barras
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Cámara trasera por defecto
        integrator.setBeepEnabled(false); // Desactivar el sonido de escaneo
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortraint.class);
        integrator.initiateScan();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((MainActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_filter = (FloatingActionButton) view.findViewById(R.id.btn_filter);
        btn_scan = (FloatingActionButton) view.findViewById(R.id.btn_scan);
        btn_refresh = (FloatingActionButton) view.findViewById(R.id.btn_refresh);

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categories = getCategories();
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scancode();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllProducts();
                btn_refresh.setVisibility(View.INVISIBLE);
            }
        });

        //Asignamos a la variable rV el recyclerView
        rV = (RecyclerView) view.findViewById(R.id.recycler_view_my_opinions);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rV.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);

        // Set a listener to the recycler adapter items
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(view);

                Fragment fragment = new DetailsProductFragment();
                Bundle args = new Bundle();
                args.putString("id", recAdapter.productList.get(index).getProductId());
                args.putString("name", recAdapter.productList.get(index).getProductName());
                args.putString("brand", recAdapter.productList.get(index).getBrand());
                args.putString("type", recAdapter.productList.get(index).getType());
                args.putDouble("mediaRating", recAdapter.productList.get(index).getMediaRating());
                args.putString("imgRef", recAdapter.productList.get(index).getImgReference());
                args.putString("url", recAdapter.productList.get(index).getShopUrl());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("keyProduct", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Set the view selected as true
                view.setSelected(true);
            }
        });
    }

    private void getAllProducts(){

        recAdapter.productList.clear();
        recAdapter.notifyDataSetChanged();

        db.collectionGroup("productos").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("Query:", "Entra");
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                            Log.d("Query:", doc.getString("name"));

                            Product product = new Product(
                                    doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("imgRef"),
                                    doc.getString("brand"),
                                    doc.getString("category"),
                                    doc.getString("type"),
                                    doc.getDouble("rating"),
                                    doc.getString("url")
                            );
                            recAdapter.productList.add(product);
                            recAdapter.notifyDataSetChanged();
                        }

                    }
                });
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

        selected_category = "";
        selected_brand = "";

        brands.clear();

        AlertDialog.Builder ventana = new AlertDialog.Builder(getContext());

        ventana.setTitle("Filtrar");

        View v = getLayoutInflater().inflate(R.layout.filter_layout, null);

        //Select Category
        select_category = v.findViewById(R.id.select_category);

        adapterItems = new ArrayAdapter<String>(getContext(),R.layout.list_item02,categories);
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

                if (selected_category.isEmpty() || selected_brand.isEmpty()){
                    dialog.dismiss();
                    return;
                }

                db.collection("categorias/"+selected_category+"/marcas/"+selected_brand+"/productos")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            recAdapter.productList.clear();

                            for (DocumentSnapshot doc: task.getResult()) {

                                Product product = new Product(
                                        doc.getString("id"),
                                        doc.getString("name"),
                                        doc.getString("imgRef"),
                                        doc.getString("brand"),
                                        doc.getString("category"),
                                        doc.getString("type"),
                                        doc.getDouble("rating"),
                                        doc.getString("url")
                                );
                                recAdapter.productList.add(product);
                            }

                            recAdapter.notifyDataSetChanged();

                            btn_refresh.setVisibility(View.VISIBLE);
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