package com.example.beassistant.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;



public class BarcodeScannerFragment extends Fragment implements SurfaceHolder.Callback{

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private CompoundBarcodeView barcodeView;

    private FirebaseFirestore db;

    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public BarcodeScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barcode_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeView = view.findViewById(R.id.barcode_scanner);

        db = FirebaseFirestore.getInstance();

        scancode();

        // Iniciar el escaneo de códigos de barras si se ha otorgado el permiso
        //startBarcodeScanner();

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null){
            Log.d("EscanerL: ", result.getContents());
        }
    });

    private void scancode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ActivityResultLauncher.class);
        barLauncher.launch(options);
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
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
                // Permiso de cámara concedido, iniciar el escaneo de códigos de barras
                startBarcodeScanner();
            } else {
                Toast.makeText(getContext(), "La aplicación necesita acceso a la cámara", Toast.LENGTH_SHORT).show();
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

                            Log.d("Referencia: ", product.toString());

                            Fragment fragment = new DetailsProductFragment();
                            Bundle args = new Bundle();
                            args.putString("id", product.getUuID());
                            args.putString("name", product.getName());
                            args.putString("brand", product.getBrand());
                            args.putString("type", product.getType());
                            args.putDouble("mediaRating", product.getMediaRating());
                            args.putString("imgRef", product.getImg_reference());
                            args.putString("url", product.getUrl());

                            Log.d("Args: ", args.toString());

                            FragmentManager fragmentManager = getParentFragmentManager();
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
                // Detener la lectura continua después de obtener un resultado válido
                barcodeView.pause();
                getProduct(barcode);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // No es necesario hacer nada aquí
            }
        });
    }
}