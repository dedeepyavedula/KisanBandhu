// FILE: app/src/main/java/com/example/kisanbandhu/activities/HomeActivity.java
package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;

/**
 * SCREEN: HOME DASHBOARD.
 *
 * Shows one card per major feature of the app. In Phase 1, none of the
 * destination screens exist yet, so every card currently shows a Toast
 * message explaining which phase will implement it.
 *
 * WHEN YOU MOVE TO LATER PHASES:
 * Each "showComingSoon(...)" call below should be replaced with an
 * actual navigation Intent, for example:
 *
 *   cardCropRecommend.setOnClickListener(v -> {
 *       startActivity(new Intent(HomeActivity.this, SoilInputActivity.class));
 *   });
 *
 * This keeps the app runnable and demonstrable at every phase, instead
 * of crashing because a target Activity doesn't exist yet.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find each card by its XML id.
        CardView cardCropRecommend = findViewById(R.id.cardCropRecommend);
        CardView cardWeather = findViewById(R.id.cardWeather);
        CardView cardCalendar = findViewById(R.id.cardCalendar);
        CardView cardFertilizer = findViewById(R.id.cardFertilizer);
        CardView cardPesticide = findViewById(R.id.cardPesticide);
        CardView cardReminders = findViewById(R.id.cardReminders);
        CardView cardLeafDisease = findViewById(R.id.cardLeafDisease);

        // For now, every card just tells the user which phase will
        // connect it to a real screen. This proves the layout and click
        // handling work correctly without needing every screen built yet.
        // Phase 2: this now opens the real Soil Input screen instead of a Toast.
        cardCropRecommend.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, SoilInputActivity.class)));

        // Phase 4: crop-specific cards need a selected crop; if none, go pick one first.
        cardWeather.setOnClickListener(v ->
                startActivity(new Intent(this, WeatherActivity.class))); // works without a crop

        cardCalendar.setOnClickListener(v -> openForCrop(CropCalendarActivity.class));
        cardFertilizer.setOnClickListener(v -> openForCrop(FertilizerActivity.class));
        cardPesticide.setOnClickListener(v -> openForCrop(PesticideActivity.class));
        cardReminders.setOnClickListener(v -> openForCrop(RemindersActivity.class));
        cardLeafDisease.setOnClickListener(v -> openForCrop(LeafDiseaseActivity.class));

        // Tapping the "Selected crop" line opens the dashboard (or crop selection).
        findViewById(R.id.tvSelectedCrop).setOnClickListener(v -> openForCrop(CropDashboardActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Crop crop = CropData.getSelectedCrop(this);
        ((TextView) findViewById(R.id.tvSelectedCrop)).setText(crop == null
                ? "No crop selected yet - tap here to select"
                : "Selected crop: " + crop.getEmoji() + " " + crop.getName() + " (tap for dashboard)");
    }

    private void openForCrop(Class<?> target) {
        if (CropData.getSelectedCrop(this) == null) {
            Toast.makeText(this, "Please select a crop first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CropSelectionActivity.class));
        } else {
            startActivity(new Intent(this, target));
        }
    }
}
