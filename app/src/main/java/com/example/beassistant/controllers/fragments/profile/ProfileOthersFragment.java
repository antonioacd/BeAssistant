package com.example.beassistant.controllers.fragments.profile;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.models.Shared;
import com.example.beassistant.adapters.ProfileRecyclerAdapter;
import com.example.beassistant.controllers.fragments.profile.othersOpinion.OthersOpinionsList;
import com.example.beassistant.models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileOthersFragment extends Fragment {

    // Declare the data base object
    private FirebaseFirestore db;

    // Declarer the storage objects
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Profile parameters
    private Button btn_follow;
    private ImageView img_profile;
    private TextView txt_username, txt_name, txt_numOpinions, txt_numFollowers, txt_numFollowing;
    private String id;
    private boolean following = false;

    // Recycler view variables
    RecyclerView rvCategories;
    ProfileRecyclerAdapter recAdapter;

    private String userId = "";

    public ProfileOthersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();

        checkFollow();

        getDataFromLastFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDataFromLastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_others, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the variables
        initViewVariables(view);

        // Get the number of opinions
        getNumberOfOpinions();

        // Get the categories
        getCategories();

        // Set the listeners
        listeners();

        // Set the recycler adapter
        setRecyclerAdapter();
    }

    /**
     * Init the variables
     */
    private void initVariables(){
        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Init the recicler adapter
        recAdapter = new ProfileRecyclerAdapter(getContext());

        // Init the storage
        storage = FirebaseStorage.getInstance();

        // Set the storage ref
        storageRef = storage.getReference();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the data from last fragment
     */
    private void getDataFromLastFragment(){

        if (!userId.isEmpty()) {
            getUser(userId);
            return;
        }

        getParentFragmentManager().setFragmentResultListener("follower", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtain the follower id
                userId = result.getString("id");
                getUser(userId);
            }
        });
    }

    /**
     * Set the recycler adapter
     */
    private void setRecyclerAdapter(){
        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rvCategories.setAdapter(recAdapter);
    }

    /**
     * Init the view variables
     * @param view
     */
    private void initViewVariables(View view){

        rvCategories = (RecyclerView) view.findViewById(R.id.rv_clasification_02);
        btn_follow = view.findViewById(R.id.btn_follow);
        img_profile = view.findViewById(R.id.img_profile_02);
        txt_username = view.findViewById(R.id.txt_username_02);
        txt_name = view.findViewById(R.id.txt_name_02);
        txt_numOpinions = view.findViewById(R.id.txt_num_opinions_02);
        txt_numFollowers = view.findViewById(R.id.txt_num_followers_02);
        txt_numFollowing = view.findViewById(R.id.txt_num_following_02);
    }

    /**
     * Set the listeners
     */
    private void listeners(){
        txt_numFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("id", id);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        txt_numFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingFragment();
                Bundle args = new Bundle();
                args.putString("id", id);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            // Set their user in mi following
            @Override
            public void onClick(View view) {
                btn_follow.setEnabled(false);
                if(!following){
                    // Follow the user
                    followUser();
                    return;
                }

                // Unfollow the user
                unfollowUser();
            }
        });

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                // Get the index
                index = rvCategories.getChildAdapterPosition(v);

                Fragment fragment = new OthersOpinionsList();
                Bundle args = new Bundle();
                args.putString("userId", id);
                args.putString("category", recAdapter.categoryList.get(index).getCategory_name());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("OthersOpinions", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Get the photo
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
     * Get the number of opinions
     */
    private void getNumberOfOpinions() {
        db.collection("opiniones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Chek if the task is successful
                if (!task.isSuccessful()) {
                    return;
                }

                int index = 0;

                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.getString("userId").equals(userId)){
                        index++;
                    }
                }

                txt_numOpinions.setText(index+"");
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

    /**
     * Function to get the opinions from the databse
     * @param categories
     */
    private void getOpinionsFromDatabase(ArrayList<String> categories) {
        // Get the opinions
        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Check if task is successful
                if (!task.isSuccessful()){
                    return;
                }

                // Loop the categories to found the occurrences
                loopAuxCategories(task, categories);
            }
        });
    }

    /**
     * Function to loop the aux categories
     * @param task
     * @param categories
     */
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

    /**
     * Function to get categories and number of categories
     * @param task
     * @param index
     * @param category
     */
    private void getCategoriesAndNumber(@NonNull Task<QuerySnapshot> task, int index, String category) {
        // Loop the opinions doc
        for (QueryDocumentSnapshot opinionsDoc : task.getResult()) {

            // Check if the opinion are made for the current user
            if (!opinionsDoc.getString("userId").equals(id)){
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
     * Check if you follow the user
     */
    private void checkFollow(){

        following = false;

        db.collection("users/"+Shared.myUser.getUserId()+"/seguidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Check if the task is successful
                        if (!task.isSuccessful()){
                            return;
                        }

                        // Loop all the docs of the result
                        for (QueryDocumentSnapshot doc: task.getResult()) {

                            // Check if is the same id
                            if (doc.getId().equals(id)){
                                following = true;
                            }
                        }

                        ColorStateList followColor = ColorStateList.valueOf(getResources().getColor(R.color.follow));
                        ColorStateList unfollowColor = ColorStateList.valueOf(getResources().getColor(R.color.unfollow));

                        btn_follow.setBackgroundTintList((following) ?  unfollowColor : followColor);
                        btn_follow.setText((following) ? "Siguiendo" : "Seguir");

                        btn_follow.setEnabled(true);
                    }
                });
    }

    /**
     * Get User with id
     * @param userId
     */
    private void getUser(String userId){
        // Search the user in the database
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // Chek if the task is successful
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get the document
                        DocumentSnapshot document = task.getResult();

                        // Check if the document exists
                        if (!document.exists()) {
                            return;
                        }

                        id = document.getId();
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
                                    if (doc.getString("userId").equals(userId)){
                                        index++;
                                    }
                                }
                                txt_numOpinions.setText(index+"");
                            }
                        });

                        checkFollow();
                    }
                });
    }

    /**
     * Follow an user
     */
    private void followUser(){
        btn_follow.setEnabled(false);
        Map<String, Object> object = new HashMap<>();
        object.put("id", id);
        db.collection("users/"+Shared.myUser.getUserId()+"/seguidos/")
                .document(id)
                .set(object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btn_follow.setText("Siguendo");
                        Map<String, Object> objecto02 = new HashMap<>();
                        objecto02.put("id", id);

                        // Set my user in thir followers
                        db.collection("users/"+id+"/seguidores/")
                                .document(Shared.myUser.getUserId())
                                .set(objecto02)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        // Add 1 to their number of followers
                                        db.collection("users")
                                                .document(id)
                                                .update("numSeguidores", FieldValue.increment(1))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        // Add 1 to mi number of following
                                                        db.collection("users")
                                                                .document(Shared.myUser.getUserId())
                                                                .update("numSeguidos", FieldValue.increment(1))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        getUser(id);
                                                                        checkFollow();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    /**
     * Unfollow an user
     */
    private void unfollowUser(){
        btn_follow.setEnabled(false);
        Map<String, Object> object = new HashMap<>();
        object.put("id", id);
        // Delete the user from mi following
        db.collection("users/"+Shared.myUser.getUserId()+"/seguidos/")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btn_follow.setText("Seguir");
                        Map<String, Object> objecto02 = new HashMap<>();
                        objecto02.put("id", id);

                        // Delete my user from their followers
                        db.collection("users/"+id+"/seguidores/")
                                .document(Shared.myUser.getUserId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        // Rest 1 to their number of followers
                                        db.collection("users")
                                                .document(id)
                                                .update("numSeguidores", FieldValue.increment(-1))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        // Rest 1 to mi number of following
                                                        db.collection("users")
                                                                .document(Shared.myUser.getUserId())
                                                                .update("numSeguidos", FieldValue.increment(-1))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        getUser(id);
                                                                        checkFollow();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
}
