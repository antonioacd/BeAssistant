package com.example.beassistant.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 *
 */

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Product> productList;

    //Declaramos los listener de nuestro RecyclerAdapter
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    private Context contexto;

    private FirebaseFirestore db;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public ProductsRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        this.productList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        return recyclerHolder;
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        Product objeto = productList.get(position);

        storageRef.child(objeto.getImgReference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.img_product.setImageBitmap(bitmap);
            }
        });

        holder.txt_name.setText(objeto.getProductName());
        holder.txt_brand.setText(objeto.getBrand());
        holder.txt_type.setText(objeto.getType());
        double ratingRounded = Math.round((objeto.getMediaRating()) * 10.0) / 10.0;
        holder.txt_media_rating.setText(String.valueOf(ratingRounded) + " ⭐");

    }

    @Override
    public int getItemCount() {
        Log.d("Query: ", "Tamaño: " + productList.size());
        return productList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        TextView txt_brand, txt_type, txt_name, txt_media_rating;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            img_product  = (ImageView) itemView.findViewById(R.id.img_product);
            txt_brand = (TextView)  itemView.findViewById(R.id.txt_brand);
            txt_type = (TextView)  itemView.findViewById(R.id.txt_type);
            txt_media_rating = (TextView)  itemView.findViewById(R.id.txt_media_rating);
            txt_name = (TextView) itemView.findViewById(R.id.txt_list_name);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
