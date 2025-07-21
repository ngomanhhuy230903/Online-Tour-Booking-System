package com.example.tourbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.tour.TourDetailsActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    public interface OnRemoveClickListener {
        void onRemoveClick(Tour tour);
    }

    private List<Tour> tourList;
    private Context context;
    private OnRemoveClickListener removeClickListener;

    public FavoriteAdapter(List<Tour> tourList, Context context, OnRemoveClickListener listener) {
        this.tourList = tourList;
        this.context = context;
        this.removeClickListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_tour, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        holder.bind(tour, removeClickListener);
    }

    @Override
    public int getItemCount() {
        return tourList != null ? tourList.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTourThumbnail;
        TextView tvTourName, tvTourPrice;
        ImageButton btnRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTourThumbnail = itemView.findViewById(R.id.ivTourThumbnail);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvTourPrice = itemView.findViewById(R.id.tvTourPrice);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }

        void bind(final Tour tour, final OnRemoveClickListener listener) {
            tvTourName.setText(tour.getTourName());
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvTourPrice.setText(currencyFormatter.format(tour.getPrice()));

            Glide.with(itemView.getContext())
                    .load(tour.getThumbnailUrl())
                    .placeholder(R.color.placeholder_gray)
                    .into(ivTourThumbnail);

            btnRemoveFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(tour);
                }
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TourDetailsActivity.class);
                intent.putExtra("tour", tour);
                intent.putExtra("tour_id", tour.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}