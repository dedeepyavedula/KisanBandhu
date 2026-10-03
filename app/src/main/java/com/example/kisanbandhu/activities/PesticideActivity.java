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
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;

import java.util.ArrayList;
import java.util.List;

public class PesticideActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        Crop crop = CropData.getSelectedCrop(this);

        if (crop == null) {
            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        TextView title = findViewById(R.id.tvTitle);
        TextView subtitle = findViewById(R.id.tvSubtitle);

        title.setText(
                crop.getName() + " Pests & Diseases"
        );

        subtitle.setText(
                "Chemical and natural pest-control guidance"
        );

        loadGuide(crop.getName());
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
                    }
                }
        );
    }

    private void showGuide(
            FirestoreCropGuideManager.CropGuide guide) {

        List<InfoItem> items = new ArrayList<>();

        for (
                FirestoreCropGuideManager.PestData pest
                : guide.pests
        ) {

            String name = pest.getName();

            String symptoms = pest.getSymptoms();

            String chemical = pest.getChemicalControl();

            String organic = pest.getOrganicControl();

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

        if (items.isEmpty()) {

            tvEmpty.setText(
                    "No pest information available for this crop yet."
            );

            tvEmpty.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);

        } else {

            tvEmpty.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setAdapter(
                    new InfoAdapter(items)
            );
        }
    }
}