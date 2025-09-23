package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class TaskModel {
    private String id;
    private String courseId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("status")
    private String status;

    @SerializedName("allow_late")
    private boolean allowLate;

    @SerializedName("attempts")
    private int attempts;

    // ✅ Match Firestore snake_case fields
    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_url")
    private String fileUrl;

    @SerializedName("file_size")
    private long fileSize;

    @SerializedName("file_type")
    private String fileType;

    // --- Getters ---
    public String getId() { return id; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getEndDate() { return endDate; }
    public String getStartDate() { return startDate; }
    public String getStatus() { return status; }
    public boolean isAllowLate() { return allowLate; }
    public int getAttempts() { return attempts; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setStatus(String status) { this.status = status; }
    public void setAllowLate(boolean allowLate) { this.allowLate = allowLate; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
