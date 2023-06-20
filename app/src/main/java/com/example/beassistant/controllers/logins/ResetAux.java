package com.example.beassistant.controllers.logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.models.Shared;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetAux extends AppCompatActivity {

    private EditText et_aux;
    private Button btn_aux;
    private TextInputLayout til_aux;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String auxString = "";

    private ProgressDialog dialog;

    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        et_aux = findViewById(R.id.et_aux_reset);
        btn_aux = findViewById(R.id.btn_aux_reset);
        til_aux = findViewById(R.id.til_aux);

        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        action = i.getStringExtra("action");

        switch (action){
            case "password":
                til_aux.setHint("Email");
                til_aux.setStartIconDrawable(R.drawable.ic_baseline_email_24);
                btn_aux.setText("Enviar correo de recuperación");
                break;
            case "user":
                til_aux.setHint("Nuevo Usuario");
                til_aux.setStartIconDrawable(R.drawable.ic_baseline_person_24);
                btn_aux.setText("Cambiar Usuario");
                break;
            case "name":
                til_aux.setHint("Nuevo Nombre");
                til_aux.setStartIconDrawable(R.drawable.ic_baseline_person_24);
                btn_aux.setText("Cambiar Nombre");
                break;
        }

        btn_aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (action){
                    case "password":
                        resetAction("email");
                        break;
                    case "user":
                        resetAction("usuario");
                        break;
                    case "name":
                        resetAction("nombre");
                        break;
                }
            }
        });

    }

    private void resetAction(String type) {
        auxString = et_aux.getText().toString().trim();

        if (auxString.isEmpty()){
            Toast.makeText(getApplicationContext(), "Debe ingresar el " + type, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Reset:", "espera");
        dialog.setMessage("Espere por favor...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        switch (type){
            case "email":
                resetPassword();
                break;
            case "usuario":
                resetUserNameAction();
                break;
            case "nombre":
                resetNameAction();
                break;
        }
    }

    private void resetUserNameAction(){

        db.collection("users")
                .document(Shared.myUser.getUserId())
                .update("username", auxString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "No ha sido posible reestablecer el usuario", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        Toast.makeText(getApplicationContext(), "El usuario ha sido modificado con exito", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void resetNameAction(){

        db.collection("users")
                .document(Shared.myUser.getUserId())
                .update("name", auxString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "No ha sido posible reestablecer el nombre", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        Toast.makeText(getApplicationContext(), "El nombre ha sido modificado con exito", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void resetPassword(){

        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(auxString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("Reset:", "entra");

                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "No se pudo enviar el correo, intentelo de nuevo en unos minutos", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Log.d("Reset:", "no");
                    return;
                }

                Toast.makeText(getApplicationContext(), "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                Log.d("Reset:", "si");
                dialog.dismiss();
            }
        });
    }

}