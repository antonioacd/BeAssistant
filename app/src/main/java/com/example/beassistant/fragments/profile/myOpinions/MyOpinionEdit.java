package com.example.beassistant.fragments.profile.myOpinions;

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
import com.example.beassistant.Shared;
import com.example.beassistant.fragments.profile.ProfileFragment;
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

    // Declare the data base controller
    private FirebaseFirestore db;

    // Declare the data base storage controller
    private FirebaseStorage storage;
    private StorageReference storageRef;

    TextView txt_username;
    EditText txt_price, txt_shopBuy, txt_toneOrColor, txt_opinion;
    ImageView img_user_profile;

    int selected_rating = -1;
    ArrayList<Integer> number_ratings = new ArrayList<>(Arrays.asList(0,1,2,3,4,5));
    AutoCompleteTextView select_rating;
    ArrayAdapter<Integer> adapterItems;

    FloatingActionButton btn_check;

    String opinionId = "";

    public MyOpinionEdit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("keyMyOpinionEdit", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                // Obtains the followers id
                getDataFromLastFragment(result);
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
        return inflater.inflate(R.layout.fragment_my_opinion_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_username = (TextView) view.findViewById(R.id.txt_username_opinion_item_02_my_opinions_details_02);
        txt_price = (EditText) view.findViewById(R.id.txt_price_02_my_opinions_details_02);
        txt_shopBuy = (EditText) view.findViewById(R.id.txt_shopBuy_02_my_opinions_details_02);
        txt_toneOrColor = (EditText) view.findViewById(R.id.txt_toneOrColor_02_my_opinions_details_02);
        txt_opinion = (EditText) view.findViewById(R.id.txt_opinion_02_my_opinions_details_02);
        img_user_profile = (ImageView) view.findViewById(R.id.img_user_profile_02_my_opinions_details_02);
        select_rating = view.findViewById(R.id.select_rating_02);
        btn_check = view.findViewById(R.id.btn_check_02);

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txt_price.getText().toString().isEmpty() ||
                        txt_shopBuy.getText().toString().isEmpty() ||
                        txt_toneOrColor.getText().toString().isEmpty() ||
                        txt_opinion.getText().toString().isEmpty()) {

                    Toast.makeText(getContext(), "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update the database object
                db.collection("opiniones").document(opinionId).update("price", Double.parseDouble(txt_price.getText().toString().trim()));
                db.collection("opiniones").document(opinionId).update("shopBuy", txt_shopBuy.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("toneOrColor", txt_toneOrColor.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("opinion", txt_opinion.getText().toString().trim());
                db.collection("opiniones").document(opinionId).update("rating", selected_rating);

                //
                Toast.makeText(getContext(), "Opinion Modificada", Toast.LENGTH_SHORT).show();

                db.collection("opiniones").document(opinionId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.isSuccessful()){
                            return;
                        }

                        DocumentSnapshot doc = task.getResult();

                        getRatingMedia(doc.getString("productId"), doc.getString("productCategory"), doc.getString("productBrand"));
                    }
                });

                Fragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();

            }
        });

        adapterRatingConfiguration();
    }

    private void getRatingMedia(String productId, String category, String brand){

        db.collection("opiniones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int cont = 0;
                        double sumatory = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("productId").equals(productId)){
                                cont++;
                                Log.d("Sumatorio: ", ""+document.getDouble("rating"));
                                sumatory += document.getDouble("rating");
                            }
                        }
                        double finalSumatory = sumatory;
                        int finalCont = cont;
                        db.collection("categorias/"+category+"/marcas/"+brand+"/productos/")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        double media = finalSumatory / finalCont;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getString("id").equals(productId)) {

                                                db.collection("categorias/"+category+"/marcas/"+brand+"/productos/").document(document.getId()).update("rating", media);

                                            }
                                        }
                                    }
                                });
                        Log.d("Sumatorio: ", ""+sumatory);
                    }
                });
    }

    private void getDataFromLastFragment(Bundle result){

        // Get the own user id
        String productId = result.getString("id");

        Log.d("RecibeEdit1: ", productId);

        getOpinionDetails(productId);
    }

    private void getOpinionDetails(String productId) {

        Log.d("RecibeEdit2: ", productId);

        db.collection("opiniones")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }

                        for (QueryDocumentSnapshot opinionsDoc : task.getResult()){

                            if (!opinionsDoc.getString("userId").equals(Shared.myUser.getUserId())) {
                                continue;
                            }

                            if (!opinionsDoc.getString("productId").equals(productId)){
                                continue;
                            }

                            opinionId = opinionsDoc.getId();

                            storageRef.child(Shared.myUser.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    img_user_profile.setImageBitmap(bitmap);
                                }
                            });

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