package com.example.beassistant.controllers.fragments.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.models.Shared;
import com.example.beassistant.adapters.UsersRecyclerAdapter;
import com.example.beassistant.controllers.fragments.profile.ProfileOthersFragment;
import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    // Declare the full users list
    private ArrayList<User> usersFullList;

    // The users search view
    private SearchView searchView;

    // Declare the data base object
    private FirebaseFirestore db;

    // The recicler adapter
    private UsersRecyclerAdapter recAdapter;

    // The recicler view
    private RecyclerView rV;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init variables
        initVariables();

        // Get the users
        getUsers();

        // Set the full users list
        usersFullList = recAdapter.usersList;
    }

    /**
     * Init view variables
     */
    private void initVariables() {
        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Init the recycler adapter
        recAdapter = new UsersRecyclerAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the view variables
        initViewVariables(view);

        // Set the recycler view configuration
        recyclerViewConfiguration();

        // Set the search view listener
        searchViewListener();

        // Set the recycler adapter listener
        recyclerAdapterListener();

    }

    /**
     * Set the recycler adapter listener
     */
    private void recyclerAdapterListener() {
        // Set a listener to the recycler adapter items
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(view);

                Fragment fragment = new ProfileOthersFragment();
                Bundle args = new Bundle();
                args.putString("id", recAdapter.usersList.get(index).getUserId());

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("follower", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Set the view selected as true
                view.setSelected(true);
            }
        });
    }

    private void searchViewListener() {
        // Set the listener to the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
    }

    /**
     * Set the recycler view configuration
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
        // Init the recycler view
        rV = (RecyclerView) view.findViewById(R.id.recyclerViewUsers);

        // Init the search view
        searchView = (SearchView) view.findViewById(R.id.searchViewUsers);
    }

    /**
     * Function to filter a list
     * @param newText
     */
    private void filterList(String newText){

        // Create a filtered list
        ArrayList<User> filteredList = new ArrayList<>();

        // Loop the user list
        for (User u : recAdapter.usersList) {
            if (u.getUsername().toLowerCase().contains(newText.toLowerCase())){
                Log.d("Entra:","si");
                filteredList.add(u);
            }
        }

        if (!newText.equals("")){
            if (filteredList.isEmpty()){
                Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_LONG).show();
            }else {
                recAdapter.setFilteredList(filteredList);
            }
        }else {
            Log.d("Entra:","vacio" + usersFullList);
            recAdapter.setFilteredList(usersFullList);
        }
    }

    private void getUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Check if the task is successful
                if (!task.isSuccessful()){
                    return;
                }
                // Loop the documents
                for (QueryDocumentSnapshot doc : task.getResult()) {

                    // Check if is my own user
                    if (doc.getId().equals(Shared.myUser.getUserId())){
                        continue;
                    }

                    // Create the user
                    User user = new User(doc.getId(), doc.getString("username"), doc.getString("imgRef"));

                    // Insert the user
                    recAdapter.usersList.add(user);

                    // Notify data set changed
                    recAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}