package com.example.beassistant.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beassistant.R;
import com.example.beassistant.models.Opinion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsOpinionFragment extends Fragment {

    // Declare the data base controller
    private FirebaseFirestore db;

    // Declare the data base storage controller
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public DetailsOpinionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("keyOpinion", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                // Obtains the followers id
                getData(result);
            }
        });

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Generate the storage instance
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_opinion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    private void getData(Bundle result){

        // Get the own user id
        String productId = result.getString("id");
        String name = result.getString("name");
        String brand = result.getString("brand");
        String type = result.getString("type");
        String imgRef = result.getString("imgRef");
        Double mediaRating = result.getDouble("mediaRating");

        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        for (DocumentSnapshot doc : task.getResult()) {

                            if (!doc.getString("productId").equals(productId)) {
                                continue;
                            }



                            //Opinion op = new Opinion(doc.getId());
                            //recAdapter.opinionsList.add(op);
                            //recAdapter.notifyDataSetChanged();
                        }
                    }
                });

        storageRef.child(imgRef).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //img_product.setImageBitmap(bitmap);
            }
        });

        /*
        txt_name.setText(name);
        txt_brand.setText(brand);
        txt_type.setText(type);
        txt_mediaRating.setText(String.valueOf(mediaRating) + " ⭐");*/
    }
}