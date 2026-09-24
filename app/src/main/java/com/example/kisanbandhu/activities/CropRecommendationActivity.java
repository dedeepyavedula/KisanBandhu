// FILE: app/src/main/java/com/example/kisanbandhu/activities/CropRecommendationActivity.java
package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.CropAdapter;
import com.example.kisanbandhu.models.CropRecommendation;
import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.utils.CropRecommendationEngine;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.List;

/**
 * SCREEN 4 - CROP RECOMMENDATIONS.
 *
 * Reads the saved Soil profile, runs it through CropRecommendationEngine,
 * and displays the resulting list as cards. Tapping a card currently
 * shows a Toast - Phase 4 will turn this into real crop selection
 * (CropSelectionActivity), saving the chosen crop via PrefsManager.
 */
public class CropRecommendationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_recommendation);

        if (!PrefsManager.hasSoilProfile(this)) {
            Toast.makeText(this, "Please fill in your soil information first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SoilInputActivity.class));
            finish();
            return;
        }

        Soil soil = PrefsManager.getSoilProfile(this);
        List<CropRecommendation> recommendations = CropRecommendationEngine.getRecommendations(soil);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCrops);
        TextView tvNoResults = findViewById(R.id.tvNoResults);

        if (recommendations.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Phase 4: tapping a card selects the crop and opens its dashboard.
        CropAdapter adapter = new CropAdapter(this, recommendations, crop -> {
            CropStageHelper.selectCrop(this, crop);
            startActivity(new Intent(this, CropDashboardActivity.class));
        });
        recyclerView.setAdapter(adapter);
    }
}
