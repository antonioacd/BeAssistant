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

    private String productId = "", name = "", brand = "", type = "", imgRef = "", url="";
    private Double mediaRating;
    Button btn_ver_videos, btn_comprar;

    private Boolean isFirstResume = true;

    public DetailsProductFragment() {
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

        getDataFromLastFragment();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFirstResume){
            isFirstResume = false;
            return;
        }

        Log.d("Args:", "Resume:" + name + brand + type + imgRef + mediaRating);

        getOpinions(productId);
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

        img_product = view.findViewById(R.id.img_product_ref3);
        txt_name = view.findViewById(R.id.txt_product_name3);
        txt_brand = view.findViewById(R.id.txt_product_brand3);
        txt_mediaRating = view.findViewById(R.id.txt_product_media_rating3);
        txt_type = view.findViewById(R.id.txt_product_type3);
        btn_comprar = (Button) view.findViewById(R.id.btn_comprar);
        btn_ver_videos = (Button) view.findViewById(R.id.btn_ver_videos);

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

        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new WebFragment();
                Bundle args = new Bundle();
                args.putString("url", url);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("shopFragment", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btn_ver_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new VideosFragment();
                Bundle args = new Bundle();
                args.putString("productName", name);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("videosFragment", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

    }

    private void getDataFromLastFragment() {

        if (!productId.isEmpty()) {
            Log.d("Args:", "isEmpty" + name + brand + type + imgRef + mediaRating);
            getOpinions(productId);
            fillProductData(name, brand, type, imgRef, mediaRating);
            return;
        }

        getParentFragmentManager().setFragmentResultListener("keyProduct", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                productId = result.getString("id");
                name = result.getString("name");
                brand = result.getString("brand");
                type = result.getString("type");
                imgRef = result.getString("imgRef");
                mediaRating = result.getDouble("mediaRating");
                url = result.getString("url");

                getOpinions(productId);
                fillProductData(name, brand, type, imgRef, mediaRating);
            }

        });
    }

    private void getOpinions(String productId) {

        recAdapter.opinionsList.clear();
        recAdapter.notifyDataSetChanged();

        // Get the own user id
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

                        if (count == 0){
                            Toast.makeText(getContext(), "El producto seleccionado aún no dispone de opiniones", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Loop the opinions
                        for (DocumentSnapshot opinionsDoc : task.getResult()) {
                            Opinion op = new Opinion();

                            op.setOpinionId(opinionsDoc.getId());
                            op.setUsername(opinionsDoc.getString("username"));
                            op.setImgUser(opinionsDoc.getString("imgUserRef"));
                            op.setRating(opinionsDoc.getDouble("rating").intValue());
                            op.setPrice(opinionsDoc.getDouble("price").intValue());
                            op.setShopBuy(opinionsDoc.getString("shopBuy"));
                            op.setToneOrColor(opinionsDoc.getString("toneOrColor"));
                            op.setOpinion(opinionsDoc.getString("opinion"));

                            Log.d("Opiniones: ", op.toString());

                            recAdapter.opinionsList.add(op);
                            recAdapter.notifyDataSetChanged();
                        }
                    }
                });


    }

    private void fillProductData(String name, String brand, String type, String imgRef, Double mediaRating) {

        Log.d("Args:", name + brand + type + imgRef + mediaRating);

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
        txt_mediaRating.setText(mediaRating +" ⭐");
    }
}