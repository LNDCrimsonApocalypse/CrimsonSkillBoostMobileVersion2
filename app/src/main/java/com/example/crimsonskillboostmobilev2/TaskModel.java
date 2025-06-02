package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class TaskModel {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("year")
    private int year;

    @SerializedName("section")
    private String section;

    @SerializedName("semester")
    private int semester;

    @SerializedName("courses")
    private String courses;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("allow_late")
    private boolean allowLate;

    @SerializedName("attempts")
    private int attempts;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getYear() {
        return year;
    }

    public String getSection() {
        return section;
    }

    public int getSemester() {
        return semester;
    }

    public String getCourses() {
        return courses;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isAllowLate() {
        return allowLate;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setAllowLate(boolean allowLate) {
        this.allowLate = allowLate;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}