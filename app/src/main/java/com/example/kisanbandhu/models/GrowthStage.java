package com.example.kisanbandhu.models;

/** One growth stage of a crop, with its day range counted from sowing. */
public class GrowthStage {

    private final String name;
    private final int startDay;
    private final int endDay;
    private final String activity;

    public GrowthStage(String name, int startDay, int endDay, String activity) {
        this.name = name;
        this.startDay = startDay;
        this.endDay = endDay;
        this.activity = activity;
    }

    public String getName() { return name; }
    public int getStartDay() { return startDay; }
    public int getEndDay() { return endDay; }
    public String getActivity() { return activity; }
}
