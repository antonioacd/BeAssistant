package com.example.beassistant.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beassistant.R;
import com.example.beassistant.databinding.ActivityMainBinding;
import com.example.beassistant.fragments.home.DetailsProductFragment;
import com.example.beassistant.fragments.settings.SettingsFragment;
import com.example.beassistant.fragments.videos.VideosFragment;
import com.example.beassistant.fragments.add.SelectProductFragment;
import com.example.beassistant.fragments.home.HomeFragment;
import com.example.beassistant.fragments.profile.ProfileFragment;
import com.example.beassistant.fragments.search.SearchFragment;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton btn_add;

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBindingConfiguration();

        initViewVariables();

        buttonAddOpinionListener();

        navigationBarListener();
    }

    private void navigationBarListener() {
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.explore:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });
    }

    private void buttonAddOpinionListener() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SelectProductFragment());
            }
        });
    }

    private void initViewVariables() {
        btn_add = (FloatingActionButton) findViewById(R.id.btn_add);
    }

    private void setBindingConfiguration() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        FrameLayout fragmentContainer = findViewById(R.id.frame_layout);

        // Ajustar la altura del FrameLayout después de que el fragmento se haya agregado
        fragmentContainer.post(new Runnable() {
            @Override
            public void run() {
                int fragmentHeight = fragmentContainer.getHeight();
                ViewGroup.LayoutParams layoutParams = fragmentContainer.getLayoutParams();
                layoutParams.height = fragmentHeight;
                fragmentContainer.setLayoutParams(layoutParams);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.d("Atras:", "Pulsado");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().commit();
            Log.d("Atras:", "Anterior Fragment");
            Log.d("Atras:", "Cantidad: " + getSupportFragmentManager().getBackStackEntryCount());
        } else {
            Log.d("Atras:", "Cantidad: " + getSupportFragmentManager().getBackStackEntryCount());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Cancelado el escaneo
                // ...
            } else {
                String barcode = result.getContents();
                Log.d("Codigo: ", barcode);
                getProduct(barcode);
            }
        }

    }

    private void getProduct(String reference){
        db.collectionGroup("productos").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("Query:", "Entra");
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {

                            Log.d("Referencia01: ", doc.getId());

                            if (!doc.getId().equals(reference)){
                                continue;
                            }

                            Log.d("Referencia: ", doc.getId());

                            Product product = new Product(
                                    doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("imgRef"),
                                    doc.getString("brand"),
                                    doc.getString("category"),
                                    doc.getString("type"),
                                    doc.getDouble("rating"),
                                    doc.getString("url")
                            );

                            Log.d("Referencia: ", product.toString());

                            Fragment fragment = new DetailsProductFragment();
                            Bundle args = new Bundle();
                            args.putString("id", product.getUuID());
                            args.putString("name", product.getName());
                            args.putString("brand", product.getBrand());
                            args.putString("type", product.getType());
                            args.putDouble("mediaRating", product.getMediaRating());
                            args.putString("imgRef", product.getImg_reference());
                            args.putString("url", product.getUrl());

                            Log.d("Args: ", args.toString());

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.setFragmentResult("keyProduct", args);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            // Return to the home fragment

                        }
                    }
                });
    }
}
