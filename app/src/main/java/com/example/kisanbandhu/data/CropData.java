// FILE: app/src/main/java/com/example/kisanbandhu/data/CropData.java
package com.example.kisanbandhu.data;

import com.example.kisanbandhu.models.Crop;

import java.util.ArrayList;
import java.util.List;

/**
 * Local "database" of crops. For this college project we keep it as a
 * plain static Java list instead of SQLite/JSON, so it's easy to read
 * and explain in a viva. To add a crop, just add one more line here -
 * no other file needs to change.
 */
public class CropData {

    public static List<Crop> getAllCrops() {
        List<Crop> crops = new ArrayList<>();

        crops.add(new Crop("Rice", "🌾",
                new String[]{"Clay", "Alluvial soil", "Loamy"},
                new String[]{"Kharif"},
                "High", "120-150 days",
                "Rice needs standing water for much of its growth and does well in " +
                        "clay or alluvial soils that hold moisture.",
                false, false, true));

        crops.add(new Crop("Wheat", "🌾",
                new String[]{"Loamy", "Alluvial soil", "Black soil"},
                new String[]{"Rabi"},
                "Moderate", "110-130 days",
                "Wheat grows well in cooler months on fertile, well-drained soil.",
                false, false, true));

        crops.add(new Crop("Maize", "🌽",
                new String[]{"Loamy", "Sandy", "Alluvial soil"},
                new String[]{"Kharif", "Rabi"},
                "Moderate", "90-110 days",
                "Maize is fairly adaptable and can be grown across two seasons in " +
                        "many regions.",
                true, false, false));

        crops.add(new Crop("Groundnut", "🥜",
                new String[]{"Sandy", "Red soil"},
                new String[]{"Kharif"},
                "Moderate", "100-120 days",
                "Groundnut prefers well-drained sandy soils and moderate rainfall.",
                true, true, false));

        crops.add(new Crop("Cotton", "🌱",
                new String[]{"Black soil", "Alluvial soil"},
                new String[]{"Kharif"},
                "Moderate", "150-180 days",
                "Cotton is well suited to black (regur) soil, which retains moisture " +
                        "through the long growing period.",
                false, false, true));

        crops.add(new Crop("Soybean", "🌱",
                new String[]{"Black soil", "Loamy"},
                new String[]{"Kharif"},
                "Moderate", "90-100 days",
                "Soybean does reasonably well under rain-fed conditions with moderate " +
                        "rainfall.",
                true, false, false));

        crops.add(new Crop("Sugarcane", "🎋",
                new String[]{"Alluvial soil", "Loamy", "Clay"},
                new String[]{"Kharif"},
                "High", "270-360 days",
                "Sugarcane is a long-duration, water-intensive crop that performs " +
                        "best with assured irrigation.",
                false, false, true));

        crops.add(new Crop("Bajra (Pearl Millet)", "🌾",
                new String[]{"Sandy", "Red soil"},
                new String[]{"Kharif"},
                "Low", "70-90 days",
                "Bajra is drought-tolerant and grows well in low-fertility sandy soils " +
                        "with limited water.",
                true, true, false));

        crops.add(new Crop("Chickpea (Gram)", "🫘",
                new String[]{"Black soil", "Loamy", "Alluvial soil"},
                new String[]{"Rabi"},
                "Low", "90-120 days",
                "Chickpea needs relatively little water and can do well as a " +
                        "rain-fed Rabi crop.",
                true, true, false));

        crops.add(new Crop("Mustard", "🌼",
                new String[]{"Loamy", "Alluvial soil", "Sandy"},
                new String[]{"Rabi"},
                "Low", "110-140 days",
                "Mustard is a low-water Rabi crop that tolerates moderately low " +
                        "fertility soils.",
                true, true, false));

        crops.add(new Crop("Watermelon", "🍉",
                new String[]{"Sandy", "Alluvial soil"},
                new String[]{"Zaid"},
                "High", "60-90 days",
                "Watermelon is a short-duration summer (Zaid) crop that needs " +
                        "consistent irrigation.",
                false, false, false));

        return crops;
    }

    /** Finds a crop by its exact name, or null if there is no such crop. */
    public static Crop getByName(String name) {
        if (name == null) return null;
        for (Crop crop : getAllCrops()) {
            if (crop.getName().equals(name)) return crop;
        }
        return null;
    }

    /** The crop the farmer selected in Phase 4 (null if none yet). */
    public static Crop getSelectedCrop(android.content.Context context) {
        return getByName(com.example.kisanbandhu.utils.PrefsManager.getString(
                context, com.example.kisanbandhu.utils.PrefsManager.KEY_SELECTED_CROP, null));
    }
}
