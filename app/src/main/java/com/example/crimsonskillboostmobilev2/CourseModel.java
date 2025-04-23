package com.example.crimsonskillboostmobilev2;

public class CourseModel {
    private String title;
    private String overview;
    private int progress;
    private boolean pending;
    private int iconResId;

    public CourseModel(String title, String overview, int progress, boolean pending, int iconResId) {
        this.title = title;
        this.overview = overview;
        this.progress = progress;
        this.pending = pending;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isPending() {
        return pending;
    }

    public int getIconResId() {
        return iconResId;
    }
}
