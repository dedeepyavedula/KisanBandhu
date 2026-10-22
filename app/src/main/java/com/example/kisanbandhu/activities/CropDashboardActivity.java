package com.example.kisanbandhu.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kisanbandhu.R;
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

public class CropDashboardActivity
        extends BaseActivity {

    private Crop crop;

    private FirestoreCropGuideManager.CropGuide guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_crop_dashboard
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

            startActivity(
                    new Intent(
                            this,
                            CropSelectionActivity.class
                    )
            );

            finish();
            return;
        }

        loadCrop(cropName);

        Button btnSowing =
                findViewById(
                        R.id.btnSetSowingDate
                );

        btnSowing.setOnClickListener(
                v -> showDatePicker()
        );

        // The crop dashboard now has one focused action: open the stage calendar.
        open(
                R.id.btnCalendar,
                CropCalendarActivity.class
        );
    }

    private void loadCrop(String cropName) {

        new FirestoreCropManager()
                .getCrop(
                        cropName,
                        new FirestoreCropManager.OnCropLoadedListener() {

                            @Override
                            public void onSuccess(Crop loadedCrop) {

                                crop = loadedCrop;

                                TextView title =
                                        findViewById(
                                                R.id.tvDashCrop
                                        );

                                TextView info =
                                        findViewById(
                                                R.id.tvDashInfo
                                        );

                                title.setText(
                                        crop.getName()
                                );

                                info.setText(
                                        "Season: "
                                                + crop.getSuitableSeasonsText()
                                                + " | Water: "
                                                + crop.getWaterRequirement()
                                                + " | Duration: "
                                                + crop.getGrowthDuration()
                                );

                                loadGuide(cropName);
                            }

                            @Override
                            public void onFailure(Exception e) {

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

        new FirestoreCropGuideManager()
                .getGuide(
                        cropName,
                        new FirestoreCropGuideManager.OnGuideLoadedListener() {

                            @Override
                            public void onSuccess(
                                    FirestoreCropGuideManager.CropGuide loadedGuide) {

                                guide = loadedGuide;

                                refreshStageCard();
                            }

                            @Override
                            public void onFailure(Exception e) {

                                Toast.makeText(
                                        CropDashboardActivity.this,
                                        "Could not load crop guide",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (guide != null) {
            refreshStageCard();
        }
    }

    private void open(
            int buttonId,
            Class<?> target) {

        findViewById(buttonId)
                .setOnClickListener(
                        v -> startActivity(
                                new Intent(this, target)
                        )
                );
    }

    private void refreshStageCard() {

        if (crop == null || guide == null) {
            return;
        }

        TextView tvStage =
                findViewById(R.id.tvDashStage);

        TextView tvActivity =
                findViewById(R.id.tvDashActivity);

        ProgressBar progress =
                findViewById(R.id.progressGrowth);

        Button btnSowing =
                findViewById(R.id.btnSetSowingDate);

        List<FirestoreCropGuideManager.StageData> stages =
                guide.stages;

        if (stages.isEmpty()) {
            return;
        }

        if (!CropStageHelper.hasSowingDate(this)) {

            tvStage.setText(
                    "Sowing date not set"
            );

            tvActivity.setText(
                    "Set your sowing date to see the growth stage, fertilizer timing and reminders."
            );

            progress.setProgress(0);

            btnSowing.setText(
                    "Set Sowing Date"
            );

            return;
        }

        String date =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                ).format(
                        new Date(
                                CropStageHelper.getSowingDate(this)
                        )
                );

        btnSowing.setText(
                "Sowing date: "
                        + date
                        + " (tap to change)"
        );

        int days =
                CropStageHelper.getDaysSinceSowing(this);

        int index =
                CropStageHelper.getCurrentStageIndex(
                        this,
                        stages
                );

        if (days < 0) {

            tvStage.setText(
                    "Sowing starts in "
                            + (-days)
                            + " day(s)"
            );

            tvActivity.setText(
                    "Prepare the field before sowing."
            );

            progress.setProgress(0);

            return;
        }

        if (index < 0) {
            return;
        }

        FirestoreCropGuideManager.StageData stage =
                stages.get(index);

        int total =
                stages.get(
                        stages.size() - 1
                ).getEndDay();

        tvStage.setText(
                "Day "
                        + days
                        + " of ~"
                        + total
                        + " • "
                        + stage.getName()
        );

        tvActivity.setText(
                "Now: "
                        + stage.getActivity()
        );

        progress.setProgress(
                Math.min(
                        100,
                        days * 100 / Math.max(total, 1)
                )
        );
    }

    private void showDatePicker() {

        Calendar cal =
                Calendar.getInstance();

        if (CropStageHelper.hasSowingDate(this)) {

            cal.setTimeInMillis(
                    CropStageHelper.getSowingDate(this)
            );
        }

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    Calendar picked =
                            Calendar.getInstance();

                    picked.clear();

                    picked.set(
                            year,
                            month,
                            day
                    );

                    PrefsManager.saveLong(
                            this,
                            PrefsManager.KEY_SOWING_DATE,
                            picked.getTimeInMillis()
                    );

                    ReminderManager.cancelAll(this);

                    Toast.makeText(
                            this,
                            "Sowing date saved.",
                            Toast.LENGTH_SHORT
                    ).show();

                    refreshStageCard();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
