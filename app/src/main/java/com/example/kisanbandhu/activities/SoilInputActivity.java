package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.activities.SoilAnalysisActivity;
import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.utils.PrefsManager;
import com.example.kisanbandhu.utils.FarmerLocationStore;

/**
 * SCREEN 2 - FARMER PROFILE / SOIL INPUT.
 *
 * Collects soil type, pH, fertility, location, irrigation and season.
 * Validates the input, saves it using PrefsManager, saves the farmer's
 * district and state to Firestore, and opens SoilAnalysisActivity.
 */
public class SoilInputActivity extends AppCompatActivity {

    private Spinner spinnerSoilType, spinnerFertility, spinnerState, spinnerSeason;
    private EditText etSoilPH, etDistrict;
    private RadioGroup radioGroupIrrigation;
    private Button btnAnalyze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_input);

        spinnerSoilType = findViewById(R.id.spinnerSoilType);
        spinnerFertility = findViewById(R.id.spinnerFertility);
        spinnerState = findViewById(R.id.spinnerState);
        spinnerSeason = findViewById(R.id.spinnerSeason);

        etSoilPH = findViewById(R.id.etSoilPH);
        etDistrict = findViewById(R.id.etDistrict);

        radioGroupIrrigation = findViewById(R.id.radioGroupIrrigation);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        prefillFromSavedProfile();

        btnAnalyze.setOnClickListener(v -> onAnalyzeClicked());
    }

    /**
     * Restore the previously saved soil profile.
     */
    private void prefillFromSavedProfile() {

        if (!PrefsManager.hasSoilProfile(this)) {
            return;
        }

        Soil saved = PrefsManager.getSoilProfile(this);

        setSpinnerSelection(spinnerSoilType, saved.getSoilType());
        setSpinnerSelection(spinnerFertility, saved.getFertility());
        setSpinnerSelection(spinnerState, saved.getState());
        setSpinnerSelection(spinnerSeason, saved.getSeason());

        etSoilPH.setText(saved.getSoilPH());
        etDistrict.setText(saved.getDistrict());

        if (saved.isIrrigated()) {
            radioGroupIrrigation.check(R.id.radioIrrigated);
        } else {
            radioGroupIrrigation.check(R.id.radioRainfed);
        }
    }

    /**
     * Select the spinner item matching the saved value.
     */
    private void setSpinnerSelection(Spinner spinner, String savedValue) {

        if (savedValue == null || spinner.getAdapter() == null) {
            return;
        }

        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {

            Object item = spinner.getAdapter().getItem(i);

            if (item != null && item.toString().equals(savedValue)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void onAnalyzeClicked() {

        if (spinnerSoilType.getSelectedItem() == null
                || spinnerFertility.getSelectedItem() == null
                || spinnerState.getSelectedItem() == null
                || spinnerSeason.getSelectedItem() == null) {

            Toast.makeText(
                    this,
                    "Please complete all selections",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String soilType = spinnerSoilType.getSelectedItem().toString().trim();
        String fertility = spinnerFertility.getSelectedItem().toString().trim();
        String state = spinnerState.getSelectedItem().toString().trim();
        String season = spinnerSeason.getSelectedItem().toString().trim();

        String district = etDistrict.getText().toString().trim();
        String ph = etSoilPH.getText().toString().trim();

        // Validate soil type.
        if (soilType.startsWith("Select")) {
            Toast.makeText(
                    this,
                    "Please select a soil type",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validate fertility.
        if (fertility.startsWith("Select")) {
            Toast.makeText(
                    this,
                    "Please select a fertility level",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validate state.
        if (state.startsWith("Select")) {
            Toast.makeText(
                    this,
                    "Please select your state",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validate district.
        if (TextUtils.isEmpty(district)) {
            etDistrict.setError("District is required");
            etDistrict.requestFocus();
            return;
        }

        // Validate season.
        if (season.startsWith("Select")) {
            Toast.makeText(
                    this,
                    "Please select a season",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validate irrigation selection.
        if (radioGroupIrrigation.getCheckedRadioButtonId() == -1) {
            Toast.makeText(
                    this,
                    "Please select irrigation availability",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validate pH if the farmer entered a value.
        if (!TextUtils.isEmpty(ph)) {

            try {
                double phValue = Double.parseDouble(ph);

                if (phValue < 0 || phValue > 14) {
                    etSoilPH.setError("pH must be between 0 and 14");
                    etSoilPH.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                etSoilPH.setError("Enter a valid number");
                etSoilPH.requestFocus();
                return;
            }
        }

        boolean irrigated =
                radioGroupIrrigation.getCheckedRadioButtonId()
                        == R.id.radioIrrigated;

        // Create and save the soil profile locally.
        Soil soil = new Soil(
                soilType,
                ph,
                fertility,
                state,
                district,
                irrigated,
                season
        );

        PrefsManager.saveSoilProfile(this, soil);

        // Save the selected location to the signed-in farmer's Firestore record.
        // Keep this before opening the next screen.
        FarmerLocationStore.saveSoilLocation(this, district, state);

        // Open the soil analysis screen.
        Intent intent = new Intent(
                SoilInputActivity.this,
                SoilAnalysisActivity.class
        );

        startActivity(intent);
    }
}