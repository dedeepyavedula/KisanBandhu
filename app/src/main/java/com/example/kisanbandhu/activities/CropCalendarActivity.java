package com.example.kisanbandhu.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kisanbandhu.R;
import com.example.kisanbandhu.adapters.InfoAdapter;
import com.example.kisanbandhu.data.CropData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.GrowthStage;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.utils.CropStageHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** PHASE 4 - CROP CALENDAR: growth-stage timeline, with real dates once sowing date is set. */
public class CropCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        Crop crop = CropData.getSelectedCrop(this);
        if (crop == null) {
            Toast.makeText(this, "Please select a crop first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(crop.getEmoji() + " " + crop.getName() + " Calendar");
        boolean hasDate = CropStageHelper.hasSowingDate(this);
        ((TextView) findViewById(R.id.tvSubtitle)).setText(hasDate
                ? "Growth-stage timeline from your sowing date"
                : "Showing day numbers. Set a sowing date on the dashboard to see real dates.");

        List<GrowthStage> stages = CropStageHelper.getStages(crop);
        int current = CropStageHelper.getCurrentStageIndex(this, stages);
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM", Locale.getDefault());

        List<InfoItem> items = new ArrayList<>();
        for (int i = 0; i < stages.size(); i++) {
            GrowthStage s = stages.get(i);
            String when = "Day " + s.getStartDay() + " - " + s.getEndDay();
            if (hasDate) {
                long sow = CropStageHelper.getSowingDate(this);
                when += "  (" + fmt.format(new Date(sow + s.getStartDay() * CropStageHelper.DAY_MS))
                        + " - " + fmt.format(new Date(sow + s.getEndDay() * CropStageHelper.DAY_MS)) + ")";
            }
            boolean isCurrent = i == current;
            items.add(new InfoItem((i + 1) + ". " + s.getName(),
                    isCurrent ? "NOW" : "", Color.parseColor("#2E7D32"),
                    when + "\n" + s.getActivity(), isCurrent));
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new InfoAdapter(items));
    }
}
