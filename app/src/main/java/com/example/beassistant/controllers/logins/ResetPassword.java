package com.example.beassistant.controllers.logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText et_email;
    private Button btn_reset_passwsord;

    private FirebaseAuth mAuth;

    private String email = "";

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        et_email = findViewById(R.id.et_email_reset_password);
        btn_reset_passwsord = findViewById(R.id.btn_reset_password);

        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        btn_reset_passwsord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = et_email.getText().toString().trim();

                if (email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe ingresar el email", Toast.LENGTH_SHORT).show();
                    Log.d("Reset:", "email vacio: " + email);
                    return;
                }

                Log.d("Reset:", "espera");
                dialog.setMessage("Espere por favor...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                resetPassword();
            }
        });

    }

    private void resetPassword(){

        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
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