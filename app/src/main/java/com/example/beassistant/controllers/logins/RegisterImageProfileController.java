package com.example.beassistant.controllers.logins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.beassistant.R;

public class RegisterImageProfileController extends AppCompatActivity {

    private String username;
    private String name;
    private String img;
    private String email;
    private String number;
    private String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_image_profile_controller);

        Intent i = getIntent();

        username = i.getStringExtra("username");
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        number = i.getStringExtra("number");
        password = i.getStringExtra("password");




    }
}