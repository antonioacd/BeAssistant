package com.example.beassistant.fragments.mainpages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.example.beassistant.adapters.HomeRecyclerAdapter;
import com.example.beassistant.adapters.ProfileRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.grpc.SynchronizationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    //Recycler view variables
    View view;
    RecyclerView rvCategories;
    ProfileRecyclerAdapter recAdapter;

    //Profile parameters
    ImageView img_profile;
    TextView txt_username;
    TextView txt_name;

    FirebaseStorage storage;
    StorageReference storageRef;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        //Creamos un objeto del recicler adapter
        recAdapter = new ProfileRecyclerAdapter(getContext());
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        cargarFoto();

        img_profile = view.findViewById(R.id.img_profile);
        txt_username = view.findViewById(R.id.txt_username);
        txt_name = view.findViewById(R.id.txt_name);

        txt_username.setText(Shared.myUser.getUsername());
        txt_name.setText(Shared.myUser.getName());

        //Asignamos a la variable rV el recyclerView
        rvCategories = (RecyclerView) view.findViewById(R.id.rv_clasification);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rvCategories.setAdapter(recAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void cargarFoto(){

        storageRef.child(Shared.myUser.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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


}