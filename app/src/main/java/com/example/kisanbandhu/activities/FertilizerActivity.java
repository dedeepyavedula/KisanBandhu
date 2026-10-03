package com.example.kisanbandhu.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FertilizerAdvisor;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

public class FertilizerActivity
        extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_list_screen
        );

        recyclerView =
                findViewById(R.id.recyclerView);

        tvTitle =
                findViewById(R.id.tvTitle);

        tvSubtitle =
                findViewById(R.id.tvSubtitle);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        String cropName =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (cropName == null) {

            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadGuide(cropName);
    }

    private void loadGuide(String cropName) {

        new FirestoreCropGuideManager()
                .getGuide(
                        cropName,
                        new FirestoreCropGuideManager.OnGuideLoadedListener() {

                            @Override
                            public void onSuccess(
                                    FirestoreCropGuideManager.CropGuide guide) {

                                showGuide(guide);
                            }

                            @Override
                            public void onFailure(Exception e) {

                                Toast.makeText(
                                        FertilizerActivity.this,
                                        "Could not load fertilizer plan: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void showGuide(
            FirestoreCropGuideManager.CropGuide guide) {

        tvTitle.setText(
                guide.cropName
                        + " Fertilizer Plan"
        );

        tvSubtitle.setText(
                "Chemical and natural fertilizer options"
        );

        List<InfoItem> items =
                new ArrayList<>();

        String soilAdjustment =
                FertilizerAdvisor.getSoilAdjustment(
                        null
                );

        items.add(
                new InfoItem(
                        "Soil-based adjustments",
                        "YOUR SOIL",
                        Color.parseColor("#795548"),
                        soilAdjustment,
                        false
                )
        );

        List<FirestoreCropGuideManager.StageData> stages =
                guide.stages;

        List<FirestoreCropGuideManager.FertilizerData> fertilizers =
                guide.fertilizers;

        int current =
                CropStageHelper.getCurrentStageIndex(
                        this,
                        stages
                );

        for (int i = 0; i < stages.size(); i++) {

            FirestoreCropGuideManager.StageData stage =
                    stages.get(i);

            String body;

            if (i < fertilizers.size()) {

                FirestoreCropGuideManager.FertilizerData advice =
                        fertilizers.get(i);

                body =
                        "Chemical:\n"
                                + advice.getChemical()
                                + "\n\nNatural / Organic:\n"
                                + advice.getOrganic();

            } else {

                body =
                        "No fertilizer information available for this stage.";
            }

            boolean isCurrent =
                    i == current;

            items.add(
                    new InfoItem(
                            stage.getName()
                                    + " (Day "
                                    + stage.getStartDay()
                                    + "-"
                                    + stage.getEndDay()
                                    + ")",
                            isCurrent ? "NOW" : "",
                            Color.parseColor("#2E7D32"),
                            body,
                            isCurrent
                    )
            );
        }

        if (items.isEmpty()) {

            tvEmpty.setText(
                    "No fertilizer information available."
            );

            tvEmpty.setVisibility(View.VISIBLE);

            return;
        }

        recyclerView.setAdapter(
                new InfoAdapter(items)
        );
    }
}