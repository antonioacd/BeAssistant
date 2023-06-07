package com.example.beassistant.controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.beassistant.R;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.List;

public class BarcodeScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private CompoundBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);

        // Solicitar permiso de cámara si no se ha concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
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
                Toast.makeText(this, "La aplicación necesita acceso a la cámara",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startBarcodeScanner() {
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Resultado del escaneo del código de barras
                String barcode = result.getText();
                Log.d("BarcodeScannerActivity", "Código de barras: " + barcode);

                // Puedes realizar acciones adicionales con el código de barras aquí

                // Detener la lectura continua después de obtener un resultado válido
                barcodeView.pause();
                finish();
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // No es necesario hacer nada aquí
            }
        });
    }
}
