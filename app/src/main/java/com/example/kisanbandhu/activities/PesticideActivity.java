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
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

public class PesticideActivity extends AppCompatActivity {

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

        tvTitle.setText(
                selectedCropName + " Pests & Diseases"
        );

        tvSubtitle.setText(
                "Chemical and natural pest-control guidance"
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

        FirestoreCropGuideManager manager =
                new FirestoreCropGuideManager();

        manager.getGuide(
                cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {

                    @Override
                    public void onSuccess(
                            FirestoreCropGuideManager.CropGuide guide) {

                        if (guide == null) {
                            showEmptyState(
                                    "No pest information available for this crop yet."
                            );
                            return;
                        }

                        showGuide(guide);
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                PesticideActivity.this,
                                "Could not load pest information: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        showEmptyState(
                                "Could not load pest information."
                        );
                    }
                }
        );
    }

    private void showGuide(
            FirestoreCropGuideManager.CropGuide guide) {

        // Never use guide.cropName to construct the title.
        // It may be null even when a crop has been selected.
        tvTitle.setText(
                selectedCropName + " Pests & Diseases"
        );

        tvSubtitle.setText(
                "Chemical and natural pest-control guidance"
        );

        List<InfoItem> items = new ArrayList<>();

        if (guide.pests != null) {

            for (FirestoreCropGuideManager.PestData pest
                    : guide.pests) {

                if (pest == null) {
                    continue;
                }

                String name = pest.getName();
                String symptoms = pest.getSymptoms();
                String chemical = pest.getChemicalControl();
                String organic = pest.getOrganicControl();

                // Prevent missing Firestore fields from showing "null".
                name = safeText(name, "Pest information");
                symptoms = safeText(
                        symptoms,
                        "No symptom information available."
                );
                chemical = safeText(
                        chemical,
                        "No chemical control information available."
                );
                organic = safeText(
                        organic,
                        "No natural or organic control information available."
                );

                String body =
                        "Symptoms:\n"
                                + symptoms
                                + "\n\nChemical control:\n"
                                + chemical
                                + "\n\nNatural / Organic control:\n"
                                + organic;

                items.add(
                        new InfoItem(
                                name,
                                "",
                                Color.BLACK,
                                body,
                                false
                        )
                );
            }
        }

        if (items.isEmpty()) {

            showEmptyState(
                    "No pest information available for this crop yet."
            );

        } else {

            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setAdapter(
                    new InfoAdapter(items)
            );
        }
    }

    private String safeText(String value, String fallback) {

        if (value == null
                || value.trim().isEmpty()
                || value.trim().equalsIgnoreCase("null")
                || value.trim().equalsIgnoreCase("undefined")) {
            return fallback;
        }

        return value.trim();
    }

    private void showEmptyState(String message) {

        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
