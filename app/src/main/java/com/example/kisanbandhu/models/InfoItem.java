package com.example.kisanbandhu.models;

/**
 * Generic "card" shown in the list screens (calendar, fertilizer, pesticide,
 * weather, alerts, reminders). One small model + one adapter (InfoAdapter)
 * keeps Phase 4 simple: each screen just converts its data into InfoItems.
 */
public class InfoItem {

    private final String title;
    private final String badge;       // small coloured label, "" to hide
    private final int badgeColor;
    private final String body;
    private final boolean highlighted; // e.g. the current growth stage

    public InfoItem(String title, String badge, int badgeColor, String body, boolean highlighted) {
        this.title = title;
        this.badge = badge;
        this.badgeColor = badgeColor;
        this.body = body;
        this.highlighted = highlighted;
    }

    public String getTitle() { return title; }
    public String getBadge() { return badge; }
    public int getBadgeColor() { return badgeColor; }
    public String getBody() { return body; }
    public boolean isHighlighted() { return highlighted; }
}
