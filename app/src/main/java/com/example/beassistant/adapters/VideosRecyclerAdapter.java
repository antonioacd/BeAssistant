package com.example.beassistant.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;
import com.example.beassistant.models.YoutubeData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 *
 */

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.RecyclerHolder>{

    public ArrayList<YoutubeData> videosList;

    //Declaramos los listener de nuestro RecyclerAdapter
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    private Context contexto;

    private FirebaseFirestore db;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    //Constructor de RecyclerAdapter
    public VideosRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        this.videosList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        return recyclerHolder;
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        YoutubeData video = videosList.get(position);

        holder.txt_title.setText(video.getTitle());
        holder.txt_des.setText(video.getDescription());
        holder.txt_date.setText(video.getPublished());

        Picasso.get().load(video.getThumbnail()).into(holder.img_thumb);
    }

    @Override
    public int getItemCount() {
        Log.d("Query: ", "Tamaño: " + videosList.size());
        return videosList.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        ImageView img_thumb;
        TextView txt_title, txt_des, txt_date;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            img_thumb = (ImageView) itemView.findViewById(R.id.img_video);
            txt_title = (TextView)  itemView.findViewById(R.id.txt_video_title);
            txt_des = (TextView)  itemView.findViewById(R.id.txt_video_desc);
            txt_date = (TextView)  itemView.findViewById(R.id.txt_video_date);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
