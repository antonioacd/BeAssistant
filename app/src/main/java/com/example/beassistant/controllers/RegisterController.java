package com.example.beassistant.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.beassistant.R;
import com.example.beassistant.models.User;

public class RegisterController extends AppCompatActivity {

    EditText et_user_reg;
    EditText et_name_reg;
    EditText et_email_reg;
    EditText et_number_reg;
    EditText et_password_reg;
    Button btn_register_reg;

    /**
     * Declare the instance of data base controller
     */

    DataBaseController DBController = new DataBaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_controller);


        /**
         * Inicialice the variables
         */

        et_user_reg = (EditText) findViewById(R.id.et_user_reg);
        et_name_reg = (EditText) findViewById(R.id.et_name_reg);
        et_email_reg = (EditText) findViewById(R.id.et_email_reg);
        et_number_reg = (EditText) findViewById(R.id.et_number_reg);
        et_password_reg = (EditText) findViewById(R.id.et_password_reg);
        btn_register_reg = (Button) findViewById(R.id.btn_register_reg);

        /**
         * Add the listeners of the buttons
         */

        btn_register_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User(et_user_reg.getText().toString().trim(), et_name_reg.getText().toString().trim(), et_email_reg.getText().toString().trim(), et_number_reg.getText().toString().trim(), et_password_reg.getText().toString().trim());

                DBController.addUser(user);

            }
        });




    }
}