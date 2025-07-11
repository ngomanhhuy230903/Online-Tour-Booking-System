package com.example.tourbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.tour.TourDetailsActivity;

import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private Context context;
    private List<Tour> tourList;

    public TourAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_search_results, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);

        holder.tvTourName.setText(tour.getTourName());
        holder.tvPrice.setText("$" + tour.getPrice());
        holder.tvDays.setText(tour.getDays() + " days");

        if (tour.getRating() != null) {
            holder.ratingBar.setRating(tour.getRating().floatValue());
        } else {
            holder.ratingBar.setRating(0f);
        }
        Glide.with(context)
                .load(tour.getThumbnailUrl())
                .into(holder.imgTour);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TourDetailsActivity.class);
            intent.putExtra("tour", tour); // Gửi object nếu Tour implement Serializable hoặc Parcelable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tourList != null ? tourList.size() : 0;
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTour;
        TextView tvTourName, tvPrice, tvDays;
        RatingBar ratingBar;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTour = itemView.findViewById(R.id.imgTour);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDays = itemView.findViewById(R.id.tvDays);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
