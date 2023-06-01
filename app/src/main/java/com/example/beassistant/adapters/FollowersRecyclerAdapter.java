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
import com.example.beassistant.models.UserInAList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 *
 */

public class FollowersRecyclerAdapter extends RecyclerView.Adapter<FollowersRecyclerAdapter.RecyclerHolder>{

    // Create the followers list
    public ArrayList<UserInAList> followersList;

    // Declare the listeners
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    // Create the context
    private Context contexto;

    // Create the storage and storage Ref
    private FirebaseStorage storage;
    private StorageReference storageRef;

    /**
     * The class constructor
     * @param contexto
     */
    public FollowersRecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        followersList = new ArrayList<>();
    }

    /**
     * Function to delete a item of the list
     * @param seleccionado
     */
    public void deleteItem(int seleccionado){
        followersList.remove(seleccionado);
        this.notifyDataSetChanged();

    }

    /**
     * Function to delete a item of the list
     * @param o
     */
    public void insertarItem(UserInAList o){
        followersList.add(o);
        this.notifyDataSetChanged();
    }

    /**
     * Function to modify a item of the list
     * @param seleccionado
     * @param id
     * @param name
     * @param desc
     */
    public void modItem(int seleccionado,String id,String name, String desc){
        this.notifyDataSetChanged();
    }

    /**
     * Function to filter the list
     *
     * @param filteredList
     */
    public void setFilteredList(ArrayList<UserInAList> filteredList){
        followersList = filteredList;
        notifyDataSetChanged();
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

        // Set the view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower,parent, false);

        // Create the recycler holder
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        // Init the storage
        storage = FirebaseStorage.getInstance();

        // Init the storage ref
        storageRef = storage.getReference();

        // Set the listeners
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);

        // return the recycler holder
        return recyclerHolder;
    }

    /**
     * Set the data int the view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        // Crete a user
        UserInAList user = followersList.get(position);

        // Set the image
        storageRef.child(user.getImgRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //  Get the bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Set the bitmap
                holder.imgProfile.setImageBitmap(bitmap);
            }
        });

        // Set the username
        holder.txt_username.setText(user.getUsername());
    }

    /**
     * Function to get the item count
     * @return
     */
    @Override
    public int getItemCount() {
        // Return item count
        return followersList.size();
    }

    /**
     * Asign the elements of owr recycler holder to the created variables
     */
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        // Create the username text view
        TextView txt_username;

        // Create the profile image view
        ImageView imgProfile;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            // Set the username
            txt_username = (TextView) itemView.findViewById(R.id.txt_follower_name);

            // Set the profile image
            imgProfile = (ImageView) itemView.findViewById(R.id.img_profile_user);

        }
    }

    /**
     * Sets the on click listener
     * @param onClickListener
     */
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Sets the on long click listener
     * @param onLongClickListener
     */
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
