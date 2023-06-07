package com.example.beassistant.fragments.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.example.beassistant.adapters.ProfileRecyclerAdapter;
import com.example.beassistant.fragments.profile.myOpinions.MyOpinionsList;
import com.example.beassistant.models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // Declare the database controller
    FirebaseFirestore db;

    // Recycler view variables
    RecyclerView rvCategories;
    ProfileRecyclerAdapter recAdapter;

    // Profile parameters
    ImageView img_profile;
    TextView txt_username;
    TextView txt_name;
    TextView txt_numOpinions;
    TextView txt_numFollowers;
    TextView txt_numFollowing;

    // Declare the storage controllers and reference
    FirebaseStorage storage;
    StorageReference storageRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init the variables
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the variables
        initViewVariables(view);

        // Configure the recicler aadpter
        setReciclerAdapter();

        // Get the categories
        getCategories();

        // Get my user
        getMyUser();

        // Set the listeners
        listeners();
    }

    /**
     * Set the recycler view variables
     */
    private void setReciclerAdapter(){

        // Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);

        // Implementamos el recyclerAdapter en el recyclerView
        rvCategories.setAdapter(recAdapter);
    };

    /**
     * Init the varibles
     */
    private void initVariables(){
        // Init the database controller
        db = FirebaseFirestore.getInstance();

        // Init the recicler adapter
        recAdapter = new ProfileRecyclerAdapter(getContext());

        // Init the storage
        storage = FirebaseStorage.getInstance();

        // Set the storage ref
        storageRef = storage.getReference();
    }

    /**
     * Init the view variables
     * @param view
     */
    private void initViewVariables(View view){

        rvCategories = (RecyclerView) view.findViewById(R.id.rv_clasification);
        img_profile = view.findViewById(R.id.img_profile);
        txt_username = view.findViewById(R.id.txt_username);
        txt_name = view.findViewById(R.id.txt_name);
        txt_numOpinions = view.findViewById(R.id.txt_num_opinions);
        txt_numFollowers = view.findViewById(R.id.txt_num_followers);
        txt_numFollowing = view.findViewById(R.id.txt_num_following);
    }

    /**
     * Set the listeners
     */
    private void listeners(){

        // Number of followers
        txt_numFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("id", Shared.myUser.getId());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // Number of following
        txt_numFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingFragment();
                Bundle args = new Bundle();
                args.putString("id", Shared.myUser.getId());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                // Get the index
                index = rvCategories.getChildAdapterPosition(v);

                Fragment fragment = new MyOpinionsList();
                Bundle args = new Bundle();
                args.putString("userId", Shared.myUser.getId());
                args.putString("category", recAdapter.categoryList.get(index).getCategory_name());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("myOpinions", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Get the categories
     */
    private void getCategories(){

        // Clear the category list
        recAdapter.categoryList.clear();
        recAdapter.notifyDataSetChanged();

        ArrayList<String> auxCategories = new ArrayList();

        // Loop all the categories
        db.collection("categorias").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Check if task is successful
                if (!task.isSuccessful()){
                    return;
                }

                // Loop the docs of categorias
                for (QueryDocumentSnapshot categoriesDoc : task.getResult()) {
                    // Add the categories to an aux array list
                    auxCategories.add(categoriesDoc.getId());
                }

                // Get the opinions
                getOpinionsFromDatabase(auxCategories);
            }
        });
    }

    private void getOpinionsFromDatabase(ArrayList<String> categories) {
        // Get the opinions
        db.collection("opiniones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Check if task is successful
                if (!task.isSuccessful()){
                    return;
                }

                // Declare the index to count the opinions
                int index = 0;

                // Loop the categories to found the occurrences
                loopAuxCategories(task, categories);
            }
        });
    }

    private void loopAuxCategories(@NonNull Task<QuerySnapshot> task, ArrayList<String> categories) {
        int index;
        // Loop the aux array list of categories
        for (String category : categories) {

            // Reset the index
            index = 0;

            // Check if the category are contain yet
            if (!categories.contains(category)){
                continue;
            }

            // Get the names of final categories and the number of products the user has reviewed in those categories
            getCategoriesAndNumber(task, index, category);
        }
    }

    private void getCategoriesAndNumber(@NonNull Task<QuerySnapshot> task, int index, String category) {
        // Loop the opinions doc
        for (QueryDocumentSnapshot opinionsDoc : task.getResult()) {

            // Check if the opinion are made for the current user
            if (!opinionsDoc.getString("userId").equals(Shared.myUser.getId())){
                continue;
            }

            // Check if the product category is the same that the category that we are looping
            if (!opinionsDoc.getString("productCategory").equals(category)){
                continue;
            }

            // Increase the index
            index++;
        }

        // Create the category
        Category cat = new Category(category, String.valueOf(index));

        // Add the category to the category list of the recycler adapter
        recAdapter.categoryList.add(cat);
        recAdapter.notifyDataSetChanged();
    }

    /**
     * Get the foto
     * @param imgRef
     */
    private void cargarFoto(String imgRef){

        storageRef.child(imgRef).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img_profile.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    /**
     * Get my user
     */
    private void getMyUser(){
        // Search the user in the database
        db.collection("users")
                .document(Shared.myUser.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // Get the document
                        DocumentSnapshot document = task.getResult();

                        // Check if the document exists
                        if (!document.exists()) {
                            return;
                        }

                        txt_username.setText(document.getString("username"));
                        txt_name.setText(document.getString("name"));
                        txt_numOpinions.setText(String.valueOf(document.getDouble("numOpiniones").intValue()));
                        txt_numFollowers.setText(String.valueOf(document.getDouble("numSeguidores").intValue()));
                        txt_numFollowing.setText(String.valueOf(document.getDouble("numSeguidos").intValue()));
                        cargarFoto(document.getString("imgRef"));

                        db.collection("opiniones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                // Chek if the task is successful
                                if (!task.isSuccessful()) {
                                    return;
                                }

                                int index = 0;

                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (doc.getString("userId").equals(Shared.myUser.getId())){
                                        index++;
                                    }
                                }

                                txt_numOpinions.setText(index+"");
                            }
                        });
                    }
                });
    }
}