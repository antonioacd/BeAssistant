package com.example.beassistant.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beassistant.R;
import com.example.beassistant.databinding.ActivityMainBinding;
import com.example.beassistant.fragments.PlayerFragment;
import com.example.beassistant.fragments.VideosFragment;
import com.example.beassistant.fragments.add.SelectProductFragment;
import com.example.beassistant.fragments.home.HomeFragment;
import com.example.beassistant.fragments.profile.ProfileFragment;
import com.example.beassistant.fragments.search.SearchFragment;
import com.example.beassistant.fragments.settings.SettingsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton btn_add;

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
                    replaceFragment(new VideosFragment());
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

}
