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
import com.example.kisanbandhu.data.CropGuideData;
import com.example.kisanbandhu.models.Crop;
import com.example.kisanbandhu.models.FertilizerAdvice;
import com.example.kisanbandhu.models.GrowthStage;
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.utils.CropStageHelper;
import com.example.kisanbandhu.utils.FertilizerAdvisor;
import com.example.kisanbandhu.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * PHASE 4 - FERTILIZER: crop + soil + growth stage. Every stage shows the
 * chemical option and the natural/organic alternative side by side.
 */
public class FertilizerActivity extends AppCompatActivity {

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

        ((TextView) findViewById(R.id.tvTitle)).setText(crop.getEmoji() + " " + crop.getName() + " Fertilizer Plan");
        ((TextView) findViewById(R.id.tvSubtitle)).setText(
                "Indicative doses per acre. Confirm with a soil test / local KVK.");

        List<GrowthStage> stages = CropStageHelper.getStages(crop);
        int current = CropStageHelper.getCurrentStageIndex(this, stages);
        List<InfoItem> items = new ArrayList<>();

        items.add(new InfoItem("Soil-based adjustments", "YOUR SOIL", Color.parseColor("#795548"),
                FertilizerAdvisor.getSoilAdjustment(PrefsManager.getSoilProfile(this)), false));

        for (int i = 0; i < stages.size(); i++) {
            GrowthStage s = stages.get(i);
            FertilizerAdvice advice = CropGuideData.getFertilizer(crop.getName(), i);
            String body;
            if (advice != null) {
                body = "Chemical:\n" + advice.getChemical() + "\n\nNatural / Organic:\n" + advice.getOrganic();
            } else {
                body = "No major fertilizer needed at this stage. Avoid extra nitrogen; focus on "
                        + "irrigation, pest scouting and preparing for harvest.";
            }
            boolean isCurrent = i == current;
            items.add(new InfoItem(s.getName() + "  (Day " + s.getStartDay() + "-" + s.getEndDay() + ")",
                    isCurrent ? "NOW" : "", Color.parseColor("#2E7D32"), body, isCurrent));
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new InfoAdapter(items));
    }
}
