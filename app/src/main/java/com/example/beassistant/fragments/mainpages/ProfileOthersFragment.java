package com.example.beassistant.fragments.mainpages;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileOthersFragment extends Fragment {

    // Declare the data base object
    private FirebaseFirestore db;

    // Declarer the storage objects
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Profile parameters
    private Button btn_follow;
    private ImageView img_profile;
    private TextView txt_username, txt_name, txt_numOpinions, txt_numFollowers, txt_numFollowing;
    private String id;
    private boolean following = false;

    public ProfileOthersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Init the storage
        storage = FirebaseStorage.getInstance();

        // Set the storage ref
        storageRef = storage.getReference();

        checkFollow();

        getParentFragmentManager().setFragmentResultListener("follower", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtain the follower id
                String userId = result.getString("id");

                // Get the user
                getUser(userId);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_others, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_follow = view.findViewById(R.id.btn_follow);
        img_profile = view.findViewById(R.id.img_profile_02);
        txt_username = view.findViewById(R.id.txt_username_02);
        txt_name = view.findViewById(R.id.txt_name_02);
        txt_numOpinions = view.findViewById(R.id.txt_num_opinions_02);
        txt_numFollowers = view.findViewById(R.id.txt_num_followers_02);
        txt_numFollowing = view.findViewById(R.id.txt_num_following_02);

        txt_numFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("id", id);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        txt_numFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FollowingFragment();
                Bundle args = new Bundle();
                args.putString("id", id);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("key", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            // Set their user in mi following
            @Override
            public void onClick(View view) {
                if(!following){
                    // Follow the user
                    followUser();
                    checkFollow();
                    return;
                }

                // Unfollow the user
                unfollowUser();
                checkFollow();
            }
        });
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

    private void checkFollow(){

        following = false;

        db.collection("users/"+Shared.myUser.getId()+"/seguidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Check if the task is successful
                        if (!task.isSuccessful()){
                            return;
                        }
                        // Loop all the docs of the result
                        for (QueryDocumentSnapshot doc: task.getResult()) {

                            // Check if is the same id
                            if (doc.getId().equals(id)){
                                following = true;
                            }
                        }

                        btn_follow.setText((following) ? "Siguiendo" : "Seguir");

                        // Check if you follow this user
                        /*if(following){
                            btn_follow.setText("Siguiendo");
                            return;
                        }

                        // Set the btn text
                        btn_follow.setText("Seguir");*/
                    }
                });
    }

    private void getUser(String userId){
        // Search the user in the database
        db.collection("users")
                .document(userId)
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

                        id = document.getId();
                        txt_username.setText(document.getString("username"));
                        txt_name.setText(document.getString("name"));
                        txt_numOpinions.setText(String.valueOf(document.getDouble("numOpiniones").intValue()));
                        txt_numFollowers.setText(String.valueOf(document.getDouble("numSeguidores").intValue()));
                        txt_numFollowing.setText(String.valueOf(document.getDouble("numSeguidos").intValue()));
                        cargarFoto(document.getString("imgRef"));

                        checkFollow();
                    }
                });
    }

    private void followUser(){
        Map<String, Object> object = new HashMap<>();
        object.put("id", id);
        db.collection("users/"+Shared.myUser.getId()+"/seguidos/")
                .document(id)
                .set(object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btn_follow.setText("Siguendo");
                        Map<String, Object> objecto02 = new HashMap<>();
                        objecto02.put("id", id);

                        // Set my user in thir followers
                        db.collection("users/"+id+"/seguidores/")
                                .document(Shared.myUser.getId())
                                .set(objecto02)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        // Add 1 to their number of followers
                                        db.collection("users")
                                                .document(id)
                                                .update("numSeguidores", FieldValue.increment(1))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        // Add 1 to mi number of following
                                                        db.collection("users")
                                                                .document(Shared.myUser.getId())
                                                                .update("numSeguidos", FieldValue.increment(1))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        getUser(id);
                                                                        checkFollow();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void unfollowUser(){
        Map<String, Object> object = new HashMap<>();
        object.put("id", id);
        // Delete the user from mi following
        db.collection("users/"+Shared.myUser.getId()+"/seguidos/")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btn_follow.setText("Seguir");
                        Map<String, Object> objecto02 = new HashMap<>();
                        objecto02.put("id", id);

                        // Delete my user from their followers
                        db.collection("users/"+id+"/seguidores/")
                                .document(Shared.myUser.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        // Rest 1 to their number of followers
                                        db.collection("users")
                                                .document(id)
                                                .update("numSeguidores", FieldValue.increment(-1))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        // Rest 1 to mi number of following
                                                        db.collection("users")
                                                                .document(Shared.myUser.getId())
                                                                .update("numSeguidos", FieldValue.increment(-1))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        getUser(id);
                                                                        checkFollow();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

}