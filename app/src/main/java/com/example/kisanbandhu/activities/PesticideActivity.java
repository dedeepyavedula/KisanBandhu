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
import com.example.kisanbandhu.models.InfoItem;
import com.example.kisanbandhu.models.PestInfo;

import java.util.ArrayList;
import java.util.List;

/** PHASE 4 - PEST & PESTICIDE GUIDANCE for the selected crop (chemical + natural control). */
public class PesticideActivity extends AppCompatActivity {

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

        ((TextView) findViewById(R.id.tvTitle)).setText(crop.getEmoji() + " " + crop.getName() + " Pests & Diseases");
        ((TextView) findViewById(R.id.tvSubtitle)).setText(
                "Prefer natural methods first. Always follow label dose and wear protective gear.");

        List<InfoItem> items = new ArrayList<>();
        for (PestInfo p : CropGuideData.getPests(crop.getName())) {
            items.add(new InfoItem(p.getName(), "", Color.BLACK,
                    "Symptoms:\n" + p.getSymptoms()
                            + "\n\nChemical control:\n" + p.getChemicalControl()
                            + "\n\nNatural / Organic control:\n" + p.getOrganicControl(), false));
        }

        TextView empty = findViewById(R.id.tvEmpty);
        if (items.isEmpty()) {
            empty.setText("No pest information available for this crop yet.");
            empty.setVisibility(android.view.View.VISIBLE);
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new InfoAdapter(items));
    }
}
