package com.example.beassistant.fragments.profile.myOpinions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beassistant.R;
import com.example.beassistant.adapters.OpinionsRecyclerAdapter;
import com.example.beassistant.adapters.ProductsRecyclerAdapter;
import com.example.beassistant.adapters.SimpleProductsRecyclerAdapter;
import com.example.beassistant.fragments.home.OpinionsFragment;
import com.example.beassistant.models.Opinion;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MyOpinionsFragment extends Fragment {

    AlertDialog dialog;

    // Creamos las variables necesarias para implementar el recyclerView
    ConstraintLayout constraintLayout;
    RecyclerView rV;
    SimpleProductsRecyclerAdapter recAdapter;

    // Declare the data base object
    private FirebaseFirestore db;

    public MyOpinionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();

        getDataFromLastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_opinions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the variables
        initViewVariables(view);

        setReciclerAdapter();

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(v);

                Fragment fragment = new MyOpinionsDetails();
                Bundle args = new Bundle();
                args.putString("id", recAdapter.productsList.get(index).getUuID());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("keyOpinions", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Get the data from last fragment
     */
    private void getDataFromLastFragment(){
        getParentFragmentManager().setFragmentResultListener("myOpinions", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtain the follower id
                String userId = result.getString("userId");
                String category = result.getString("category");

                // Get the user
                fillRecyclerAdapter(userId, category);
            }
        });
    }

    /**
     * Fill the recycler adapter with the user id and category
     * @param userId
     */
    private void fillRecyclerAdapter(String userId, String category){
        // Search the user in the database
        db.collection("opiniones")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            return;
                        }

                        // Loop the result
                        for (DocumentSnapshot document : task.getResult()) {

                            // Check the user id to get their opinion products
                            if (!document.getString("userId").equals(userId)) {
                                continue;
                            }

                            // Check if the product category are the same
                            if (!document.getString("productCategory").equals(category.toLowerCase())){
                                continue;
                            }

                            // Get all the products
                            db.collectionGroup("productos")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            // Loop all the products
                                            for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                                                // Check if the
                                                if (doc.getId().equals(document.getString("productId"))){
                                                    Product product = new Product(
                                                            doc.getString("id"),
                                                            doc.getString("name"),
                                                            doc.getString("imgRef"),
                                                            doc.getString("brand"),
                                                            doc.getString("category"),
                                                            doc.getString("type"),
                                                            Math.round(doc.getDouble("rating") * 100.0) / 100.0
                                                    );
                                                    recAdapter.productsList.add(product);
                                                    recAdapter.notifyDataSetChanged();
                                                }

                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Set the recycler view variables
     */
    private void setReciclerAdapter(){

        // Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rV.setLayoutManager(layoutManager);

        // Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);
    };

    /**
     * Init the varibles
     */
    private void initVariables(){
        // Init the database controller
        db = FirebaseFirestore.getInstance();

        // Init the recicler adapter
        recAdapter = new SimpleProductsRecyclerAdapter(getContext());
    }

    /**
     * Init the view variables
     * @param view
     */
    private void initViewVariables(View view){
        rV = (RecyclerView) view.findViewById(R.id.recycler_view_my_opinions);
    }
}