package com.example.beassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;
import com.example.beassistant.models.Producto;

import java.util.ArrayList;


/**
 *
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Producto> productList;
    //private CircularProgressDrawable progressDrawable;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    //Constructor de RecyclerAdapter
    public HomeRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        productList = new ArrayList<Producto>();
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
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        TextView txt_brand;
        TextView txt_type;
        TextView txt_price;
        TextView txt_toneColor;
        TextView txt_shopbuy;
        TextView txt_opinion;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            img_product  = (ImageView) itemView.findViewById(R.id.img_product);
            txt_brand = (TextView)  itemView.findViewById(R.id.txt_brand);
            txt_type = (TextView)  itemView.findViewById(R.id.txt_type);
            txt_price = (TextView)  itemView.findViewById(R.id.txt_price);
            txt_toneColor = (TextView)  itemView.findViewById(R.id.txt_toneColor);
            txt_opinion = (TextView)  itemView.findViewById(R.id.txt_opinion);

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
