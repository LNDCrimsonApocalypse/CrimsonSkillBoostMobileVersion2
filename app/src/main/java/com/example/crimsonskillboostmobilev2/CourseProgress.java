package com.example.crimsonskillboostmobilev2;

public class CourseProgress {
    private String title;
    private int progress;

    public CourseProgress(String title, int progress) {
        this.title = title;
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public int getProgress() {
        return progress;
    }
}

