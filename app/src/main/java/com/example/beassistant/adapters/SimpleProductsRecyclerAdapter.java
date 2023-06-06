package com.example.beassistant.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 *
 */

public class SimpleProductsRecyclerAdapter extends RecyclerView.Adapter<SimpleProductsRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Product> productsList;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    FirebaseStorage storage;
    StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public SimpleProductsRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        productsList = new ArrayList<>();
    }

    public void setFilteredList(ArrayList<Product> filteredList){
        productsList = filteredList;
        notifyDataSetChanged();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        return recyclerHolder;
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        Product p = productsList.get(position);

        storageRef.child(p.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgProduct.setImageBitmap(bitmap);
            }
        });
        
        holder.txtName.setText(p.getName());
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgProduct;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txt_name_list);
            imgProduct = (ImageView) itemView.findViewById(R.id.imgProductList);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
