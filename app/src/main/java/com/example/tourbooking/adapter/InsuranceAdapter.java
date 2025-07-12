package com.example.tourbooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Insurance;

import java.util.*;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.ViewHolder> {

    private List<Insurance> insuranceList;
    private List<Insurance> selected = new ArrayList<>();
    private final OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<Insurance> selected);
    }

    public InsuranceAdapter(List<Insurance> list, OnSelectionChangedListener listener) {
        this.insuranceList = list;
        this.listener = listener;
    }

    public void updateList(List<Insurance> newList) {
        this.insuranceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InsuranceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insurance_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsuranceAdapter.ViewHolder holder, int position) {
        Insurance pkg = insuranceList.get(position);
        holder.name.setText(pkg.getName());
        holder.provider.setText(pkg.getProvider());
        holder.price.setText(pkg.getPrice() + "đ");

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(selected.contains(pkg));
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selected.add(pkg);
            else selected.remove(pkg);
            listener.onSelectionChanged(selected);
        });
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, provider, price;
        CheckBox checkbox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvInsuranceName);
            provider = itemView.findViewById(R.id.tvProvider);
            price = itemView.findViewById(R.id.tvPrice);
            checkbox = itemView.findViewById(R.id.checkboxSelect);
        }
    }
}
