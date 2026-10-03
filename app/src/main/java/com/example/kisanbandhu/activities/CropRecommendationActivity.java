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

public class CropRecommendationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_recommendation);

        recyclerView = findViewById(R.id.recyclerViewCrops);
        tvNoResults = findViewById(R.id.tvNoResults);

        if (!PrefsManager.hasSoilProfile(this)) {
            Toast.makeText(
                    this,
                    "Please fill in your soil information first",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(this, SoilInputActivity.class)
            );
            finish();
            return;
        }

        Soil soil = PrefsManager.getSoilProfile(this);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Load recommendations from Firestore
        CropRecommendationEngine.getRecommendations(
                soil,
                new CropRecommendationEngine.OnRecommendationsLoadedListener() {

                    @Override
                    public void onSuccess(
                            List<CropRecommendation> recommendations) {

                        if (recommendations.isEmpty()) {
                            tvNoResults.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            return;
                        }

                        tvNoResults.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        CropAdapter adapter = new CropAdapter(
                                CropRecommendationActivity.this,
                                recommendations,
                                crop -> {
                                    CropStageHelper.selectCrop(
                                            CropRecommendationActivity.this,
                                            crop
                                    );

                                    startActivity(
                                            new Intent(
                                                    CropRecommendationActivity.this,
                                                    CropDashboardActivity.class
                                            )
                                    );
                                }
                        );

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                CropRecommendationActivity.this,
                                "Could not load recommendations: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}