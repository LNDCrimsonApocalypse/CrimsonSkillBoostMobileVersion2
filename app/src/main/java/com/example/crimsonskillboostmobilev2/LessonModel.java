package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class LessonModel {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("course_id")
    private int courseId;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}