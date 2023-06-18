package com.example.beassistant.controllers.logins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterImageProfileController extends AppCompatActivity {

    private String img, email, password, action;

    private Button btn_take;
    private Button btn_register_last;
    private Button btn_getGalleryImg;
    private ImageView img_profile_register;

    //Declare the data base object
    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    FirebaseStorage storage;
    StorageReference storageRef;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_image_profile_controller);

        // Init the database variables
        initVariables();

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

    private void initVariables() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
        img = "";
    }

    private void initViewVariables() {
        btn_take = findViewById(R.id.btn_take);
        btn_register_last = findViewById(R.id.btn_register_last);
        btn_getGalleryImg = findViewById(R.id.btn_getGalleryImg);
        img_profile_register = findViewById(R.id.img_profile_register);
    }

    private void getIntentData() {
        Intent i = getIntent();
        email = i.getStringExtra("email");
        password = i.getStringExtra("password");
        action = i.getStringExtra("action");
        img = "profileImages/" + email + "_img_profile.jpg";
        Log.d("imagen: ", img);
    }

    /**
     * Function that contains a listener of the take photo selector
     */
    private void buttonTakeListener() {
        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()) {
                    openCamera();
                }
            }
        });
    }

    /**
     * Function that contains a listener of the register button
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
     * Function that contains a listener of the gallery selector
     */
    private void buttonGetGalleryImgListener() {
        btn_getGalleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkReadExternalStoragePermission()) {
                    openGallery();
                }
            }
        });
    }

    /**
     * Function to check if the camera permission is granted
     */
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false;
        } else {
            // Permission is granted
            return true;
        }
    }

    /**
     * Function to check if the read external storage permission is granted
     */
    private boolean checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_IMAGE_FROM_GALLERY);
            return false;
        } else {
            // Permission is granted
            return true;
        }
    }

    /**
     * Function to open the camera
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Function to open the gallery
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione la Aplicacion"), REQUEST_IMAGE_FROM_GALLERY);
    }

    /**
     * Function to handle the result of the activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Handle the captured image from the camera
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imgBitmap = (Bitmap) extras.get("data");
                img_profile_register.setImageBitmap(imgBitmap);
                StorageReference imgRef = storageRef.child(img);
                uploadImage(imgRef);
            }
        } else if (requestCode == REQUEST_IMAGE_FROM_GALLERY) {
            // Handle the selected image from the gallery
            Uri imageUri = data.getData();
            img_profile_register.setImageURI(imageUri);
            StorageReference imgRef = storageRef.child(img);
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
        byte[] data = baos.toByteArray();

        // Put the image in the database
        UploadTask uploadTask = imgRef.putBytes(data);
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

    private void modifyImage() {

        String id = "";

        FirebaseUser user = mAuth.getCurrentUser();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (user != null){
            id = user.getUid();
            doChange(id);
            return;
        }

        if (acct != null){
            id = acct.getId();
            doChange(id);
            return;
        }
    }

    private void doChange(String id) {
        // Modify the image of the profile
        db.collection("users").document(id)
                .update("imgRef", img)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if (action.equals("mod")){
                            Toast.makeText(getApplicationContext(), "Foto de perfil modificada", Toast.LENGTH_LONG).show();
                            return;
                        }

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
