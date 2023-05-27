package com.example.beassistant.controllers.logins;

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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginController extends AppCompatActivity {


    // Declare the variables
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    private ImageView btn_google;

    // Declare the firebase variables
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    // Declare the google sing in variables
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

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

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            fillSharedUser();
        }

        /**
         * Inicialice the variables
         */

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        /**
         * Add the listeners of the buttons
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Fill the shared user
                        fillSharedUser();
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

    private void fillSharedUser(){

        db.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()){
                            return;
                        }

                        DocumentSnapshot doc = task.getResult();

                        User user = new User();

                        user.setId(doc.getId());
                        user.setUsername(doc.getString("username"));
                        user.setName(doc.getString("name"));
                        user.setImg_reference(doc.getString("imgRef"));
                        user.setEmail(doc.getString("email"));
                        user.setPassword(doc.getString("password"));
                        user.setNumOpiniones(0);
                        user.setNumSeguidores(0);
                        user.setNumSeguidos(0);

                        Shared.myUser = user;

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct!=null){
            String personalName = acct.getDisplayName();
            String personalEmail = acct.getEmail();
            String name  = acct.getGivenName();


            Log.d("Usuario: ","Nombre: " + personalName +
                                    "\n Email: " + personalEmail +
                                    "\n 1Id: " + acct.getId() +
                                    "\n 2Photo: " + acct.getPhotoUrl() +
                                    "\n 3FamilyName: " + acct.getFamilyName() +
                                    "\n 4Account: " + acct.getAccount().toString()
                                    );
        }
    }

    /*private void generateUser(){

        String id = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("name", name);
        user.put("imgRef", img);
        user.put("email", email);
        user.put("password", password);
        user.put("numOpiniones", 0);
        user.put("numSeguidores", 0);
        user.put("numSeguidos", 0);

        db.collection("users").document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        Intent i = new Intent(getApplicationContext(), LoginController.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Foto de perfil establecida", Toast.LENGTH_LONG).show();
                    }
                });
    }*/
}
