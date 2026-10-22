package com.example.kisanbandhu.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kisanbandhu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/** Reads the signed-in farmer's name, phone, district and state from Firestore. */
public class ProfileActivity extends BaseActivity {

    private TextView tvFarmerName;
    private TextView tvFarmerPhone;
    private TextView tvFarmLocation;
    private TextView tvProfileInitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFarmerName = findViewById(R.id.tvFarmerName);
        tvFarmerPhone = findViewById(R.id.tvFarmerPhone);
        tvFarmLocation = findViewById(R.id.tvFarmLocation);
        tvProfileInitial = findViewById(R.id.tvProfileInitial);

        loadProfileFromFirestore();
    }

    private void loadProfileFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            renderProfile("Not signed in", "Not available", "Not available");
            return;
        }

        // Profile fields and soil-analysis district/state are stored on users/{uid}.
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (document == null || !document.exists()) {
                        renderProfile("Profile not found", "Not available",
                                "Complete Soil Analysis to add location");
                        return;
                    }

                    String name = firstNonBlank(readAny(document,
                                    "name", "fullName", "farmerName", "displayName", "username"),
                            "Not available");
                    String phone = firstNonBlank(readAny(document,
                                    "phone", "phoneNumber", "mobile", "mobileNumber"),
                            "Not available");

                    String district = firstNonBlank(
                            readAny(document, "district", "farmDistrict", "soilDistrict"),
                            readNested(document, "soilAnalysis", "district", "farmDistrict"),
                            readNested(document, "soilInput", "district", "farmDistrict"),
                            readNested(document, "soilInputs", "district", "farmDistrict"),
                            "");
                    String state = firstNonBlank(
                            readAny(document, "state", "farmState", "soilState"),
                            readNested(document, "soilAnalysis", "state", "farmState"),
                            readNested(document, "soilInput", "state", "farmState"),
                            readNested(document, "soilInputs", "state", "farmState"),
                            "");

                    String location;
                    if (!district.isEmpty() && !state.isEmpty()) {
                        location = district + ", " + state;
                    } else if (!district.isEmpty()) {
                        location = district;
                    } else if (!state.isEmpty()) {
                        location = state;
                    } else {
                        location = "Complete Soil Analysis to add location";
                    }

                    renderProfile(name, phone, location);
                })
                .addOnFailureListener(error -> {
                    if (isFinishing() || isDestroyed()) return;
                    renderProfile("Could not load", "Could not load",
                            "Check internet connection / Firestore access");
                    Toast.makeText(this,
                            "Could not load farmer details from Firestore",
                            Toast.LENGTH_LONG).show();
                });
    }

    private String readAny(DocumentSnapshot document, String... keys) {
        for (String key : keys) {
            Object value = document.get(key);
            String text = value == null ? "" : String.valueOf(value).trim();
            if (!text.isEmpty() && !"null".equalsIgnoreCase(text)) return text;
        }
        return "";
    }

    private String readNested(DocumentSnapshot document, String mapKey, String... keys) {
        Object value = document.get(mapKey);
        if (!(value instanceof Map)) return "";
        Map<?, ?> map = (Map<?, ?>) value;
        for (String key : keys) {
            Object nested = map.get(key);
            String text = nested == null ? "" : String.valueOf(nested).trim();
            if (!text.isEmpty() && !"null".equalsIgnoreCase(text)) return text;
        }
        return "";
    }

    private void renderProfile(String name, String phone, String location) {
        tvFarmerName.setText(name);
        tvFarmerPhone.setText(phone);
        tvFarmLocation.setText(location);
        String initial = name == null || name.trim().isEmpty()
                || "Not available".equalsIgnoreCase(name)
                ? "F" : name.trim().substring(0, 1).toUpperCase();
        tvProfileInitial.setText(initial);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) return value.trim();
        }
        return "";
    }
}
