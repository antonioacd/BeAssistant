package com.example.beassistant.controllers.logins;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.controllers.DataBaseController;
import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterController extends AppCompatActivity {

    EditText et_user_reg;
    EditText et_name_reg;
    EditText et_email_reg;
    EditText et_number_reg;
    EditText et_password_reg;
    Button btn_register_reg;

    private FirebaseAuth mAuth;

    //Declare the data base object
    private FirebaseFirestore db;

    /**
     * Declare the instance of data base controller
     */

    DataBaseController DBController = new DataBaseController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_controller);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

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
                    generateUser();
                }
            }
        });

    }

    private void generateUser(){
        mAuth.createUserWithEmailAndPassword(et_email_reg.getText().toString().trim(), et_password_reg.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("id", id);

                    /**
                     * Add a new document with a generated ID
                     */
                    db.collection("users").document(id)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Usuario creado", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), RegisterImageProfileController.class);
                                    i.putExtra("username", et_user_reg.getText().toString().trim());
                                    i.putExtra("name", et_name_reg.getText().toString().trim());
                                    i.putExtra("email", et_email_reg.getText().toString().trim());
                                    i.putExtra("number", et_number_reg.getText().toString().trim());
                                    i.putExtra("password", et_password_reg.getText().toString().trim());
                                    startActivity(i);
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }

}