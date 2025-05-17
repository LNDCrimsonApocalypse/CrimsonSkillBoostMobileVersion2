package com.example.crimsonskillboostmobilev2;

public class CourseModel {
    private int id;
    private String title;
    private String content;

    // Add getter methods
    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return content;
    }

    public int getProgress() {
        return 0; // Placeholder or derive logic later
    }

    public boolean isPending() {
        return false; // Placeholder or map from your data
    }

    public int getIconResId() {
        return android.R.drawable.ic_dialog_info; // Default drawable or logic based on title
    }
}

