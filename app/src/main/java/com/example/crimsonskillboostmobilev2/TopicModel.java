package com.example.crimsonskillboostmobilev2;

import com.google.firebase.Timestamp;

public class TopicModel {
    private String id;
    private String title;
    private String description;
    private Timestamp createdAt;
    private String createdBy;

    // ✅ New fields
    private String requiredTopic;
    private boolean locked;

    // Empty constructor (required by Firestore)
    public TopicModel() {}

    // Optional full constructor
    public TopicModel(String id, String title, String description, Timestamp createdAt,
                      String createdBy, String requiredTopic, boolean locked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.requiredTopic = requiredTopic;
        this.locked = locked;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public String getRequiredTopic() { return requiredTopic; }
    public boolean isLocked() { return locked; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setRequiredTopic(String requiredTopic) { this.requiredTopic = requiredTopic; }
    public void setLocked(boolean locked) { this.locked = locked; }
}
