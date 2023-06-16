package com.example.beassistant.controllers.logins;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginController extends AppCompatActivity {


    // Declare the variables
    private EditText et_email, et_password;
    private Button btn_login, btn_register, btn_reset;
    private ImageView btn_google;

    // Declare the firebase variables
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    // Declare the google sing in variables
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initDatabaseVariables();

        initVariables();

        initViewVariables();

        fillUserFields();

        buttonGoogleListener();

        buttonLoginListener();

        buttonRegisterListener();

        buttonResetListener();
    }

    private void fillUserFields(){
        Intent i = getIntent();

        i.getExtras();

        if (i == null || i.getExtras() == null) {
            return;
        }

        Log.d("Intent:", i.getExtras().toString());

        String email = "", password = "";

        email = i.getStringExtra("email");
        password = i.getStringExtra("password");

        if (email.isEmpty() || password.isEmpty()){
            return;
        }

        et_email.setText(email);
        et_password.setText(password);
    }

    private void buttonResetListener() {
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ResetAux.class);
                i.putExtra("action", "password");
                startActivity(i);
            }
        });
    }

    private void buttonRegisterListener() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Sesion: ", "Registrar");
                Intent i = new Intent(getApplicationContext(), RegisterController.class);
                startActivity(i);
            }
        });
    }

    private void buttonLoginListener() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                dialog.setMessage("Iniciando sesión...\nEspere por favor");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if (et_email.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()){
                   Toast.makeText(LoginController.this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
                   dialog.dismiss();
                   return;
                }

                Log.d("Sesion: ", user + ". " + password);

                firebaseAuth.signInWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Sesion: ", "No iniciada");
                            Toast.makeText(LoginController.this, "Algo ha fallado, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        // Fill the shared user
                        fillSharedUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Sesion: ", e.getMessage());
                    }
                });

            }
        });
    }

    private void buttonGoogleListener() {
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void initVariables() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        dialog = new ProgressDialog(this);
    }

    private void initViewVariables() {
        btn_google = findViewById(R.id.btn_google);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_reset = (Button) findViewById(R.id.btn_reset);
    }

    private void initDatabaseVariables() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    protected void fillSharedUser(){

        // Get the current user
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Check if task is successful
                        if (!task.isSuccessful()){
                            return;
                        }

                        DocumentSnapshot doc = task.getResult();

                        User user = new User();

                        user.setUserId(doc.getId());
                        user.setUsername(doc.getString("username"));
                        user.setName(doc.getString("name"));
                        user.setImg_reference(doc.getString("imgRef"));
                        user.setEmail(doc.getString("email"));
                        user.setNumOpiniones(doc.getDouble("numOpiniones").intValue());
                        user.setNumSeguidores(doc.getDouble("numSeguidores").intValue());
                        user.setNumSeguidos(doc.getDouble("numSeguidos").intValue());

                        Shared.myUser = user;

                        dialog.dismiss();
                        Toast.makeText(LoginController.this, "¡ Bienvenido " + user.getUsername() + " !", Toast.LENGTH_SHORT).show();
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
                onSignInGoogle();
            } catch (ApiException e) {
                Log.d("Error: ", "Error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Algo fue mal", Toast.LENGTH_LONG).show();
            }
        }
    }

    void onSignInGoogle() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct == null) {
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", acct.getId());
        user.put("username", acct.getDisplayName() + acct.getId());
        user.put("name", acct.getDisplayName());
        user.put("imgRef", "/profileImages/defaultprofile.png");
        user.put("email", acct.getEmail());
        user.put("numOpiniones", 0);
        user.put("numSeguidores", 0);
        user.put("numSeguidos", 0);

        /**
         * Add a new document with a generated ID
         */
        db.collection("users").document(acct.getId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        fillGoogleUser(acct);

                        Toast.makeText(getApplicationContext(), "Sesion iniciada", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Function to fill the google user
     */
    protected void fillGoogleUser(GoogleSignInAccount acct){

        db.collection("users").document(acct.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // Check if task is successful
                if (!task.isSuccessful()){
                    return;
                }

                DocumentSnapshot doc = task.getResult();

                User user = new User();

                user.setUserId(acct.getId());
                user.setUsername(acct.getDisplayName() + acct.getId());
                user.setName(acct.getDisplayName());
                user.setImg_reference("/profileImages/default-profile.png");
                user.setEmail(acct.getEmail());
                user.setNumOpiniones(doc.getDouble("numOpiniones").intValue());
                user.setNumSeguidores(doc.getDouble("numSeguidores").intValue());
                user.setNumSeguidos(doc.getDouble("numSeguidos").intValue());

                Shared.myUser = user;

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

}
