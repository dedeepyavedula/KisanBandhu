// FILE: app/src/main/java/com/example/kisanbandhu/models/Soil.java
package com.example.kisanbandhu.models;

/**
 * Simple data holder for everything the farmer tells us on the
 * Soil Input screen (Screen 2). No logic here - just fields, a
 * constructor, and getters/setters. This object is what gets saved
 * to SharedPreferences and later read back for analysis and
 * crop recommendations.
 */
public class Soil {

    private String soilType;       // e.g. "Loamy"
    private String soilPH;         // stored as String since it's optional; "" if not provided
    private String fertility;      // "Low" / "Medium" / "High"
    private String state;          // e.g. "Maharashtra"
    private String district;       // free text
    private boolean irrigated;     // true = Irrigated, false = Rain-fed
    private String season;         // "Kharif" / "Rabi" / "Zaid"

    public Soil(String soilType, String soilPH, String fertility, String state,
                String district, boolean irrigated, String season) {
        this.soilType = soilType;
        this.soilPH = soilPH;
        this.fertility = fertility;
        this.state = state;
        this.district = district;
        this.irrigated = irrigated;
        this.season = season;
    }

    public String getSoilType() { return soilType; }
    public String getSoilPH() { return soilPH; }
    public String getFertility() { return fertility; }
    public String getState() { return state; }
    public String getDistrict() { return district; }
    public boolean isIrrigated() { return irrigated; }
    public String getSeason() { return season; }

    /** Convenience: returns true if the farmer actually entered a pH value. */
    public boolean hasPH() {
        return soilPH != null && !soilPH.trim().isEmpty();
    }

    public String getIrrigationLabel() {
        return irrigated ? "Irrigated" : "Rain-fed";
    }
}
