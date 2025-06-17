package com.example.tourbooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    public static class FAQItem {
        public final String question;
        public final String answer;
        public final String category;
        public FAQItem(String q, String a, String cat) {
            question = q; answer = a; category = cat;
        }
    }

    private final List<FAQItem> items;

    public FAQAdapter(List<FAQItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem item = items.get(position);
        holder.question.setText(item.question);
        holder.answer.setText(item.answer);
        holder.answer.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (holder.answer.getVisibility() == View.VISIBLE) {
                holder.answer.setVisibility(View.GONE);
            } else {
                holder.answer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<FAQItem> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
    }
} 