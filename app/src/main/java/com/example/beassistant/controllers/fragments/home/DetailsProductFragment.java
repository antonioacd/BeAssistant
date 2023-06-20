package com.example.beassistant.controllers.fragments.home;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.adapters.OpinionsRecyclerAdapter;
import com.example.beassistant.controllers.fragments.videos.VideosFragment;
import com.example.beassistant.controllers.fragments.shoping.WebFragment;
import com.example.beassistant.models.Opinion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsProductFragment extends Fragment {

    private TextView txt_name, txt_brand, txt_type, txt_mediaRating;

    private ImageView img_product;

    private FirebaseFirestore db;

    private FirebaseStorage storage;

    private StorageReference storageRef;

    private OpinionsRecyclerAdapter recAdapter;

    private RecyclerView rV;

    private String productId = "", name = "", brand = "", type = "", imgRef = "", url="";

    private Double mediaRating;

    private Button btn_see_videos, btn_buy;

    private Boolean isFirstResume = true;

    public DetailsProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init variables
        initVariables();

        // Get data from las fragment
        getDataFromLastFragment();
    }

    /**
     * Function to init the variables
     */
    private void initVariables() {
        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Generate the storage instance
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Init the recycler adapter
        recAdapter = new OpinionsRecyclerAdapter(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if it is the first resume
        if (isFirstResume){
            // Set to false
            isFirstResume = false;
            return;
        }

        // Get the opinions
        getOpinions(productId);

        // Fill the product data
        fillProductData(name, brand, type, imgRef, mediaRating);
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

        // Init the view variables
        initViewVariables(view);

        // Set the recycler view configuration
        recyclerViewConfiguration();

        // Set the recycler adapter listener
        recyclerAdapterListener();

        // Set the buy button listener
        buyButtonListener();

        // Set the see videos button listener
        seeVideosButtonListener();

    }

    /**
     * Fuction to set the see videos button listener
     */
    private void seeVideosButtonListener() {
        // Set the listener
        btn_see_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crete the fragment
                Fragment fragment = new VideosFragment();

                // Set the arguments
                Bundle args = new Bundle();
                args.putString("productName", name);

                // Set the fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("videosFragment", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Function to set the buy button listener
     */
    private void buyButtonListener() {
        // Set the listener
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a fragment
                Fragment fragment = new WebFragment();
                // Set the arguments
                Bundle args = new Bundle();
                args.putString("url", url);

                // Set the fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("shopFragment", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Function to set the recycler adapter listener
     */
    private void recyclerAdapterListener() {
        // Set the listener
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(v);

                // Create a fragment
                Fragment fragment = new DetailsOpinionFragment();
                // Add the arguments
                Bundle args = new Bundle();
                args.putString("id", recAdapter.opinionsList.get(index).getOpinionId());

                // Set the fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("keyOpinion", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Function to set the recycler view configuration
     */
    private void recyclerViewConfiguration() {
        // Create a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout manager to the recycler view
        rV.setLayoutManager(layoutManager);

        // Set the recycler adapter in the recycler view
        rV.setAdapter(recAdapter);
    }

    /**
     * Function to init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        img_product = view.findViewById(R.id.img_product_ref3);
        txt_name = view.findViewById(R.id.txt_product_name3);
        txt_brand = view.findViewById(R.id.txt_product_brand3);
        txt_mediaRating = view.findViewById(R.id.txt_product_media_rating3);
        txt_type = view.findViewById(R.id.txt_product_type3);
        btn_buy = (Button) view.findViewById(R.id.btn_comprar);
        btn_see_videos = (Button) view.findViewById(R.id.btn_ver_videos);
        rV = (RecyclerView) view.findViewById(R.id.recycler_view_opinions);
    }

    /**
     * Function to get the data from the last fragment
     */
    private void getDataFromLastFragment() {

        if (!productId.isEmpty()) {

            getOpinions(productId);
            fillProductData(name, brand, type, imgRef, mediaRating);
            return;
        }

        getParentFragmentManager().setFragmentResultListener("keyProduct", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                // Set the data
                productId = result.getString("id");
                name = result.getString("name");
                brand = result.getString("brand");
                type = result.getString("type");
                imgRef = result.getString("imgRef");
                mediaRating = result.getDouble("mediaRating");
                url = result.getString("url");

                // Get the opinions of the product
                getOpinions(productId);

                // Fill the product data
                fillProductData(name, brand, type, imgRef, mediaRating);
            }

        });
    }

    /**
     * Function to get the opinions
     * @param productId
     */
    private void getOpinions(String productId) {

        // Clear the recycler adapter list
        recAdapter.opinionsList.clear();
        recAdapter.notifyDataSetChanged();

        // Get opinions of the product
        db.collection("opiniones")
                .whereEqualTo("productId",productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get the number of opinions
                        int count = task.getResult().size();

                        // Check if the product contains opinions
                        if (count == 0){
                            Toast.makeText(getContext(), "El producto seleccionado aún no dispone de opiniones", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Loop the opinions
                        for (DocumentSnapshot opinionsDoc : task.getResult()) {

                            // Create the opinion
                            Opinion op = new Opinion();

                            // Add the values to the opinion
                            op.setOpinionId(opinionsDoc.getId());
                            op.setUsername(opinionsDoc.getString("username"));
                            op.setImgUser(opinionsDoc.getString("imgUserRef"));
                            op.setRating(opinionsDoc.getDouble("rating").intValue());
                            op.setPrice(opinionsDoc.getDouble("price").intValue());
                            op.setShopBuy(opinionsDoc.getString("shopBuy"));
                            op.setToneOrColor(opinionsDoc.getString("toneOrColor"));
                            op.setOpinion(opinionsDoc.getString("opinion"));

                            // Add the opinion to the opinion list
                            recAdapter.opinionsList.add(op);
                            recAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * Function to fill the product values
     * @param name
     * @param brand
     * @param type
     * @param imgRef
     * @param mediaRating
     */
    private void fillProductData(String name, String brand, String type, String imgRef, Double mediaRating) {

        // Set the image
        storageRef.child(imgRef).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img_product.setImageBitmap(bitmap);
            }
        });

        // Set the values
        txt_name.setText(name);
        txt_brand.setText(brand);
        txt_type.setText(type);
        txt_mediaRating.setText(mediaRating +" ⭐");
    }
}