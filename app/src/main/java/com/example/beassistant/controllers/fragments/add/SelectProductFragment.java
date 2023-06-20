package com.example.beassistant.controllers.fragments.add;

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
import com.example.beassistant.models.Shared;
import com.example.beassistant.adapters.SimpleProductsRecyclerAdapter;
import com.example.beassistant.controllers.addOpinion.AddOpinionActivity;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SelectProductFragment extends Fragment {

    private SearchView searchView;

    private ArrayList<Product> fullList;

    private ArrayList<String> categories, brands = new ArrayList<String>();

    private AutoCompleteTextView select_category, select_brand;

    private ArrayAdapter<String> adapterItems, adapterItems02;

    private String selected_category, selected_brand;

    private FloatingActionButton btn_refresh;

    private FirebaseFirestore db;

    private SimpleProductsRecyclerAdapter recAdapter;
    
    private RecyclerView rV;

    public SelectProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init the variables
        initVariables();
    }

    /**
     * Function to init the variables
     */
    private void initVariables() {

        // Generate the instance of database
        db = FirebaseFirestore.getInstance();

        // Set the recycler adapter
        recAdapter = new SimpleProductsRecyclerAdapter(getContext());

        // Set the categories
        categories = getCategories();

        // Set the selected category and selected brand
        this.selected_category = "";
        this.selected_brand = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configure the recycler view
        recyclerViewConfiguration(view);

        // Init the view variables
        initViewVariables(view);

        // Get the products
        getAllProducts();

        // Listener and configuration of category selector
        categorySelectorConfigurationAndListener();

        // Listener and configuration of brand selector
        brandSelectorConfigurationAndListener();

        // Listener of button refresh
        buttonRefreshListener();

        // Listener of recycler adapter
        recyclerAdapterListener();

        // Listener of search view
        searchViewListener();
    }

    /**
     * Listener of button refresh
     */
    private void buttonRefreshListener() {
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllProducts();
                btn_refresh.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Function to get all the products
     */
    private void getAllProducts(){

        // Init the variables to empty
        selected_category = "";
        selected_brand = "";

        // Clear the recycler adapter list
        recAdapter.productsList.clear();
        recAdapter.notifyDataSetChanged();

        // Get the products from the database
        db.collectionGroup("productos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // Loop the documents
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {

                            // Generate a product
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
                            recAdapter.productsList.add(product);
                            recAdapter.notifyDataSetChanged();
                        }
                        // Set the full list
                        fullList = recAdapter.productsList;
                    }
                });
    }

    /**
     * Listener of the search view
     */
    private void searchViewListener() {
        // Set the listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Filter the list with the string
                filterList(s);
                return true;
            }
        });
    }

    /**
     * Listener of the recycler adapter
     */
    private void recyclerAdapterListener() {
        // Set the adapter
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfYouOpined(view);
            }
        });
    }

    /**
     * Function to check if you have make an opinion
     * @param view
     */
    private void checkIfYouOpined(View view) {
        // Get the opinions
        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Check the task
                if (!task.isSuccessful()){
                    return;
                }

                int indice = 0;
                // Get the index
                indice = rV.getChildAdapterPosition(view);

                // Get the product id
                String productId = recAdapter.productsList.get(indice).getProductId();

                // Loop the result
                for (DocumentSnapshot doc : task.getResult()) {

                    // Check the product id
                    if (!doc.getString("productId").equals(productId)){
                        continue;
                    }

                    // Check the user id
                    if(!doc.getString("userId").equals(Shared.myUser.getUserId())){
                        continue;
                    }

                    // Notify
                    Toast.makeText(getContext(), "Ya has opinado de este producto", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create a new intent
                Intent i = new Intent(getContext(), AddOpinionActivity.class);

                // Put the extra info to the intent
                i.putExtra("id", recAdapter.productsList.get(indice).getProductId());
                i.putExtra("category", recAdapter.productsList.get(indice).getCategory());
                i.putExtra("brand", recAdapter.productsList.get(indice).getBrand());

                // Start the intent
                startActivity(i);

                // Set the view
                view.setSelected(true);
            }
        });
    }

    /**
     * Funtion thah contains the configuration and listener of brand selector
     */
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
                                    }

                                    recAdapter.notifyDataSetChanged();

                                    // Set the full list
                                    fullList = recAdapter.productsList;
                                    btn_refresh.setVisibility(View.VISIBLE);
                                } else {

                                }
                            }
                        });
            }
        });
    }

    /**
     * Funtion to get a product with a document
     * @param doc
     * @return
     */
    private Product getProductWithDocument(QueryDocumentSnapshot doc) {

        // Generate a product
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
        return product;
    }

    /**
     * Funtion thah contains the configuration and listener of category selector
     */
    private void categorySelectorConfigurationAndListener() {

        adapterItems = new ArrayAdapter<String>(getContext(), R.layout.list_item02, categories);
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
                                    // Add the brands to the array
                                    brands.add(document.getId());
                                }
                            }
                        });
            }
        });
    }

    /**
     * Function to init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        searchView = (SearchView) view.findViewById(R.id.searchView);

        // Select Category
        select_category = view.findViewById(R.id.select_category);

        // Select Brand
        select_brand = view.findViewById(R.id.select_brand);

        btn_refresh = (FloatingActionButton) view.findViewById(R.id.btn_refresh_02);
    }

    /**
     * Function to set the recycler view configuration
     * @param view
     */
    private void recyclerViewConfiguration(@NonNull View view) {
        // Set the recycler view
        rV = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Create the layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout manager
        rV.setLayoutManager(layoutManager);

        // set the adapter
        rV.setAdapter(recAdapter);
    }

    /**
     * Function to filter the list
     * @param newText
     */
    private void filterList(String newText) {

        // Create a new aux list
        ArrayList<Product> filteredList = new ArrayList<>();

        // Loop the product list
        for (Product p : recAdapter.productsList) {

            // Check if the name of the product contains the text
            if (!p.getProductName().toLowerCase().contains(newText.toLowerCase())) {
                continue;
            }

            // Add the product to the filtered list
            filteredList.add(p);
        }

        // Check if search bar is empty
        if (newText.equals("")) {

            // Set the filtered list
            recAdapter.setFilteredList(fullList);
            return;
        }

        // Check if filtered list is empty
        if (!filteredList.isEmpty()) {

            // Set the filtered list
            recAdapter.setFilteredList(filteredList);
            return;
        }

        // Notify
        Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_LONG).show();
    }

    /**
     * Function to get the categories
     * @return
     */
    private ArrayList getCategories() {

        // Create the aux array
        ArrayList<String> auxArray = new ArrayList<>();

        // Get the categories from the database
        db.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Check te task
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Loop the results
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Add the id to the aux array
                            auxArray.add(document.getId());
                        }
                    }
                });

        return auxArray;
    }
}