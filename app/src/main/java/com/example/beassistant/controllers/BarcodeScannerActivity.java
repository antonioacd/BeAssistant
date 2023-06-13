package com.example.beassistant.controllers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beassistant.R;
import com.example.beassistant.fragments.home.DetailsProductFragment;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.List;

public class BarcodeScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private CompoundBarcodeView barcodeView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);

        db = FirebaseFirestore.getInstance();

        // Solicitar permiso de cámara si no se ha concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startBarcodeScanner();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startBarcodeScanner();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // No es necesario hacer nada aquí
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // No es necesario hacer nada aquí
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner();
            } else {
                Toast.makeText(this, "La aplicación necesita acceso a la cámara", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getProduct(String reference){
        db.collectionGroup("productos").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("Query:", "Entra");
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {

                            Log.d("Referencia01: ", doc.getId());

                            if (!doc.getId().equals(reference)){
                                Log.d("Referencia01: ", "-" + doc.getId() + "- - -" + reference + "-");
                                continue;
                            }

                            Log.d("Referencia: ", doc.getId());

                            Product product = new Product(
                                    doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("imgRef"),
                                    doc.getString("brand"),
                                    doc.getString("category"),
                                    doc.getString("type"),
                                    doc.getDouble("rating"),
                                    doc.getString("url")
                            );

                            // Detener la lectura continua después de obtener un resultado válido
                            barcodeView.pause();

                            finish();
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            Fragment fragment = new DetailsProductFragment();
                            Bundle args = new Bundle();
                            args.putString("id", product.getUuID());
                            args.putString("name", product.getName());
                            args.putString("brand", product.getBrand());
                            args.putString("type", product.getType());
                            args.putDouble("mediaRating", product.getMediaRating());
                            args.putString("imgRef", product.getImg_reference());
                            args.putString("url", product.getUrl());

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.setFragmentResult("keyProduct", args);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            // Return to the home fragment

                        }
                    }
                });
    }


    private void startBarcodeScanner() {
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Resultado del escaneo del código de barras
                String barcode = result.getText();

                Log.d("Barcode:", barcode);

                getProduct(barcode);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // No es necesario hacer nada aquí
            }
        });
    }
}
