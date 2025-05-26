package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class CourseModel {

    @SerializedName("id")
    private int id;

    @SerializedName("course_name")  // <-- THIS IS THE FIX
    private String title;

    // The API doesn’t return this, but keep it to avoid breaking layout binding
    private String content = "Overview not available.";

    private int progress = 0; // Default if not in JSON

    private boolean pending = true; // Default if not in JSON

    private int iconResId = R.drawable.gamedev_icon; // Fallback drawable

    public CourseModel() {}

    public CourseModel(int id, String title, String content, int progress, boolean pending, int iconResId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.progress = progress;
        this.pending = pending;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return content;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
