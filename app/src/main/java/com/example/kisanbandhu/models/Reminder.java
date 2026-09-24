package com.example.kisanbandhu.models;

/** A crop activity reminder tied to a date computed from the sowing date. */
public class Reminder {

    private final String title;
    private final String message;
    private final long timeMillis;

    public Reminder(String title, String message, long timeMillis) {
        this.title = title;
        this.message = message;
        this.timeMillis = timeMillis;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimeMillis() { return timeMillis; }
}
