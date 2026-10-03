package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.utils.PrefsManager;

public class HomeActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_home
        );

        Button btnLogout =
                findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            com.google.firebase.auth.FirebaseAuth
                    .getInstance()
                    .signOut();

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });

        CardView cardCropRecommend =
                findViewById(
                        R.id.cardCropRecommend
                );

        CardView cardWeather =
                findViewById(
                        R.id.cardWeather
                );

        CardView cardCalendar =
                findViewById(
                        R.id.cardCalendar
                );

        CardView cardFertilizer =
                findViewById(
                        R.id.cardFertilizer
                );

        CardView cardPesticide =
                findViewById(
                        R.id.cardPesticide
                );

        CardView cardReminders =
                findViewById(
                        R.id.cardReminders
                );

        CardView cardLeafDisease =
                findViewById(
                        R.id.cardLeafDisease
                );

        cardCropRecommend.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                SoilInputActivity.class
                        )
                )
        );

        cardWeather.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                WeatherActivity.class
                        )
                )
        );

        cardCalendar.setOnClickListener(
                v -> openForCrop(
                        CropCalendarActivity.class
                )
        );

        cardFertilizer.setOnClickListener(
                v -> openForCrop(
                        FertilizerActivity.class
                )
        );

        cardPesticide.setOnClickListener(
                v -> openForCrop(
                        PesticideActivity.class
                )
        );

        cardReminders.setOnClickListener(
                v -> openForCrop(
                        RemindersActivity.class
                )
        );

        cardLeafDisease.setOnClickListener(
                v -> openForCrop(
                        LeafDiseaseActivity.class
                )
        );

        TextView tvSelectedCrop =
                findViewById(
                        R.id.tvSelectedCrop
                );

        tvSelectedCrop.setOnClickListener(
                v -> openForCrop(
                        CropDashboardActivity.class
                )
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        TextView tvSelectedCrop =
                findViewById(
                        R.id.tvSelectedCrop
                );

        String selectedCrop =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (selectedCrop == null) {

            tvSelectedCrop.setText(
                    "No crop selected yet - tap here to select"
            );

        } else {

            tvSelectedCrop.setText(
                    "Selected crop: "
                            + selectedCrop
                            + " (tap for dashboard)"
            );
        }
    }

    private void openForCrop(
            Class<?> target) {

        String selectedCrop =
                PrefsManager.getString(
                        this,
                        PrefsManager.KEY_SELECTED_CROP,
                        null
                );

        if (selectedCrop == null) {

            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(
                            this,
                            CropSelectionActivity.class
                    )
            );

        } else {

            startActivity(
                    new Intent(
                            this,
                            target
                    )
            );
        }
    }
}