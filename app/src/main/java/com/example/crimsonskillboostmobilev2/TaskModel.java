package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class TaskModel {

    private String id;          // Firestore document ID
    private String courseId;    // Parent course document ID

    // 🔹 Basic info
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    // 🔹 Scheduling
    @SerializedName("start_date")
    private String startDate;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("start_datetime")
    private String startDatetime;

    @SerializedName("end_date")
    private String endDate; // optional (not always in Firestore)

    // 🔹 File info
    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_url")
    private String fileUrl;

    @SerializedName("file_size")
    private long fileSize;

    @SerializedName("file_type")
    private String fileType;

    // 🔹 Task dependencies
    @SerializedName("requiredTask")
    private String requiredTask;

    @SerializedName("requiredTopic")
    private String requiredTopic; // renamed from topicId for Firestore consistency

    // 🔹 Status / configuration
    @SerializedName("status")
    private String status;

    @SerializedName("allow_late")
    private boolean allowLate;

    @SerializedName("attempts")
    private int attempts;

    // 🔹 App-side (not from Firestore)
    private boolean locked;

    // --- Getters ---
    public String getId() { return id; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getStartTime() { return startTime; }
    public String getStartDatetime() { return startDatetime; }
    public String getEndDate() { return endDate; }
    public String getFileName() { return fileName; }
    public String getFileUrl() { return fileUrl; }
    public long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    public String getRequiredTask() { return requiredTask; }
    public String getRequiredTopic() { return requiredTopic; }
    public String getStatus() { return status; }
    public boolean isAllowLate() { return allowLate; }
    public int getAttempts() { return attempts; }
    public boolean isLocked() { return locked; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setStartDatetime(String startDatetime) { this.startDatetime = startDatetime; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setRequiredTask(String requiredTask) { this.requiredTask = requiredTask; }
    public void setRequiredTopic(String requiredTopic) { this.requiredTopic = requiredTopic; }
    public void setStatus(String status) { this.status = status; }
    public void setAllowLate(boolean allowLate) { this.allowLate = allowLate; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public void setLocked(boolean locked) { this.locked = locked; }
}
