package com.example.kisanbandhu.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/** Saves Soil Analysis district/state to the signed-in farmer's Firestore profile. */
public final class FarmerLocationStore {
    private FarmerLocationStore() { }

    public static void saveSoilLocation(Context context, String district, String state) {
        String cleanDistrict = district == null ? "" : district.trim();
        String cleanState = state == null ? "" : state.trim();
        if (cleanDistrict.isEmpty() || cleanState.isEmpty()) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "Please sign in to save your farm location.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("district", cleanDistrict);
        profile.put("state", cleanState);
        profile.put("farmLocation", cleanDistrict + ", " + cleanState);
        profile.put("soilLocationUpdatedAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .set(profile, SetOptions.merge())
                .addOnFailureListener(error -> Toast.makeText(
                        context,
                        "Could not save district and state to Firestore.",
                        Toast.LENGTH_LONG
                ).show());
    }
}
