package com.example.kisanbandhu.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.models.InfoItem;

import java.util.List;

/**
 * One adapter for all Phase 4 list screens (calendar, fertilizer, pesticide,
 * weather, alerts, reminders). Each screen converts its data to InfoItems.
 */
public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private final List<InfoItem> items;

    public InfoAdapter(List<InfoItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_card, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());

        if (item.getBadge() == null || item.getBadge().isEmpty()) {
            holder.tvBadge.setVisibility(View.GONE);
        } else {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(item.getBadge());
            holder.tvBadge.setBackgroundColor(item.getBadgeColor());
        }
        holder.card.setCardBackgroundColor(Color.parseColor(item.isHighlighted() ? "#E8F5E9" : "#FFFFFF"));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvTitle, tvBadge, tvBody;

        InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardInfo);
            tvTitle = itemView.findViewById(R.id.tvInfoTitle);
            tvBadge = itemView.findViewById(R.id.tvInfoBadge);
            tvBody = itemView.findViewById(R.id.tvInfoBody);
        }
    }
}
