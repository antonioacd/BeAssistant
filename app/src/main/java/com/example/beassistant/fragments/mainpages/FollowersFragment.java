package com.example.beassistant.fragments.mainpages;

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
import android.widget.TextView;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.example.beassistant.adapters.FollowersRecyclerAdapter;
import com.example.beassistant.models.UserInAList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FollowersFragment extends Fragment {

    // Declare the data base object
    private FirebaseFirestore db;

    // Declare de recyler adapter
    FollowersRecyclerAdapter recyclerAdapter;

    // Declare the recicler view
    RecyclerView reciclerView;

    // Declare the title text view
    TextView txt_title;

    public FollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtains the followers id
                getFollowersId(result);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_follows, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Init the title
        txt_title = view.findViewById(R.id.txt_title);

        // Set the title
        txt_title.setText("Seguidores");

        // Init the recicler adapter
        recyclerAdapter = new FollowersRecyclerAdapter(getContext());

        // Init the recicler view
        reciclerView = (RecyclerView) view.findViewById(R.id.rec_view_followers);

        // Create a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout managetr to the recicler view
        reciclerView.setLayoutManager(layoutManager);

        // Implement the recicler adapter in the recicler view
        reciclerView.setAdapter(recyclerAdapter);

        // Set a listener to the recicler adapter items
        recyclerAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;

                // Get the index
                index = reciclerView.getChildAdapterPosition(view);

                String selectedUserId = recyclerAdapter.followersList.get(index).getId();

                Fragment fragment;

                if(selectedUserId.equals(Shared.myUser.getId())){
                    fragment = new ProfileFragment();
                }else{
                    fragment = new ProfileOthersFragment();
                }

                Bundle args = new Bundle();
                args.putString("id", selectedUserId);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("follower", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();

                // Set the view selected as true
                view.setSelected(true);
            }
        });
    }

    /**
     * Function to get the followers id
     * @param result
     */
    private void getFollowersId(Bundle result){
        // Get the own user id
        String userId = result.getString("id");

        // Create the path to the query
        String path = "/users/"+userId+"/seguidores";

        // Query about the followers
        db.collection(path)
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
                            getDataUsers(doc);
                        }
                        // Notify data set changed
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * Function to get the data users
     * @param doc
     */
    private void getDataUsers(QueryDocumentSnapshot doc){
        Log.d("TAG", "Entra al bucle");
        db.collection("users")
                .document(doc.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("TAG", "Entra al on Complete");
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Obtains the documents
                        DocumentSnapshot document = task.getResult();

                        // Check if document exists
                        if (!document.exists()) {
                            return;
                        }

                        // Insert the user
                        insertUser(document);
                    }
                });
    }

    /**
     * Function to insert a user
     * @param document
     */
    private void insertUser(DocumentSnapshot document){

        // Create the user
        UserInAList user = new UserInAList(document.getId(), document.getString("username"), document.getString("imgRef"));

        // Insert the user
        recyclerAdapter.followersList.add(user);

        // Notify data set changed
        recyclerAdapter.notifyDataSetChanged();
    }



}