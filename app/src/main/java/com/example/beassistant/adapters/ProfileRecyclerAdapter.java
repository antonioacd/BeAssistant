package com.example.beassistant.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;
import com.example.beassistant.models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.RecyclerHolder>{

    public ArrayList<Category> categoryList;
    //private CircularProgressDrawable progressDrawable;

    //Declaramos los listener de nuestro RecyclerAdapter
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;

    Context contexto;

    // Declare the data base object
    private FirebaseFirestore db;

    //Constructor de RecyclerAdapter
    public ProfileRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        //Init the category list
        categoryList = new ArrayList<>();
        // Get the database instance
        db = FirebaseFirestore.getInstance();

    }

    /**
     * Create the Holder view
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_profile_item,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        //asignamos los listener a nuestra vista
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        return recyclerHolder;
    }

    /**
     * Set the data in the view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        Category c = categoryList.get(position);
        holder.txt_category.setText(c.getCategory_name().toUpperCase());
        holder.txt_category_number.setText(c.getNumber_of_opinions());

    }

    /**
     * Function to get the item count
     * @return
     */
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    /**
     * Asign the elements of owr recycler holder to the created variables
     */
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView txt_category, txt_category_number;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txt_category = (TextView) itemView.findViewById(R.id.txt_category_profile);
            txt_category_number = (TextView) itemView.findViewById(R.id.txt_category_number);

        }
    }

    /**
     * Sets the on click listener
     * @param onClickListener
     */
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
