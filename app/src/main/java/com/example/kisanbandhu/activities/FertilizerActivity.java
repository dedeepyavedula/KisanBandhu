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

public class FertilizerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvEmpty;

    private String selectedCropName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        recyclerView = findViewById(R.id.recyclerView);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        selectedCropName = cleanCropName(
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                )
        );

        if (selectedCropName == null) {
            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Set the title using the selected crop, not a nullable
        // crop name returned by Firestore.
        tvTitle.setText(selectedCropName + " Fertilizer Plan");

        tvSubtitle.setText(
                "Chemical and natural fertilizer options"
        );

        tvEmpty.setVisibility(View.GONE);

        loadGuide(selectedCropName);
    }

    private String cleanCropName(String cropName) {

        if (cropName == null) {
            return null;
        }

        cropName = cropName.trim();

        if (cropName.isEmpty()
                || cropName.equalsIgnoreCase("null")
                || cropName.equalsIgnoreCase("undefined")) {
            return null;
        }

        return cropName;
    }

    private void loadGuide(String cropName) {

        new FirestoreCropGuideManager().getGuide(
                cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {

                    @Override
                    public void onSuccess(
                            FirestoreCropGuideManager.CropGuide guide) {

                        if (guide == null) {
                            showEmptyState(
                                    "No fertilizer information available."
                            );
                            return;
                        }

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

                        showEmptyState(
                                "Could not load fertilizer information."
                        );
                    }
                }
        );
    }

    private void showGuide(
            FirestoreCropGuideManager.CropGuide guide) {

        // Keep the title valid even if Firestore's cropName is null.
        tvTitle.setText(
                selectedCropName + " Fertilizer Plan"
        );

        tvSubtitle.setText(
                "Chemical and natural fertilizer options"
        );

        List<InfoItem> items = new ArrayList<>();

        String soilAdjustment =
                FertilizerAdvisor.getSoilAdjustment(null);

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

        if (stages != null && !stages.isEmpty()) {

            int current = CropStageHelper.getCurrentStageIndex(
                    this,
                    stages
            );

            for (int i = 0; i < stages.size(); i++) {

                FirestoreCropGuideManager.StageData stage =
                        stages.get(i);

                if (stage == null) {
                    continue;
                }

                String body;

                if (fertilizers != null && i < fertilizers.size()
                        && fertilizers.get(i) != null) {

                    FirestoreCropGuideManager.FertilizerData advice =
                            fertilizers.get(i);

                    String chemical = advice.getChemical();
                    String organic = advice.getOrganic();

                    body = "Chemical:\n"
                            + (chemical == null || chemical.trim().isEmpty()
                            ? "No chemical fertilizer information available."
                            : chemical)
                            + "\n\nNatural / Organic:\n"
                            + (organic == null || organic.trim().isEmpty()
                            ? "No natural fertilizer information available."
                            : organic);

                } else {
                    body =
                            "No fertilizer information available for this stage.";
                }

                boolean isCurrent = i == current;

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
        }

        if (items.isEmpty()) {
            showEmptyState(
                    "No fertilizer information available."
            );
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(
                new InfoAdapter(items)
        );
    }

    private void showEmptyState(String message) {

        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}