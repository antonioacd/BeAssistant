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
import com.example.beassistant.models.UserInAList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 *
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.RecyclerHolder>{

    public ArrayList<UserInAList> usersList;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    FirebaseStorage storage;
    StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public UsersRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        usersList = new ArrayList<>();
    }

    //Metodo para borrar un item del recyclerAdapter, borrandolo de la lista
    public void deleteItem(int seleccionado){
        usersList.remove(seleccionado);
        this.notifyDataSetChanged();
        
    }

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(UserInAList o){
        usersList.add(o);
        this.notifyDataSetChanged();
    }

    //Metodo para modificar un Item del RecyclerAdapter
    public void modItem(int seleccionado,String id,String name, String desc){
        this.notifyDataSetChanged();
    }

    public void setFilteredList(ArrayList<UserInAList> filteredList){
        usersList = filteredList;
        notifyDataSetChanged();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower,parent, false);
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

        UserInAList p = usersList.get(position);

        storageRef.child(p.getImgRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgUser.setImageBitmap(bitmap);
            }
        });
        
        holder.txt_username.setText(p.getUsername());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txt_username;
        ImageView imgUser;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txt_username = (TextView) itemView.findViewById(R.id.txt_follower_name);
            imgUser = (ImageView) itemView.findViewById(R.id.img_profile_user);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
