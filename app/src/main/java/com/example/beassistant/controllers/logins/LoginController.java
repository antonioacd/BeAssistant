package com.example.beassistant.controllers.logins;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beassistant.R;
import com.example.beassistant.Shared;
import com.example.beassistant.controllers.MainActivity;
import com.example.beassistant.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginController extends AppCompatActivity {

    /**
     * Declare the variables
     */

    EditText et_user;
    EditText et_password;
    Button btn_login;
    Button btn_register;

    //Declare the data base object
    private FirebaseFirestore db;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView btn_google;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_google = findViewById(R.id.btn_google);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        db = FirebaseFirestore.getInstance();

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

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                // Don't exist = -1, exist and password is correct = 0, exist but incorrect password = 1
                                int response = -1;

                                User user = new User();

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        String password = doc.getString("password");
                                        if (doc.getString("username").equals(et_user.getText().toString().trim())) {
                                            if (password.equals(et_password.getText().toString())) {
                                                response = 0;
                                                    user.setUsername(doc.getString("username"));
                                                    user.setName(doc.getString("name"));
                                                    user.setImg_reference(doc.getString("imgRef"));
                                                    user.setEmail(doc.getString("email"));
                                                    user.setNumber(doc.getString("phoneNumber"));
                                                    user.setPassword(doc.getString("password"));
                                            } else {
                                                response = 1;
                                            }
                                        }
                                        Log.d("Datos:", "Clave: " + doc.getString("username") + ", Valor: " + password);
                                    }

                                    if (response == 0) {
                                        Toast.makeText(getApplicationContext(), "Correcto: " + user.toString(), Toast.LENGTH_LONG).show();
                                        Shared.myUser = user;
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                    } else if (response == 1) {
                                        Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Usuario no registrado", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterController.class);
                startActivity(i);
            }
        });
    }

    private void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Log.d("Error: ", "Error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Algo fue mal", Toast.LENGTH_LONG).show();
            }
        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginController.this, SecondActivity.class);
        startActivity(intent);
    }
}
