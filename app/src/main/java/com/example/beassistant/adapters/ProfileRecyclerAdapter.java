package com.example.beassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.RecyclerHolder>{

    public ArrayList<String> categoryList;
    //private CircularProgressDrawable progressDrawable;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    //Constructor de RecyclerAdapter
    public ProfileRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        categoryList = new ArrayList<String>(Arrays.asList("Cara", "Labios", "Ojos", "Cejas"));
    }

    /*//Metodo para borrar un item del recyclerAdapter, borrandolo de la lista
    public void deleteItem(int seleccionado){
        productList.remove(seleccionado);
        this.notifyDataSetChanged();
        
    }

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(Objeto o){
        productList.add(o);
        this.notifyDataSetChanged();
    }

    //Metodo para modificar un Item del RecyclerAdapter
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

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

        Objeto objeto = productList.get(position);
        holder.txtViewDesc.setText(objeto.getDescripcion());
        holder.txtViewTitle.setText(objeto.getTitulo());
        Glide.with(contexto)
                .load(objeto.getFotoId())
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher)
                .into(holder.img);*/

        Object o = categoryList.get(position);
        holder.txt_category.setText(o.toString());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txt_category;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txt_category = (TextView) itemView.findViewById(R.id.txt_item);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
