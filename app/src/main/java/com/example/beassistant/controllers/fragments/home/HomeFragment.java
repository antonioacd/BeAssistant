package com.example.beassistant.controllers.fragments.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    private FloatingActionButton btn_filter, btn_scan, btn_refresh;

    private ArrayList<String> categories = new ArrayList<String>();

    private AutoCompleteTextView select_category;

    private ArrayAdapter<String> adapterItems;

    private ArrayList<String> brands = new ArrayList<String>();

    private AutoCompleteTextView select_brand;

    private ArrayAdapter<String> adapterItems02;

    private String selected_category = "";

    private String selected_brand = "";

    private AlertDialog dialog;

    private RecyclerView rV;

    private ProductsRecyclerAdapter recAdapter;

    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init the variables
        initVariables();

        // Get all products
        getAllProducts();
    }

    private void initVariables() {
        // Creamos un objeto del recicler adapter
        recAdapter = new ProductsRecyclerAdapter(getContext());

        // Generate the instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    /**
     * Function to configure the scancode
     */
    private void scancodeConfiguration() {

        // Create the integrator
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        // Set the format
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        // Set the prompt
        integrator.setPrompt("Escanea un código de barras");
        // Select the camera
        integrator.setCameraId(0);
        // Disabled the beep
        integrator.setBeepEnabled(false);
        // Disabled barcode image
        integrator.setBarcodeImageEnabled(true);
        // Set orientation locked to false
        integrator.setOrientationLocked(false);
        // Set the capture activity
        integrator.setCaptureActivity(CaptureActivityPortraint.class);
        // Init the scan
        integrator.initiateScan();
    }

    /**
     * Function to get the barcode activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((MainActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init view variables
        initViewVariables(view);

        // Set the button filter listener
        buttonFilterListener();

        // Set the button scan listener
        buttonScanListener();

        // Set the button refresh listener
        buttonRefreshListener();

        // Set the recycler view configuration
        recyclerViewConfiguration();

        // Set the recycler adapter listener
        recyclerAdapterListener();
    }

    /**
     * Function to set the recycler adapter listener
     */
    private void recyclerAdapterListener() {
        // Set a listener to the recycler adapter items
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(view);

                // Create the fragment
                Fragment fragment = new DetailsProductFragment();

                // Set the arguments
                Bundle args = new Bundle();
                args.putString("id", recAdapter.productList.get(index).getProductId());
                args.putString("name", recAdapter.productList.get(index).getProductName());
                args.putString("brand", recAdapter.productList.get(index).getBrand());
                args.putString("type", recAdapter.productList.get(index).getType());
                args.putDouble("mediaRating", recAdapter.productList.get(index).getMediaRating());
                args.putString("imgRef", recAdapter.productList.get(index).getImgReference());
                args.putString("url", recAdapter.productList.get(index).getShopUrl());

                // Set the listener
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

    /**
     * Set the recycler view configuration
     */
    private void recyclerViewConfiguration() {
        // Create the linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout manager
        rV.setLayoutManager(layoutManager);

        // Set the adapter
        rV.setAdapter(recAdapter);
    }

    /**
     * Function to set the button refresh listener
     */
    private void buttonRefreshListener() {
        // Set the listener
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get all the products
                getAllProducts();
                // Set the btn_refresh invisible
                btn_refresh.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Function to set the button scan listener
     */
    private void buttonScanListener() {
        // Set the listener
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the scancode
                scancodeConfiguration();
            }
        });
    }

    /**
     * Function to set the button filter listener
     */
    private void buttonFilterListener() {
        // Set the listener
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the categories
                categories = getCategories();
            }
        });
    }

    /**
     * Init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        btn_filter = (FloatingActionButton) view.findViewById(R.id.btn_filter);
        btn_scan = (FloatingActionButton) view.findViewById(R.id.btn_scan);
        btn_refresh = (FloatingActionButton) view.findViewById(R.id.btn_refresh);
        rV = (RecyclerView) view.findViewById(R.id.recycler_view_my_opinions);
    }

    /**
     * Function to get all the products
     */
    private void getAllProducts(){

        // Clear the recycler adapter product list
        recAdapter.productList.clear();
        recAdapter.notifyDataSetChanged();

        // Get the products
        db.collectionGroup("productos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // Loop the documents
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {

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
                            // Add the product to the list
                            recAdapter.productList.add(product);
                            recAdapter.notifyDataSetChanged();
                        }

                    }
                });
    }

    /**
     * Function to get the categories
     * @return
     */
    private ArrayList getCategories(){

        // Create aux array
        ArrayList<String> auxArray = new ArrayList<>();

        // Get the categories
        db.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Check the task
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Loop the result
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add the document id to the aux array
                            auxArray.add(document.getId());
                        }

                        // Open the filter dialogz
                        openFilterDialog();
                    }
                });

        return auxArray;
    }

    /**
     * Dialog to open the filter
     */
    private void openFilterDialog(){

        selected_category = "";
        selected_brand = "";

        // Clear the brands
        brands.clear();

        // Create the window
        AlertDialog.Builder window = new AlertDialog.Builder(getContext());

        // Set the tittle
        window.setTitle("Filtrar");

        // Get the view
        View v = getLayoutInflater().inflate(R.layout.filter_layout, null);

        categorySelectorConfiguration(v);

        brandSelectorConfiguration(v);

        buttonCheckConfiguration(v);

        window.setView(v);

        dialog = window.create();

        dialog.show();
    }

    /**
     * Function to configure the check button
     * @param v
     */
    private void buttonCheckConfiguration(View v) {
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
                        if (!task.isSuccessful()) {

                        }
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

                    }
                });
            }
        });
    }

    /**
     * Function to configure the brand selector
     * @param v
     */
    private void brandSelectorConfiguration(View v) {
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
    }

    /**
     * Function to configure the category selector
     * @param v
     */
    private void categorySelectorConfiguration(View v) {
        // Select Category
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
                                if (!task.isSuccessful()) {
                                    return;
                                }

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    brands.add(document.getId());
                                }
                            }
                        });
            }
        });
    }
}