package com.example.beassistant.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.adapters.SimpleProductsRecyclerAdapter;
import com.example.beassistant.controllers.AddOpinionActivity;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddProductFragment extends Fragment {

    private SearchView searchView;

    ArrayList<Product> fullList;
    ArrayList<String> categories, brands = new ArrayList<String>();
    AutoCompleteTextView select_category, select_brand;
    ArrayAdapter<String> adapterItems, adapterItems02;

    String selected_category, selected_brand;

    // Declare the data base object
    private FirebaseFirestore db;

    SimpleProductsRecyclerAdapter recAdapter;
    RecyclerView rV;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDatabaseVariables();

        initVariables();
    }

    private void initDatabaseVariables() {
        // Generate the instance
        db = FirebaseFirestore.getInstance();
    }

    private void initVariables() {
        recAdapter = new SimpleProductsRecyclerAdapter(getContext());
        categories = getCategories();
        this.selected_category = "";
        this.selected_brand = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewConfiguration(view);

        initViewVariables(view);

        categorySelectorConfigurationAndListener();

        brandSelectorConfigurationAndListener();

        recyclerAdapterListener();

        searchViewListener();
    }

    private void searchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
    }

    private void recyclerAdapterListener() {
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int indice = 0;
                //Capturamos el indice del elemento seleccionado
                indice = rV.getChildAdapterPosition(view);

                Intent i = new Intent(getContext(), AddOpinionActivity.class);
                i.putExtra("id", recAdapter.productsList.get(indice).getUuID());
                i.putExtra("category", recAdapter.productsList.get(indice).getCategory());
                i.putExtra("brand", recAdapter.productsList.get(indice).getBrand());
                startActivity(i);

                //Indicamos que se ha seleccionado un elemento de la vista
                view.setSelected(true);
            }
        });
    }

    private void brandSelectorConfigurationAndListener() {
        adapterItems02 = new ArrayAdapter<String>(getContext(), R.layout.list_item02, brands);
        select_brand.setAdapter(adapterItems02);

        select_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_brand = parent.getItemAtPosition(position).toString();
                recAdapter.productsList.clear();
                recAdapter.notifyDataSetChanged();

                db.collection("categorias/" + selected_category + "/marcas/" + selected_brand + "/productos")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Product p = getProductWithDocument(document);

                                        recAdapter.productsList.add(p);
                                        recAdapter.notifyDataSetChanged();

                                    }
                                    // Set the full list
                                    fullList = recAdapter.productsList;
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @NonNull
    private static Product getProductWithDocument(QueryDocumentSnapshot document) {

        // Generate a product
        Product p = new Product(
                document.getString("id"),
                document.getString("name"),
                document.getString("imgRef"),
                document.getString("brand"),
                document.getString("category"),
                document.getString("type"),
                0
        );
        return p;
    }

    private void categorySelectorConfigurationAndListener() {

        adapterItems = new ArrayAdapter<String>(getContext(), R.layout.list_item02, categories);
        select_category.setAdapter(adapterItems);

        select_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_category = parent.getItemAtPosition(position).toString();
                recAdapter.productsList.clear();
                recAdapter.notifyDataSetChanged();
                brands.clear();
                db.collection("/categorias/" + selected_category + "/marcas")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    return;
                                }

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Add the brands to the array
                                    brands.add(document.getId());
                                }
                            }
                        });
            }
        });
    }

    private void initViewVariables(@NonNull View view) {
        searchView = (SearchView) view.findViewById(R.id.searchView);

        // Select Category
        select_category = view.findViewById(R.id.select_category);

        // Select Brand
        select_brand = view.findViewById(R.id.select_brand);
    }

    private void recyclerViewConfiguration(@NonNull View view) {
        // Asignamos a la variable rV el recyclerView
        rV = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rV.setLayoutManager(layoutManager);

        // Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);
    }

    private void filterList(String newText) {

        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product p : recAdapter.productsList) {
            if (!p.getName().toLowerCase().contains(newText.toLowerCase())) {
                continue;
            }

            filteredList.add(p);
        }

        if (newText.equals("")) {
            recAdapter.setFilteredList(fullList);
            return;
        }

        if (!filteredList.isEmpty()) {
            recAdapter.setFilteredList(filteredList);
            return;
        }

        Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_LONG).show();
    }


    private ArrayList getCategories() {

        ArrayList<String> auxArray = new ArrayList<>();

        db.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            auxArray.add(document.getId());
                        }
                    }
                });

        return auxArray;
    }
}