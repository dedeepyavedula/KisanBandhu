package com.example.kisanbandhu.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.activities.CropSelectionActivity;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.FirestoreCropManager;
import com.example.kisanbandhu.utils.PrefsManager;
import com.example.kisanbandhu.utils.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CropDashboardActivity extends AppCompatActivity {

    private Crop crop;
    private FirestoreCropGuideManager.CropGuide guide;

    private TextView tvDashCrop;
    private TextView tvDashInfo;
    private TextView tvDashStage;
    private TextView tvDashActivity;
    private TextView tvSowingDateDisplay;

    private ProgressBar progressGrowth;
    private Button btnSetSowingDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_dashboard);

        // Bind views from activity_crop_dashboard.xml
        tvDashCrop = findViewById(R.id.tvDashCrop);
        tvDashInfo = findViewById(R.id.tvDashInfo);
        tvDashStage = findViewById(R.id.tvDashStage);
        tvDashActivity = findViewById(R.id.tvDashActivity);
        tvSowingDateDisplay = findViewById(R.id.tvSowingDateDisplay);

        progressGrowth = findViewById(R.id.progressGrowth);
        btnSetSowingDate = findViewById(R.id.btnSetSowingDate);

        // Get the currently selected crop
        String cropName = PrefsManager.getString(
                this,
                PrefsManager.KEY_SELECTED_CROP,
                null
        );

        if (cropName == null || cropName.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select a crop first",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(
                    new Intent(this, CropSelectionActivity.class)
            );

            finish();
            return;
        }

        // Load crop details and its growth guide
        loadCrop(cropName);

        // Open the date picker
        btnSetSowingDate.setOnClickListener(
                v -> showDatePicker()
        );

        // Display the previously saved date, if available
        updateSowingDateDisplay();
    }

    private void loadCrop(String cropName) {

        new FirestoreCropManager().getCrop(
                cropName,
                new FirestoreCropManager.OnCropLoadedListener() {

                    @Override
                    public void onSuccess(Crop loadedCrop) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (loadedCrop == null) {
                            Toast.makeText(
                                    CropDashboardActivity.this,
                                    "Crop details not found.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        crop = loadedCrop;

                        tvDashCrop.setText(crop.getName());

                        tvDashInfo.setText(
                                "Season: "
                                        + crop.getSuitableSeasonsText()
                                        + "  |  Water: "
                                        + crop.getWaterRequirement()
                                        + "\nDuration: "
                                        + crop.getGrowthDuration()
                        );

                        loadGuide(cropName);
                    }

                    @Override
                    public void onFailure(Exception e) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        Toast.makeText(
                                CropDashboardActivity.this,
                                "Could not load crop: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
    private void loadGuide(String cropName) {

        new FirestoreCropGuideManager().getGuide(
                cropName,
                new FirestoreCropGuideManager.OnGuideLoadedListener() {

                    @Override
                    public void onSuccess(
                            FirestoreCropGuideManager.CropGuide loadedGuide) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        guide = loadedGuide;
                        refreshStageCard();
                    }

                    @Override
                    public void onFailure(Exception e) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        Toast.makeText(
                                CropDashboardActivity.this,
                                "Could not load crop growth guide.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSowingDateDisplay();

        if (guide != null) {
            refreshStageCard();
        }
    }

    private void updateSowingDateDisplay() {

        if (tvSowingDateDisplay == null) {
            return;
        }

        if (!CropStageHelper.hasSowingDate(this)) {

            tvSowingDateDisplay.setText(
                    "Select your sowing date"
            );

            return;
        }

        String date = new SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
        ).format(
                new Date(CropStageHelper.getSowingDate(this))
        );

        tvSowingDateDisplay.setText(date);
    }



    private void refreshStageCard() {

        if (crop == null || guide == null) {
            return;
        }

        List<FirestoreCropGuideManager.StageData> stages =
                guide.stages;

        if (stages == null || stages.isEmpty()) {

            tvDashStage.setText("Growth stages unavailable");

            tvDashActivity.setText(
                    "Crop growth information is not available yet."
            );

            progressGrowth.setProgress(0);
            return;
        }

        // If the user has not selected a sowing date
        if (!CropStageHelper.hasSowingDate(this)) {

            tvDashStage.setText("Sowing date not set");

            tvDashActivity.setText(
                    "Set your sowing date to view your crop's "
                            + "current growth stage and daily guidance."
            );

            progressGrowth.setProgress(0);

            btnSetSowingDate.setText("Choose Sowing Date");

            updateSowingDateDisplay();
            return;
        }

        updateSowingDateDisplay();

        int days = CropStageHelper.getDaysSinceSowing(this);

        int index = CropStageHelper.getCurrentStageIndex(
                this,
                stages
        );

        int total = Math.max(
                stages.get(stages.size() - 1).getEndDay(),
                1
        );

        // Sowing date is in the future
        if (days < 0) {

            tvDashStage.setText(
                    "Sowing in " + (-days) + " day(s)"
            );

            tvDashActivity.setText(
                    "Get your field ready before sowing."
            );

            progressGrowth.setProgress(0);
            return;
        }

        // Display the final stage if the crop duration has passed
        if (index < 0) {

            FirestoreCropGuideManager.StageData lastStage =
                    stages.get(stages.size() - 1);

            tvDashStage.setText(
                    "Day " + days + " • " + lastStage.getName()
            );

            tvDashActivity.setText(
                    "Estimated crop duration: "
                            + total
                            + " days. Check the crop before harvesting."
            );

            progressGrowth.setProgress(100);
            return;
        }

        FirestoreCropGuideManager.StageData stage =
                stages.get(index);

        tvDashStage.setText(
                "Day "
                        + days
                        + " of ~"
                        + total
                        + " • "
                        + stage.getName()
        );

        tvDashActivity.setText(
                "Today's guidance: "
                        + stage.getActivity()
        );

        int percentage = Math.min(
                100,
                Math.max(0, days * 100 / total)
        );

        progressGrowth.setProgress(percentage);
    }


    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        if (CropStageHelper.hasSowingDate(this)) {

            calendar.setTimeInMillis(
                    CropStageHelper.getSowingDate(this)
            );
        }

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    Calendar pickedDate = Calendar.getInstance();

                    pickedDate.clear();

                    pickedDate.set(
                            year,
                            month,
                            day,
                            12,
                            0,
                            0
                    );

                    PrefsManager.saveLong(
                            this,
                            PrefsManager.KEY_SOWING_DATE,
                            pickedDate.getTimeInMillis()
                    );

                    ReminderManager.cancelAll(this);

                    updateSowingDateDisplay();
                    refreshStageCard();

                    Toast.makeText(
                            this,
                            "Sowing date saved.",
                            Toast.LENGTH_SHORT
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );


        datePicker.show();
    }
}