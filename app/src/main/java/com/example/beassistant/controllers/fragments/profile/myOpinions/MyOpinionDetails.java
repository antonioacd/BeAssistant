package com.example.beassistant.controllers.fragments.profile.myOpinions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.models.Shared;
import com.example.beassistant.controllers.fragments.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyOpinionDetails extends Fragment {

    // Declare the data base controller
    private FirebaseFirestore db;

    // Declare the data base storage controller
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView txt_username, txt_rating, txt_price, txt_shopBuy, txt_toneOrColor, txt_opinion;
    private ImageView img_user_profile;
    private FloatingActionButton btn_edit, btn_delete, btn_delete_confimation, btn_delete_cancel;

    // Get the own user id
    private String productId = "";
    private String category = "";
    private String brand = "";

    private String opinionId = "";

    private AlertDialog dialog;

    public MyOpinionDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data from last fragment
        getData();

        // Init the variables
        initVariables();

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
    }

    /**
     * Function to get the data from last fragment
     */
    private void getData() {
        getParentFragmentManager().setFragmentResultListener("keyOpinions", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                // Obtains the followers id
                getData(result);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_opinions_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_username = (TextView) view.findViewById(R.id.txt_username_opinion_item_02_my_opinions_details);
        txt_rating = (TextView) view.findViewById(R.id.txt_rating_02_my_opinions_details);
        txt_price = (TextView) view.findViewById(R.id.txt_price_02_my_opinions_details);
        txt_shopBuy = (TextView) view.findViewById(R.id.txt_shopBuy_02_my_opinions_details);
        txt_toneOrColor = (TextView) view.findViewById(R.id.txt_toneOrColor_02_my_opinions_details);
        txt_opinion = (TextView) view.findViewById(R.id.txt_opinion_02_my_opinions_details);
        img_user_profile = (ImageView) view.findViewById(R.id.img_user_profile_02_my_opinions_details);
        btn_edit = (FloatingActionButton) view.findViewById(R.id.btn_edit_my_opinion);
        btn_delete = (FloatingActionButton) view.findViewById(R.id.btn_delete_my_opinion);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MyOpinionEdit();
                Bundle args = new Bundle();
                args.putString("id", productId);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("keyMyOpinionEdit", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

    }

    private void delete(){

        AlertDialog.Builder window = new AlertDialog.Builder(getContext());

        View v = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);

        //Select Category
        btn_delete_confimation = v.findViewById(R.id.btn_delete_confirmation);
        btn_delete_cancel = v.findViewById(R.id.btn_delete_cancel);

        btn_delete_confimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("opiniones")
                        .document(opinionId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Opinion eliminada correctamente", Toast.LENGTH_SHORT).show();

                                Fragment fragment = new ProfileFragment();
                                FragmentManager fragmentManager = getParentFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.commit();

                                // Update rating media
                                updateRatingMedia();

                                dialog.dismiss();
                            }
                        });
            }
        });

        btn_delete_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        window.setView(v);

        dialog = window.create();

        dialog.show();
    }

    private void getData(Bundle result){

        // Set the product id
        productId = result.getString("id");

        // Get the opinion details
        getOpinionDetails(productId);
    }

    /**
     * Function to get the opinion details
     * @param productId
     */
    private void getOpinionDetails(String productId) {

        // Get the opinions
        db.collection("opiniones")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Check the task
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Loop the result
                        for (QueryDocumentSnapshot opinionsDoc : task.getResult()){

                            // Check the user id
                            if (!opinionsDoc.getString("userId").equals(Shared.myUser.getUserId())) {
                                continue;
                            }

                            // Check the product id
                            if (!opinionsDoc.getString("productId").equals(productId)){
                                continue;
                            }

                            // Set the opinion id
                            opinionId = opinionsDoc.getId();

                            // Set the image
                            storageRef.child(Shared.myUser.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    img_user_profile.setImageBitmap(bitmap);
                                }
                            });

                            // Set the data
                            txt_username.setText(Shared.myUser.getUsername());
                            txt_rating.setText(opinionsDoc.getDouble("rating") + " ⭐");
                            txt_price.setText(opinionsDoc.getDouble("price") + "€");
                            txt_shopBuy.setText(opinionsDoc.getString("shopBuy"));
                            txt_toneOrColor.setText(opinionsDoc.getString("toneOrColor"));
                            txt_opinion.setText(opinionsDoc.getString("opinion"));

                        }
                    }
                });
    }

    /**
     * Function to update the rating media
     */
    private void updateRatingMedia(){

        // Get the products
        db.collectionGroup("productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Check the task
                if (!task.isSuccessful()){
                    return;
                }

                // Loop the result
                for (QueryDocumentSnapshot productsDoc : task.getResult()) {

                    // Check the id
                    if (!productsDoc.getString("id").equals(productId)){
                        continue;
                    }
                    // Set the category and brand
                    category = productsDoc.getString("category");
                    brand = productsDoc.getString("brand");
                }

                // Update the media
                getMedia();
            }
        });
    }

    /**
     * Function to get the media
     */
    private void getMedia() {
        // Get all the opinions of the product
        db.collection("opiniones").whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Create the variables
                        int cont = 0;
                        double sumatory = 0;

                        // Loop the result
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cont++;
                            sumatory += document.getDouble("rating");
                        }

                        // Create the variables
                        double finalSumatory = sumatory;
                        int finalCont = cont;

                        // Get the product to get the id
                        db.collection("categorias/"+category+"/marcas/"+brand+"/productos/")
                                .whereEqualTo("id", productId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        double media = finalSumatory / finalCont;

                                        // Loop the result
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Update the media
                                            db.collection("categorias/"+category+"/marcas/"+brand+"/productos/").document(document.getId()).update("rating", media);
                                        }
                                    }
                                });
                    }
                });
    }
}