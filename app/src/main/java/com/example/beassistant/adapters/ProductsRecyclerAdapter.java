package com.example.beassistant.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beassistant.R;
import com.example.beassistant.models.Producto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 */

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Producto> productsList;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    FirebaseStorage storage;
    StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public ProductsRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        productsList = new ArrayList<>();
    }

    //Metodo para borrar un item del recyclerAdapter, borrandolo de la lista
    public void deleteItem(int seleccionado){
        productsList.remove(seleccionado);
        this.notifyDataSetChanged();
        
    }

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(Producto o){
        productsList.add(o);
        this.notifyDataSetChanged();
    }

    //Metodo para modificar un Item del RecyclerAdapter
    public void modItem(int seleccionado,String id,String name, String desc){
        this.notifyDataSetChanged();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product,parent, false);
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

        Producto o = productsList.get(position);

        storageRef.child(o.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgProduct.setImageBitmap(bitmap);
            }
        });

        holder.txtName.setText(o.getName());
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
