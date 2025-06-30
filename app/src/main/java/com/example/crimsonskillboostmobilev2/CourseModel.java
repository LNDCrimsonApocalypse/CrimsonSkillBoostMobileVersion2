package com.example.crimsonskillboostmobilev2;

import com.google.firebase.Timestamp;

public class CourseModel {

    private String courseId; // Add courseId field
    private String courseName;
    private Timestamp createdAt;
    private String instructorName;
    private String userId; // Educator's ownership
    private String overview;
    private String requirements;
    private String year; // Year assigned for students
    private String section; // Section assigned for students
    private String semester; // Semester assigned for students

    // Getters
    public String getCourseId() {
        return courseId; // Getter for courseId
    }

    public String getCourseName() {
        return courseName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getUserId() {
        return userId;
    }

    public String getOverview() {
        return overview;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getYear() {
        return year;
    }

    public String getSection() {
        return section;
    }

    public String getSemester() {
        return semester;
    }

    // Setters
    public void setCourseId(String courseId) {
        this.courseId = courseId; // Setter for courseId
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}