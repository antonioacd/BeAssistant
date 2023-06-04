package com.example.beassistant.fragments;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.adapters.OpinionsRecyclerAdapter;
import com.example.beassistant.adapters.UsersRecyclerAdapter;
import com.example.beassistant.models.Opinion;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OpinionsFragment extends Fragment {

    TextView txt_name, txt_brand, txt_type, txt_mediaRating;
    ImageView img_product;

    // Declare the data base controller
    private FirebaseFirestore db;

    // Declare the data base storage controller
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // The recicler adapter
    private OpinionsRecyclerAdapter recAdapter;

    // The recicler view
    private RecyclerView rV;

    public OpinionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Generate the storage instance
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Init the recycler adapter
        recAdapter = new OpinionsRecyclerAdapter(getContext());

        getParentFragmentManager().setFragmentResultListener("keyProduct", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("Data: ", result.toString());

                // Obtains the followers id
                getData(result);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_opinions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img_product = view.findViewById(R.id.img_product_ref3);
        txt_name = view.findViewById(R.id.txt_product_name3);
        txt_brand = view.findViewById(R.id.txt_product_brand3);
        txt_mediaRating = view.findViewById(R.id.txt_product_media_rating3);
        txt_type = view.findViewById(R.id.txt_product_type3);

        // Init the recycler view
        rV = (RecyclerView) view.findViewById(R.id.recycler_view_opinions);

        // Create a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout manager to the recycler view
        rV.setLayoutManager(layoutManager);

        // Set the recycler adapter in the recycler view
        rV.setAdapter(recAdapter);

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(v);

                Fragment fragment = new DetailsOpinionFragment();
                Bundle args = new Bundle();
                args.putString("id", recAdapter.opinionsList.get(index).getOpinionId());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("keyOpinion", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
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
                            Log.d("Data: ","Doc: " + doc.toString());
                            if (doc.getString("productId").equals(productId)){
                                Opinion op = new Opinion(doc.getId());
                                recAdapter.opinionsList.add(op);
                                recAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                });

        storageRef.child(imgRef).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img_product.setImageBitmap(bitmap);
            }
        });

        txt_name.setText(name);
        txt_brand.setText(brand);
        txt_type.setText(type);
        txt_mediaRating.setText(String.valueOf(mediaRating) + " ⭐");
    }
}