package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

public class CourseModel {

    @SerializedName("id")
    private int id;

    @SerializedName("course_name")
    private String title;

    @SerializedName("overview")
    private String content;

    @SerializedName("instructor_name")
    private String instructorName;

    @SerializedName("instructor_email")
    private String instructorEmail;

    @SerializedName("topic")
    private String topic;

    @SerializedName("requirements")
    private String requirements;

    private int progress = 0;

    private boolean pending = true;

    private int iconResId = R.drawable.gamedev_icon;

    public CourseModel() {}

    public CourseModel(int id, String title, String content, String instructorName, String instructorEmail,
                       String topic, String requirements, int progress, boolean pending, int iconResId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.instructorName = instructorName;
        this.instructorEmail = instructorEmail;
        this.topic = topic;
        this.requirements = requirements;
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

    public String getInstructorName() {
        return instructorName;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public String getTopic() {
        return topic;
    }

    public String getRequirements() {
        return requirements;
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

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
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
