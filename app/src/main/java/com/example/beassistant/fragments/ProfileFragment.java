package com.example.beassistant.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // Declare the database controller
    FirebaseFirestore db;

    //Recycler view variables
    View view;
    RecyclerView rvCategories;
    ProfileRecyclerAdapter recAdapter;

    //Profile parameters
    ImageView img_profile;
    TextView txt_username;
    TextView txt_name;
    TextView txt_numOpinions;
    TextView txt_numFollowers;
    TextView txt_numFollowing;

    FirebaseStorage storage;
    StorageReference storageRef;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init the database controller
        db = FirebaseFirestore.getInstance();

        // Init the recicler adapter
        recAdapter = new ProfileRecyclerAdapter(getContext());

        // Init the storage
        storage = FirebaseStorage.getInstance();

        // Set the storage ref
        storageRef = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get my user
        getMyUser();

        img_profile = view.findViewById(R.id.img_profile);
        txt_username = view.findViewById(R.id.txt_username);
        txt_name = view.findViewById(R.id.txt_name);
        txt_numOpinions = view.findViewById(R.id.txt_num_opinions);
        txt_numFollowers = view.findViewById(R.id.txt_num_followers);
        txt_numFollowing = view.findViewById(R.id.txt_num_following);

        //Asignamos a la variable rV el recyclerView
        rvCategories = (RecyclerView) view.findViewById(R.id.rv_clasification);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rvCategories.setAdapter(recAdapter);

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

        // Inflate the layout for this fragment
        return view;
    }

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

    private void getMyUser(){
        // Search the user in the database
        db.collection("users")
                .document(Shared.myUser.getId())
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

                        txt_username.setText(document.getString("username"));
                        txt_name.setText(document.getString("name"));
                        txt_numOpinions.setText(String.valueOf(document.getDouble("numOpiniones").intValue()));
                        txt_numFollowers.setText(String.valueOf(document.getDouble("numSeguidores").intValue()));
                        txt_numFollowing.setText(String.valueOf(document.getDouble("numSeguidos").intValue()));
                        cargarFoto(document.getString("imgRef"));
                    }
                });
    }


}