package com.example.crimsonskillboostmobilev2;

public class QuizModel {
    private String id;
    private String title;
    private String description;
    private String courseId;
    private String createdAt;
    private boolean published;
    private String publishedAt;
    private int attempts;
    private boolean completed;

    // Empty constructor (needed for Firestore)
    public QuizModel() {}

    // Minimal constructor for when you just need id, title, and description
    public QuizModel(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // Full constructor (optional, if you want to populate everything at once)
    public QuizModel(String id, String title, String description, String courseId,
                     String createdAt, boolean published, String publishedAt,
                     int attempts, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.courseId = courseId;
        this.createdAt = createdAt;
        this.published = published;
        this.publishedAt = publishedAt;
        this.attempts = attempts;
        this.completed = completed;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
