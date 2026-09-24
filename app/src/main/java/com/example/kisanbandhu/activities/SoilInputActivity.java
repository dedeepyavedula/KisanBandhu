// FILE: app/src/main/java/com/example/kisanbandhu/activities/SoilInputActivity.java
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
import com.example.kisanbandhu.models.Soil;
import com.example.kisanbandhu.utils.PrefsManager;

/**
 * SCREEN 2 - FARMER PROFILE / SOIL INPUT.
 *
 * Collects soil type, pH, fertility, location, irrigation and season,
 * validates the input, saves it via PrefsManager, then moves to the
 * Soil Analysis screen.
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
     * If the farmer already filled this form before, pre-select their
     * previous answers so they don't have to retype everything. This is
     * a nice touch to mention in your viva ("the app remembers you").
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

    /** Finds the entry matching savedValue in the spinner's adapter and selects it. */
    private void setSpinnerSelection(Spinner spinner, String savedValue) {
        if (savedValue == null) return;
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equals(savedValue)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void onAnalyzeClicked() {
        String soilType = spinnerSoilType.getSelectedItem().toString();
        String fertility = spinnerFertility.getSelectedItem().toString();
        String state = spinnerState.getSelectedItem().toString();
        String season = spinnerSeason.getSelectedItem().toString();
        String district = etDistrict.getText().toString().trim();
        String ph = etSoilPH.getText().toString().trim();

        // ---- Validation (Screen 2 error handling requirements) ----

        if (soilType.startsWith("Select")) {
            Toast.makeText(this, "Please select a soil type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fertility.startsWith("Select")) {
            Toast.makeText(this, "Please select a fertility level", Toast.LENGTH_SHORT).show();
            return;
        }
        if (state.startsWith("Select")) {
            Toast.makeText(this, "Please select your state", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(district)) {
            etDistrict.setError("District is required");
            etDistrict.requestFocus();
            return;
        }
        if (season.startsWith("Select")) {
            Toast.makeText(this, "Please select a season", Toast.LENGTH_SHORT).show();
            return;
        }
        if (radioGroupIrrigation.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select irrigation availability", Toast.LENGTH_SHORT).show();
            return;
        }
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

        boolean irrigated = radioGroupIrrigation.getCheckedRadioButtonId() == R.id.radioIrrigated;

        Soil soil = new Soil(soilType, ph, fertility, state, district, irrigated, season);
        PrefsManager.saveSoilProfile(this, soil);

        startActivity(new Intent(SoilInputActivity.this, SoilAnalysisActivity.class));
    }
}
