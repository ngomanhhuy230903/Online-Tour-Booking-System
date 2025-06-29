package com.example.tourbooking.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import java.util.List;

public class SelectedPhotoAdapter extends RecyclerView.Adapter<SelectedPhotoAdapter.PhotoViewHolder> {

    public interface OnPhotoRemoveListener {
        void onPhotoRemoved(int position);
    }

    private List<Uri> photoUris;
    private Context context;
    private OnPhotoRemoveListener listener;

    public SelectedPhotoAdapter(List<Uri> photoUris, Context context, OnPhotoRemoveListener listener) {
        this.photoUris = photoUris;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = photoUris.get(position);
        Glide.with(context).load(uri).into(holder.ivSelectedPhoto);
        holder.btnRemovePhoto.setOnClickListener(v -> listener.onPhotoRemoved(position));
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSelectedPhoto;
        ImageButton btnRemovePhoto;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSelectedPhoto = itemView.findViewById(R.id.ivSelectedPhoto);
            btnRemovePhoto = itemView.findViewById(R.id.btnRemovePhoto);
        }
    }
}