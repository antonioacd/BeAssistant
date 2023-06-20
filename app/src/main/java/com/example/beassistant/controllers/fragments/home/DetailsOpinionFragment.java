package com.example.beassistant.controllers.fragments.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsOpinionFragment extends Fragment {

    // Declare the data base controller
    private FirebaseFirestore db;

    // Declare the data base storage controller
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView txt_username, txt_rating, txt_price, txt_shopBuy, txt_toneOrColor, txt_opinion;
    private ImageView img_user_profile;

    public DetailsOpinionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the data from last fragment
        getDataFromLastFragment();

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
     * Functio to get the data from last fragment
     */
    private void getDataFromLastFragment() {
        getParentFragmentManager().setFragmentResultListener("keyOpinion", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                // Obtains the followers id
                getOpinionDetails(result);
            }
        });
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

        // Init the view variables
        initViewVariables(view);
    }

    /**
     * Function to init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        txt_username = (TextView) view.findViewById(R.id.txt_username_opinion_item_02);
        txt_rating = (TextView) view.findViewById(R.id.txt_rating_02);
        txt_price = (TextView) view.findViewById(R.id.txt_price_02);
        txt_shopBuy = (TextView) view.findViewById(R.id.txt_shopBuy_02);
        txt_toneOrColor = (TextView) view.findViewById(R.id.txt_toneOrColor_02);
        txt_opinion = (TextView) view.findViewById(R.id.txt_opinion_02);
        img_user_profile = (ImageView) view.findViewById(R.id.img_user_profile_02);
    }

    /**
     * Function to get the opinion details
     * @param result
     */
    private void getOpinionDetails(Bundle result){

        // Get the own user ids
        String opinionId = result.getString("id");

        // Get the opinions
        db.collection("opiniones")
                .document(opinionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // Check the task
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get the doc
                        DocumentSnapshot opinionsDoc = task.getResult();

                        // Get the user, to get the username and image
                        db.collection("users")
                                .document(opinionsDoc.getString("userId"))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        // Check the task
                                        if (!task.isSuccessful()) {
                                            return;
                                        }

                                        // Get the doc
                                        DocumentSnapshot usersDoc = task.getResult();

                                        // Set the profile image
                                        storageRef.child(usersDoc.getString("imgRef")).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                img_user_profile.setImageBitmap(bitmap);
                                            }
                                        });

                                        // Set the details
                                        txt_username.setText(usersDoc.getString("username"));
                                        txt_rating.setText(String.valueOf(opinionsDoc.getDouble("rating")) + " ⭐");
                                        txt_price.setText(String.valueOf(opinionsDoc.getDouble("price")) + "€");
                                        txt_shopBuy.setText(opinionsDoc.getString("shopBuy"));
                                        txt_toneOrColor.setText(opinionsDoc.getString("toneOrColor"));
                                        txt_opinion.setText(opinionsDoc.getString("opinion"));

                                    }
                                });
                    }
                });
    }
}