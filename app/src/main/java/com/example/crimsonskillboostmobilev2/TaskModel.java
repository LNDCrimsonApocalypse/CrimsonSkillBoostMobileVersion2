package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class TaskModel {
    private String id; // Change from int to String

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("status")
    private String status;

    public String getId() { // Update getter
        return id;
    }

    public void setId(String id) { // Update setter
        this.id = id;
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