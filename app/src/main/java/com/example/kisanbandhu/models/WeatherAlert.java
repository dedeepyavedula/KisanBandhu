package com.example.kisanbandhu.models;

/** A warning produced by WeatherAlertEngine. */
public class WeatherAlert {

    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int SEVERE = 2;

    private final String title;
    private final String message;
    private final int severity;
    private final String date;

    public WeatherAlert(String title, String message, int severity, String date) {
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public int getSeverity() { return severity; }
    public String getDate() { return date; }
}
