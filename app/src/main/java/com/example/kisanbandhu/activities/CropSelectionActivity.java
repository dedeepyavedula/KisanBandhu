package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.CropAdapter;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.CropRecommendation;
import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.utils.CropRecommendationEngine;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

public class CropSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvSelectionSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_selection);

        recyclerView =
                findViewById(R.id.recyclerViewSelection);

        tvSelectionSubtitle =
                findViewById(R.id.tvSelectionSubtitle);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadRecommendedCrops();
    }

    private void loadRecommendedCrops() {

        Soil soil = PrefsManager.getSoilProfile(this);

        if (soil == null) {

            tvSelectionSubtitle.setText(
                    "Tip: fill in your soil profile to see recommended crops first"
            );

            Toast.makeText(
                    this,
                    "Please fill in your soil information first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        CropRecommendationEngine.getRecommendations(
                soil,
                new CropRecommendationEngine.OnRecommendationsLoadedListener() {

                    @Override
                    public void onSuccess(
                            List<CropRecommendation> recommendations) {

                        showCrops(recommendations);
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                CropSelectionActivity.this,
                                "Could not load crops: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void showCrops(
            List<CropRecommendation> recommendations) {

        if (recommendations.isEmpty()) {

            tvSelectionSubtitle.setText(
                    "No suitable crops found for your soil conditions."
            );

            return;
        }

        tvSelectionSubtitle.setText(
                "Recommended crops based on your soil and conditions"
        );

        CropAdapter adapter =
                new CropAdapter(
                        this,
                        recommendations,
                        crop -> {

                            CropStageHelper.selectCrop(
                                    this,
                                    crop
                            );

                            startActivity(
                                    new Intent(
                                            this,
                                            CropDashboardActivity.class
                                    )
                            );

                            finish();
                        }
                );

        recyclerView.setAdapter(adapter);
    }
}