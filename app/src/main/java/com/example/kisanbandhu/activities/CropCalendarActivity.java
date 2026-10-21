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
import com.example.kisanbandhu.utils.FirestoreCropGuideManager;
import com.example.kisanbandhu.utils.PrefsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CropCalendarActivity
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

                                showStages(guide);
                            }

                            @Override
                            public void onFailure(Exception e) {

                                Toast.makeText(
                                        CropCalendarActivity.this,
                                        "Could not load calendar: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void showStages(
            FirestoreCropGuideManager.CropGuide guide) {

        String displayCropName = guide.cropName;

        if (displayCropName == null
                || displayCropName.trim().isEmpty()) {

            displayCropName = PrefsManager.getString(
                    this,
                    PrefsManager.KEY_SELECTED_CROP,
                    "Crop"
            );
        }

        tvTitle.setText(displayCropName + " Calendar");

        tvTitle.setText(displayCropName + " Calendar");

        boolean hasDate =
                CropStageHelper.hasSowingDate(this);

        tvSubtitle.setText(
                hasDate
                        ? "Growth-stage timeline from your sowing date"
                        : "Set a sowing date on the dashboard to see real dates."
        );

        List<FirestoreCropGuideManager.StageData> stages =
                guide.stages;

        if (stages.isEmpty()) {

            tvEmpty.setText(
                    "No crop calendar information available."
            );

            tvEmpty.setVisibility(View.VISIBLE);

            return;
        }

        int current =
                CropStageHelper.getCurrentStageIndex(
                        this,
                        stages
                );

        SimpleDateFormat fmt =
                new SimpleDateFormat(
                        "dd MMM",
                        Locale.getDefault()
                );

        List<InfoItem> items =
                new ArrayList<>();

        for (int i = 0; i < stages.size(); i++) {

            FirestoreCropGuideManager.StageData stage =
                    stages.get(i);

            String when =
                    "Day "
                            + stage.getStartDay()
                            + " - "
                            + stage.getEndDay();

            if (hasDate) {

                long sow =
                        CropStageHelper.getSowingDate(this);

                when +=
                        " ("
                                + fmt.format(
                                new Date(
                                        sow
                                                + stage.getStartDay()
                                                * CropStageHelper.DAY_MS
                                )
                        )
                                + " - "
                                + fmt.format(
                                new Date(
                                        sow
                                                + stage.getEndDay()
                                                * CropStageHelper.DAY_MS
                                )
                        )
                                + ")";
            }

            boolean isCurrent =
                    i == current;

            items.add(
                    new InfoItem(
                            (i + 1)
                                    + ". "
                                    + stage.getName(),
                            isCurrent ? "NOW" : "",
                            Color.parseColor(
                                    "#2E7D32"
                            ),
                            when
                                    + "\n"
                                    + stage.getActivity(),
                            isCurrent
                    )
            );
        }

        recyclerView.setAdapter(
                new InfoAdapter(items)
        );
    }
}