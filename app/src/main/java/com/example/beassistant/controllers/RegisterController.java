package com.example.beassistant.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        DBController.getUsernames();

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

                // bad user = -1 || bad email = 1 || correct = 0

                if (et_user_reg.getText().toString().isEmpty() || et_name_reg.getText().toString().isEmpty() || et_email_reg.getText().toString().isEmpty() || et_number_reg.getText().toString().isEmpty() || et_password_reg.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
                }else{
                    int response = 0;

                    for (String username : DBController.listUsernames.keySet()) {
                        String email = DBController.listUsernames.get(username);
                        if (username.equals(et_user_reg.getText().toString().trim()) || email.equals(et_email_reg.getText().toString().trim())) {
                            if (username.equals(et_user_reg.getText().toString().trim())) {
                                response = -1;
                            } else {
                                response = 1;
                            }
                        }
                    }

                    if (response == -1){
                        Toast.makeText(getApplicationContext(), "Ese usuario ya existe", Toast.LENGTH_LONG).show();
                    }else if (response == 1){
                        Toast.makeText(getApplicationContext(), "Ese email ya existe", Toast.LENGTH_LONG).show();
                    }else{
                        User user = new User(
                                et_user_reg.getText().toString().trim(),
                                et_name_reg.getText().toString().trim(),
                                et_email_reg.getText().toString().trim(),
                                et_number_reg.getText().toString().trim(),
                                et_password_reg.getText().toString().trim());

                        DBController.addUser(user);

                        Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        DBController.getUsernames();
    }

}