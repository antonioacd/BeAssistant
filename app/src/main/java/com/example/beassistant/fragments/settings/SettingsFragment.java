package com.example.beassistant.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.beassistant.R;
import com.example.beassistant.controllers.BarcodeScannerActivity;
import com.example.beassistant.controllers.logins.LoginController;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_sign_out = view.findViewById(R.id.btn_sign_out);
        Button btn_change_user = view.findViewById(R.id.btn_change_user);
        Button btn_change_img_profile = view.findViewById(R.id.btn_change_img_profile);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                // Declare the google sing in variables
                GoogleSignInOptions gso;
                GoogleSignInClient gsc;

                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                gsc = GoogleSignIn.getClient(getContext(), gso);

                Task<Void> signOutTask = gsc.signOut();
                signOutTask.addOnCompleteListener(task -> {
                    startActivity(new Intent(getContext(), LoginController.class));
                });
            }
        });

        btn_change_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BarcodeScannerActivity.class));
            }
        });

    }
}