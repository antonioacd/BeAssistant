package com.example.beassistant.adapters;

import static android.content.ContentValues.TAG;

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
import com.example.beassistant.Shared;
import com.example.beassistant.models.Opinion;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Product> productList;
    //private CircularProgressDrawable progressDrawable;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public HomeRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        productList = new ArrayList<Product>();
    }

    /*//Metodo para borrar un item del recyclerAdapter, borrandolo de la lista
    public void deleteItem(int seleccionado){
        productList.remove(seleccionado);
        this.notifyDataSetChanged();
        
    }*/

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(Product p){
        productList.add(p);
        this.notifyDataSetChanged();
    }

    /*//Metodo para modificar un Item del RecyclerAdapter
    public void modItem(int seleccionado,String id,String name, String desc){

        productList.get(seleccionado).setTitulo(name);
        productList.get(seleccionado).setDescripcion(desc);
        productList.get(seleccionado).setFotoId(id);

        this.notifyDataSetChanged();
    }*/

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        return recyclerHolder;
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        /*progressDrawable = new CircularProgressDrawable(contexto);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();


        holder.txtViewDesc.setText(objeto.getDescripcion());
        holder.txtViewTitle.setText(objeto.getTitulo());
        Glide.with(contexto)
                .load(objeto.getFotoId())
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher)
                .into(holder.img);*/

        Product objeto = productList.get(position);
        //Log.d("Lista", "pasa");

        storageRef.child(objeto.getImg_reference()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.img_product.setImageBitmap(bitmap);
            }
        });

        holder.txt_name.setText(objeto.getName());
        holder.txt_brand.setText(objeto.getBrand());
        holder.txt_type.setText(objeto.getType());
        holder.txt_media_rating.setText(String.valueOf(objeto.getMediaRating()));

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        TextView txt_brand;
        TextView txt_type;
        TextView txt_name;
        TextView txt_media_rating;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            img_product  = (ImageView) itemView.findViewById(R.id.img_product);
            txt_brand = (TextView)  itemView.findViewById(R.id.txt_brand);
            txt_type = (TextView)  itemView.findViewById(R.id.txt_type);
            txt_media_rating = (TextView)  itemView.findViewById(R.id.txt_media_rating);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name_list);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
