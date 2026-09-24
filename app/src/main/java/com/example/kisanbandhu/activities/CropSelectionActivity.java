package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.CropAdapter;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.CropRecommendation;
import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.utils.CropRecommendationEngine;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * PHASE 4 - SELECT CROP.
 *
 * Lists every crop (recommended ones first, if a soil profile exists),
 * saves the tapped crop and opens its dashboard.
 */
public class CropSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_selection);

        List<CropRecommendation> items = new ArrayList<>();
        List<String> added = new ArrayList<>();

        Soil soil = PrefsManager.getSoilProfile(this);
        if (soil != null) {
            for (CropRecommendation r : CropRecommendationEngine.getRecommendations(soil)) {
                items.add(r);
                added.add(r.getCrop().getName());
            }
        } else {
            ((TextView) findViewById(R.id.tvSelectionSubtitle)).setText(
                    "Tip: fill in your soil profile to see recommended crops first");
        }
        for (Crop crop : CropData.getAllCrops()) {
            if (!added.contains(crop.getName())) {
                items.add(new CropRecommendation(crop, 0, "Other"));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSelection);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CropAdapter(this, items, crop -> {
            CropStageHelper.selectCrop(this, crop);
            startActivity(new Intent(this, CropDashboardActivity.class));
            finish();
        }));
    }
}
