package com.example.beassistant.controllers;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beassistant.R;
import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginController extends AppCompatActivity {

    /**
     * Declare the variables
     */

    EditText et_user;
    EditText et_password;
    Button btn_login;
    Button btn_password;

    /**
     * Declare the instance of data base controller
     */

    DataBaseController DBController = new DataBaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Inicialice the variables
         */

        et_user = (EditText) findViewById(R.id.et_user);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_password = (Button) findViewById(R.id.btn_register);



        /**
         * Add the listeners of the buttons
         */

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User(et_user.getText().toString(), "Antonio", "fad", "fasd", et_password.getText().toString());

                DBController.addUser(user);

            }
        });




    }
}
