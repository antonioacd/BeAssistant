package com.example.beassistant.controllers.addOpinion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beassistant.R;
import com.example.beassistant.models.Shared;
import com.example.beassistant.controllers.main.MainActivity;
import com.example.beassistant.models.Opinion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddOpinionActivity extends AppCompatActivity {

    String productId, category, brand;
    ImageView img_product;
    TextView txt_product_name;
    TextView txt_product_brand;
    TextView txt_product_type, txt_product_media_rating;

    int selected_rating = -1;
    ArrayList<Integer> number_ratings = new ArrayList<>(Arrays.asList(0,1,2,3,4,5));
    AutoCompleteTextView select_rating;
    ArrayAdapter<Integer> adapterItems;

    EditText et_shopBuy;
    EditText et_price;
    EditText et_toneOrColor;
    EditText et_opinion;

    FloatingActionButton btn_okey;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opinion);

        // Init the database variables
        initVariables();

        // Get the intent values
        getTheIntentValues();

        // Init the view variables
        initViewVariables();

        // Get the selected product
        getProductData();

        // Init the adapter and set the listener
        adapterRatingConfiguration();

        // Set the listener
        btnOkeyListener();

    }

    /**
     * Fucntion to the bnt okey listener
     */
    private void btnOkeyListener(){
        // Set the listener
        btn_okey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the fields are empty
                if (et_shopBuy.getText().toString().isEmpty() || et_price.getText().toString().isEmpty() || et_toneOrColor.getText().toString().isEmpty() || et_opinion.getText().toString().isEmpty() || selected_rating == -1){
                    Toast.makeText(getApplicationContext(), "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create the opinion
                createOpinion();

                // Return to the home fragment
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

        });
    }

    /**
     * Function to create an opinion
     */
    private void createOpinion(){

        // Create the empty opinion
        Opinion op = new Opinion();

        // Add the data to the opinion
        op.setOpinionId(java.util.UUID.randomUUID().toString().trim());
        op.setUsername(Shared.myUser.getUsername());
        op.setProductId(productId);
        op.setProductCategory(category);
        op.setProductBrand(brand);
        op.setImgUser(Shared.myUser.getImg_reference());
        op.setRating(selected_rating);
        op.setPrice(Double.parseDouble(et_price.getText().toString().trim()));
        op.setShopBuy(et_shopBuy.getText().toString().trim());
        op.setToneOrColor(et_toneOrColor.getText().toString().trim());
        op.setOpinion(et_opinion.getText().toString().trim());

        // Crete the map
        Map<String, Object> newOpinion = new HashMap<>();
        newOpinion.put("opinionId", op.getOpinionId());
        newOpinion.put("productId", op.getProductId());
        newOpinion.put("productCategory", op.getProductCategory());
        newOpinion.put("productBrand", op.getProductBrand());
        newOpinion.put("userId", Shared.myUser.getUserId());
        newOpinion.put("username", op.getUsername());
        newOpinion.put("imgUserRef", op.getImgUser());
        newOpinion.put("shopBuy", op.getShopBuy());
        newOpinion.put("price", op.getPrice());
        newOpinion.put("toneOrColor", op.getToneOrColor());
        newOpinion.put("opinion", op.getOpinion());
        newOpinion.put("rating", op.getRating());

        // Add the map to the opinions table
        db.collection("opiniones").document(op.getOpinionId())
                .set(newOpinion).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Notify
                        Toast.makeText(getApplicationContext(), "Opinion Añadida", Toast.LENGTH_LONG).show();
                    }
                });

        // Update the rating
        updateRatingMedia();
    }

    /**
     * Function to configure the adapter
     */
    private void adapterRatingConfiguration(){
        // Create the adapter
        adapterItems = new ArrayAdapter<>(this,R.layout.list_item02, number_ratings);

        // Set the adapter
        select_rating.setAdapter(adapterItems);

        // Set the listener
        select_rating.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_rating = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }
        });
    }

    /**
     * Function to update the media
     */
    private void updateRatingMedia(){

        // Get the opinions of the product
        db.collection("opiniones").whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int cont = 0;
                        double sumatory = 0;

                        // Loop the documents
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the count of the documents
                            cont++;

                            // Add the rating to the sumatory
                            sumatory += document.getDouble("rating");

                        }

                        // Set the final sumatory and final count
                        double finalSumatory = sumatory;
                        int finalCont = cont;

                        // Get the product to get the id
                        db.collection("categorias/"+category+"/marcas/"+brand+"/productos/").whereEqualTo("id",productId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        // Do the media
                                        double media = finalSumatory / finalCont;
                                        // Loop the result
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Update the product
                                            db.collection("categorias/"+category+"/marcas/"+brand+"/productos/").document(document.getId()).update("rating", media);
                                        }
                                    }
                                });
                    }
                });
    }

    /**
     * Function to init the variables
     */
    private void initVariables(){
        // Generate the instance
        db = FirebaseFirestore.getInstance();

        // Generate the storage instance
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    /**
     * Function to get the intent values
     */
    private void getTheIntentValues(){
        // Get the intent
        Intent i = getIntent();

        // Set the data intent
        productId = i.getStringExtra("id");
        category = i.getStringExtra("category");
        brand = i.getStringExtra("brand");
    }

    /**
     * Function to init the view variables
     */
    private void initViewVariables(){
        img_product = findViewById(R.id.img_product_ref);
        txt_product_name = findViewById(R.id.txt_product_name);
        txt_product_brand = findViewById(R.id.txt_product_brand);
        txt_product_type = findViewById(R.id.txt_product_type);
        txt_product_media_rating = findViewById(R.id.txt_product_media_rating);
        et_shopBuy = findViewById(R.id.et_shopBuy);
        et_price = findViewById(R.id.et_price);
        et_toneOrColor = findViewById(R.id.et_toneOrColor);
        et_opinion = findViewById(R.id.et_opinion);
        btn_okey = findViewById(R.id.btn_okey);
        select_rating = findViewById(R.id.select_rating);
    }

    /**
     * Function to get the product data
     */
    private void getProductData(){
        // Get the products of the received category and brand
        db.collection("categorias/"+category+"/marcas/"+brand+"/productos")
                .document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // Get the document if the id is the same that selected
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get the document
                        DocumentSnapshot productsDocument = task.getResult();

                        // Set the image
                        storageRef.child(productsDocument.getString("imgRef")).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                img_product.setImageBitmap(bitmap);
                            }
                        });

                        // Set the text of the view texts view
                        txt_product_name.setText(productsDocument.getString("name"));
                        txt_product_brand.setText(productsDocument.getString("brand"));
                        txt_product_type.setText(productsDocument.getString("type"));
                        txt_product_media_rating.setText(Math.round(productsDocument.getDouble("rating") * 100.0) / 100.0 + " ⭐");
                    }
                });
    }
}