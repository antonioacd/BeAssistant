package com.example.beassistant.controllers.fragments.profile.myOpinions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class MyOpinionEdit extends Fragment {

    private FirebaseFirestore db;

    private FirebaseStorage storage;

    private StorageReference storageRef;

    private TextView txt_username;

    private EditText txt_price, txt_shopBuy, txt_toneOrColor, txt_opinion;

    private ImageView img_user_profile;

    private int selected_rating = -1;

    private ArrayList<Integer> number_ratings = new ArrayList<>(Arrays.asList(0,1,2,3,4,5));

    private AutoCompleteTextView select_rating;

    private ArrayAdapter<Integer> adapterItems;

    private FloatingActionButton btn_check;

    private String opinionId = "";

    public MyOpinionEdit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data from last fragment
        getDataFromLastFragment();

        // Init the variables
        initVariables();
    }

    /**
     * Function to init variables
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
    private void getDataFromLastFragment() {
        getParentFragmentManager().setFragmentResultListener("keyMyOpinionEdit", this, new FragmentResultListener() {
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
        return inflater.inflate(R.layout.fragment_my_opinion_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the view variables
        initViewVariables(view);

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the fields are empty
                if (txt_price.getText().toString().isEmpty() ||
                        txt_shopBuy.getText().toString().isEmpty() ||
                        txt_toneOrColor.getText().toString().isEmpty() ||
                        txt_opinion.getText().toString().isEmpty()) {

                    // Notify
                    Toast.makeText(getContext(), "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update the database object
                db.collection("opiniones").document(opinionId).update("price", Double.parseDouble(txt_price.getText().toString().trim()));
                db.collection("opiniones").document(opinionId).update("shopBuy", txt_shopBuy.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("toneOrColor", txt_toneOrColor.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("opinion", txt_opinion.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("rating", selected_rating);

                Toast.makeText(getContext(), "Opinion Modificada", Toast.LENGTH_SHORT).show();

                // Update the rating media
                updateRatingMedia();

                Fragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();

            }
        });

        adapterRatingConfiguration();
    }

    /**
     * Function to init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        txt_username = (TextView) view.findViewById(R.id.txt_username_opinion_item_02_my_opinions_details_02);
        txt_price = (EditText) view.findViewById(R.id.txt_price_02_my_opinions_details_02);
        txt_shopBuy = (EditText) view.findViewById(R.id.txt_shopBuy_02_my_opinions_details_02);
        txt_toneOrColor = (EditText) view.findViewById(R.id.txt_toneOrColor_02_my_opinions_details_02);
        txt_opinion = (EditText) view.findViewById(R.id.txt_opinion_02_my_opinions_details_02);
        img_user_profile = (ImageView) view.findViewById(R.id.img_user_profile_02_my_opinions_details_02);
        select_rating = view.findViewById(R.id.select_rating_02);
        btn_check = view.findViewById(R.id.btn_check_02);
    }

    /**
     * Function to update the rating media
     */
    private void updateRatingMedia(){

        // Get the opinion
        db.collection("opiniones").document(opinionId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // Check the task
                if (!task.isSuccessful()){
                    return;
                }

                DocumentSnapshot opinionsDoc = task.getResult();

                String productId = opinionsDoc.getString("productId");
                String category = opinionsDoc.getString("productCategory");
                String brand = opinionsDoc.getString("productBrand");

                db.collection("opiniones")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                // Check the task
                                if (!task.isSuccessful()){
                                    return;
                                }

                                // Create the variables
                                int cont = 0;
                                double sumatory = 0;

                                // Loop the result
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    // Check the product id
                                    if (document.getString("productId").equals(productId)){
                                        // Increment the cont
                                        cont++;
                                        // Add the rating to the sumatory
                                        sumatory += document.getDouble("rating");
                                    }
                                }
                                // Set the variables
                                double finalSumatory = sumatory;
                                int finalCont = cont;

                                // Get the products
                                db.collection("categorias/"+category+"/marcas/"+brand+"/productos/")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                // Check the task
                                                if (!task.isSuccessful()){
                                                    return;
                                                }

                                                // Get the media
                                                double media = finalSumatory / finalCont;

                                                // Loop the results
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    // Check the product id
                                                    if (document.getString("id").equals(productId)) {
                                                        // Update the media rating
                                                        db.collection("categorias/"+category+"/marcas/"+brand+"/productos/").document(document.getId()).update("rating", media);
                                                    }
                                                }
                                            }
                                        });
                            }
                        });
            }
        });


    }

    /**
     * Function to get the data from a result
     * @param result
     */
    private void getData(Bundle result){

        // Get product id
        String productId = result.getString("id");

        // Get opinion details
        getOpinionDetails(productId);
    }

    /**
     * Function to get the opinion details
     * @param productId
     */
    private void getOpinionDetails(String productId) {

        // Get the opinions
        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                            // Check the productId
                            if (!opinionsDoc.getString("productId").equals(productId)){
                                continue;
                            }

                            // Set the opinionId
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
                            txt_price.setText(opinionsDoc.getDouble("price").toString());
                            txt_shopBuy.setText(opinionsDoc.getString("shopBuy"));
                            txt_toneOrColor.setText(opinionsDoc.getString("toneOrColor"));
                            txt_opinion.setText(opinionsDoc.getString("opinion"));
                            selected_rating = opinionsDoc.getDouble("rating").intValue();

                        }
                    }
                });
    }

    private void adapterRatingConfiguration(){
        adapterItems = new ArrayAdapter<>(getContext(),R.layout.list_item02, number_ratings);
        select_rating.setAdapter(adapterItems);

        select_rating.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_rating = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }
        });
    }
}