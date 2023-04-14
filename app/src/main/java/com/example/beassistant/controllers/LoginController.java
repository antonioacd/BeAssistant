package com.example.beassistant.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beassistant.R;

public class LoginController extends AppCompatActivity {

    /**
     * Declare the variables
     */

    EditText et_user;
    EditText et_password;
    Button btn_login;
    Button btn_register;

    /**
     * Declare the instance of data base controller
     */

    DataBaseController DBController = new DataBaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DBController.getLogins();

        /**
         * Inicialice the variables
         */

        et_user = (EditText) findViewById(R.id.et_user);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        /**
         * Add the listeners of the buttons
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Don't exist = -1, exist and password is correct = 0, exist but incorrect password = 1
                int response = -1;
                for (String username : DBController.listLogUsers.keySet()) {
                    String password = DBController.listLogUsers.get(username);
                    if (username.equals(et_user.getText().toString().trim())){
                        if (password.equals(et_password.getText().toString())){
                            response = 0;
                        }else{
                            response = 1;
                        }
                    }
                    Log.d("Datos:", "Clave: " + username + ", Valor: " + password);
                }
                if (response == 0){
                    Toast.makeText(getApplicationContext(), "Correcto", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), ScreenActivity.class);
                    startActivity(i);
                }else if (response == 1){
                    Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBController.getLogins();

                Intent i = new Intent(getApplicationContext(), RegisterController.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBController.getLogins();
    }
}
