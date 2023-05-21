package com.example.beassistant.fragments.mainpages;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.beassistant.adapters.ProductsRecyclerAdapter;
import com.example.beassistant.controllers.AddOpinionActivity;
import com.example.beassistant.controllers.logins.LoginController;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment{

    private SearchView searchView;

    ArrayList<Product> fullList;
    ArrayList<String> categories = new ArrayList<String>();
    AutoCompleteTextView select_category;
    ArrayAdapter<String> adapterItems;

    ArrayList<String> brands = new ArrayList<String>();
    AutoCompleteTextView select_brand;
    ArrayAdapter<String> adapterItems02;

    String selected_category = "Todos";
    String selected_brand = "Todos";

    // Declare the data base object
    private FirebaseFirestore db;

    ProductsRecyclerAdapter recAdapter;
    RecyclerView rV;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        categories = getCategories();

        recAdapter = new ProductsRecyclerAdapter(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        //Asignamos a la variable rV el recyclerView
        rV = (RecyclerView) view.findViewById(R.id.recyclerView);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rV.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);

        searchView = (SearchView) view.findViewById(R.id.searchView);

        //Select Category
        select_category = view.findViewById(R.id.select_category);

        adapterItems = new ArrayAdapter<String>(getContext(),R.layout.list_item,categories);
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
        select_brand = view.findViewById(R.id.select_brand);

        adapterItems02 = new ArrayAdapter<String>(getContext(),R.layout.list_item02,brands);
        select_brand.setAdapter(adapterItems02);

        select_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_brand = parent.getItemAtPosition(position).toString();
                recAdapter.productsList.clear();
                recAdapter.notifyDataSetChanged();

                try {
                    db.collection("categorias/" + selected_category + "/marcas/" + selected_brand + "/productos")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Product p = new Product(
                                                    document.getString("id"),
                                                    document.getString("name"),
                                                    document.getString("imgRef"),
                                                    document.getString("brand"),
                                                    document.getString("category"),
                                                    document.getString("type"),
                                                    0
                                            );

                                            recAdapter.productsList.add(p);
                                            recAdapter.notifyDataSetChanged();

                                            Log.d("Productos: ", recAdapter.productsList.toString());

                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                        fullList = recAdapter.productsList;
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Fallo: ", e.getMessage());
                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Log.d("Fallo: ", "Cancelado");
                                }
                            });
                }catch (Exception e){
                    Log.d("Fallo: ", e.getMessage());
                }


            }
        });

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

        return view;
    }

    private void filterList(String newText){

        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product p : recAdapter.productsList) {
            if (p.getName().toLowerCase().contains(newText.toLowerCase())){
                Log.d("Entra:","si");
                filteredList.add(p);
            }
        }

        if (!newText.equals("")){
            if (filteredList.isEmpty()){
                Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_LONG).show();
            }else {
                recAdapter.setFilteredList(filteredList);
            }
        }else {
            Log.d("Entra:","vacio" + fullList);
            recAdapter.setFilteredList(fullList);
        }



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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return auxArray;
    }
}