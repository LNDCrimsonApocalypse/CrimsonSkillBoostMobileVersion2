package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class TaskModel {
    private String id; // Firestore document ID
    private String courseId; // Parent course ID from Firestore path

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("status")
    private String status;

    // Getters
    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
