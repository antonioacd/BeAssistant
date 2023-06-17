package com.example.beassistant.controllers.logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterImageProfileController extends AppCompatActivity {

    private String img, email, password;

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

        // Init the database variables
        initDatabaseVariables();

        // Init the view variables
        initViewVariables();

        // Get intent data
        getIntentData();

        // Listener of take foto button
        buttonTakeListener();

        //Listener of get gallery image button
        buttonGetGalleryImgListener();

        // Listener of register button
        buttonRegisterListener();
    }

    private void initDatabaseVariables() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    private void initViewVariables() {
        btn_take = (Button) findViewById(R.id.btn_take);
        btn_register_last = (Button) findViewById(R.id.btn_register_last);
        btn_getGalleryImg = (Button) findViewById(R.id.btn_getGalleryImg);
        img_profile_register = (ImageView) findViewById(R.id.img_profile_register);
    }

    private void getIntentData() {
        Intent i = getIntent();
        email = i.getStringExtra("email");
        password = i.getStringExtra("password");
        img = "profileImages/" + email + "_img_profile.jpg";
    }

    /**
     * Funtion that contains a listener of the take photo selector
     */
    private void buttonTakeListener() {
        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    /**
     * Funtion that contains a listener of the register button
     */
    private void buttonRegisterListener() {
        btn_register_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyImage();
            }
        });
    }

    /**
     * Funtion that contains a listener of the gallery selector
     */
    private void buttonGetGalleryImgListener() {
        // Get the image from gallery
        btn_getGalleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start new gallery intent
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/");
                startActivityForResult(i.createChooser(i, "Seleccione la Aplicacion"), 10);
            }
        });
    }

    /**
     * Function to open the camera
     */
    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }

    /**
     * Function to get the result of the activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        // If take photo request code
        if (requestCode == 1) {

            // Get the extras
            Bundle extras = data.getExtras();

            // Get the data
            Bitmap imgBitmap = (Bitmap) extras.get("data");

            // Set the image in the image view
            img_profile_register.setImageBitmap(imgBitmap);

            // Create a reference to the profile image
            StorageReference imgRef = storageRef.child(img);

            uploadImage(imgRef);
        }

        if (requestCode == 10) {

            // Get the path
            Uri path = data.getData();

            // Set the image in the image view
            img_profile_register.setImageURI(path);

            // Create a reference to the image
            StorageReference imgRef = storageRef.child(img);

            // Get the data from an ImageView as bytes
            uploadImage(imgRef);
        }
    }

    /**
     * Function to upload the image to the database
     * @param imgRef
     */
    private void uploadImage(StorageReference imgRef) {
        // Get the data of the bitmap in array of bytes
        img_profile_register.setDrawingCacheEnabled(true);
        img_profile_register.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) img_profile_register.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data0 = baos.toByteArray();

        // Put the image in the database
        UploadTask uploadTask = imgRef.putBytes(data0);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la imagen", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    private void modifyImage(){

        String id = mAuth.getCurrentUser().getUid();

        // Modify the image of the profile
        db.collection("users").document(id)
                .update("imgRef", img)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        Intent i = new Intent(getApplicationContext(), LoginController.class);
                        i.putExtra("email", email);
                        i.putExtra("password", password);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Foto de perfil establecida ", Toast.LENGTH_LONG).show();
                    }
                });
    }
}