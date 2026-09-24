// FILE: app/src/main/java/com/example/kisanbandhu/adapters/CropAdapter.java
package com.example.kisanbandhu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.CropRecommendation;

import java.util.List;

/**
 * Feeds the list of CropRecommendation results into a RecyclerView on
 * the Crop Recommendations screen (Screen 4).
 */
public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    /** Callback so the hosting Activity finds out which crop was tapped. */
    public interface OnCropClickListener {
        void onCropClick(Crop crop);
    }

    private final Context context;
    private final List<CropRecommendation> recommendations;
    private final OnCropClickListener listener;

    public CropAdapter(Context context, List<CropRecommendation> recommendations,
                        OnCropClickListener listener) {
        this.context = context;
        this.recommendations = recommendations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_crop_card, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        CropRecommendation recommendation = recommendations.get(position);
        Crop crop = recommendation.getCrop();

        holder.tvCropName.setText(crop.getEmoji() + "  " + crop.getName());

        holder.tvSuitability.setText(recommendation.getSuitabilityLabel());
        holder.tvSuitability.setBackgroundColor(getColorForLabel(recommendation.getSuitabilityLabel()));

        String details = "Season: " + crop.getSuitableSeasonsText() +
                "  |  Water: " + crop.getWaterRequirement() +
                "\nSuitable soil: " + crop.getSuitableSoilsText() +
                "  |  Duration: " + crop.getGrowthDuration();
        holder.tvCropDetails.setText(details);

        holder.tvCropDescription.setText(crop.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCropClick(crop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    private int getColorForLabel(String label) {
        switch (label) {
            case "High":
                return Color.parseColor("#2E7D32"); // green
            case "Medium":
                return Color.parseColor("#EF6C00"); // orange
            default:
                return Color.parseColor("#757575"); // grey
        }
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvSuitability, tvCropDetails, tvCropDescription;

        CropViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvSuitability = itemView.findViewById(R.id.tvSuitability);
            tvCropDetails = itemView.findViewById(R.id.tvCropDetails);
            tvCropDescription = itemView.findViewById(R.id.tvCropDescription);
        }
    }
}
