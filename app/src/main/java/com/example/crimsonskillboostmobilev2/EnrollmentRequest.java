package com.example.crimsonskillboostmobilev2;

public class EnrollmentRequest {
    private String studentId;
    private String courseId;

    public EnrollmentRequest(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }
}
