package com.example.crimsonskillboostmobilev2;

import com.google.firebase.Timestamp;

public class CourseModel {

    private String courseName;
    private Timestamp createdAt;
    private String instructorName;
    private String overview;
    private String requirements;

    // Getters
    public String getCourseName() {
        return courseName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getOverview() {
        return overview;
    }

    public String getRequirements() {
        return requirements;
    }

    // Setters
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}