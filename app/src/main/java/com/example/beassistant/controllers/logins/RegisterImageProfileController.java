package com.example.beassistant.controllers.logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterImageProfileController extends AppCompatActivity {

    private String username;
    private String name;
    private String img;
    private String email;
    private String number;
    private String password;

    private Button btn_take;
    private Button btn_register_last;
    private Button btn_getGalleryImg;
    private ImageView img_profile_register;

    //Declare the data base object
    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_image_profile_controller);

        btn_take = (Button) findViewById(R.id.btn_take);
        btn_register_last = (Button) findViewById(R.id.btn_register_last);
        btn_getGalleryImg = (Button) findViewById(R.id.btn_getGalleryImg);
        img_profile_register = (ImageView) findViewById(R.id.img_profile_register);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        Intent i = getIntent();

        username = i.getStringExtra("username");
        name = i.getStringExtra("name");
        img = "profileImages/" + username + "_img_profile.jpg";
        email = i.getStringExtra("email");
        number = i.getStringExtra("number");
        password = i.getStringExtra("password");

        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });

        // Get the image from gallery
        btn_getGalleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a reference to "mountains.jpg"
                StorageReference imgRef = storageRef.child(img);

                // Get the data from an ImageView as bytes
                img_profile_register.setDrawingCacheEnabled(true);
                img_profile_register.buildDrawingCache();

                Bitmap bitmap = ((BitmapDrawable) img_profile_register.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data0 = baos.toByteArray();

                UploadTask uploadTask = imgRef.putBytes(data0);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "No se ha podido subir la imagen", Toast.LENGTH_LONG);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Imagen subida", Toast.LENGTH_LONG);
                    }
                });
            }
        });

        btn_register_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.isEmpty() || name.isEmpty() || img.isEmpty() || email.isEmpty() || number.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }
                generateUser();
            }
        });

    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            img_profile_register.setImageBitmap(imgBitmap);

            // Create a reference to "mountains.jpg"
            StorageReference imgRef = storageRef.child(img);

            // Get the data from an ImageView as bytes
            img_profile_register.setDrawingCacheEnabled(true);
            img_profile_register.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) img_profile_register.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data0 = baos.toByteArray();

            UploadTask uploadTask = imgRef.putBytes(data0);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("Foto: ", "No: " + exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Foto: ", "Si");
                }
            });
        }
    }

    private void generateUser(){

        String id = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("name", name);
        user.put("imgRef", img);
        user.put("email", email);
        user.put("phoneNumber", number);
        user.put("password", password);
        user.put("numOpiniones", 0);
        user.put("numSeguidores", 0);
        user.put("numSeguidos", 0);

        /**
         * Add a new document with a generated ID
         */
        db.collection("users").document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        Intent i = new Intent(getApplicationContext(), LoginController.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Foto de perfil establecida ", Toast.LENGTH_LONG).show();
                    }
                });
    }


}