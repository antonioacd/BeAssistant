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
import com.example.beassistant.models.Opinion;
import com.example.beassistant.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 *
 */

public class OpinionsRecyclerAdapter extends RecyclerView.Adapter<OpinionsRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Opinion> opinionsList;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    FirebaseStorage storage;
    StorageReference storageRef;

    // Declare the database contoller
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Constructor de RecyclerAdapter
    public OpinionsRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        opinionsList = new ArrayList<>();
    }

    //Metodo para borrar un item del recyclerAdapter, borrandolo de la lista
    public void deleteItem(int seleccionado){
        opinionsList.remove(seleccionado);
        this.notifyDataSetChanged();

    }

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(Opinion o){
        opinionsList.add(o);
        this.notifyDataSetChanged();
    }

    //Metodo para modificar un Item del RecyclerAdapter
    public void modItem(int seleccionado,String id,String name, String desc){
        this.notifyDataSetChanged();
    }

    public void setFilteredList(ArrayList<Opinion> filteredList){
        opinionsList = filteredList;
        notifyDataSetChanged();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opinion_item,parent, false);
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

        Opinion p = opinionsList.get(position);

        db.collection("opiniones")
                .document(p.getOpinionId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        DocumentSnapshot doc = task.getResult();

                        db.collection("users")
                                .document(doc.getString("userId"))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            return;
                                        }
                                        DocumentSnapshot document = task.getResult();

                                        storageRef.child(document.getString("imgRef")).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                holder.img_user_profile.setImageBitmap(bitmap);
                                            }
                                        });

                                        holder.txt_username.setText(document.getString("username"));
                                        holder.txt_rating.setText(String.valueOf(doc.getDouble("rating")) + " ⭐");
                                        holder.txt_price.setText(String.valueOf(doc.getDouble("price")) + "€");
                                        holder.txt_shopBuy.setText(doc.getString("shopBuy"));
                                        holder.txt_toneOrColor.setText(doc.getString("toneOrColor"));
                                        holder.txt_opinion.setText(doc.getString("opinion"));

                                    }
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return opinionsList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txt_username, txt_rating, txt_price, txt_shopBuy, txt_toneOrColor, txt_opinion;
        ImageView img_user_profile;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txt_username = (TextView) itemView.findViewById(R.id.txt_username_opinion_item);
            txt_rating = (TextView) itemView.findViewById(R.id.txt_rating);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_shopBuy = (TextView) itemView.findViewById(R.id.txt_shopBuy);
            txt_toneOrColor = (TextView) itemView.findViewById(R.id.txt_toneOrColor);
            txt_opinion = (TextView) itemView.findViewById(R.id.txt_opinion);
            img_user_profile = (ImageView) itemView.findViewById(R.id.img_user_profile);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}