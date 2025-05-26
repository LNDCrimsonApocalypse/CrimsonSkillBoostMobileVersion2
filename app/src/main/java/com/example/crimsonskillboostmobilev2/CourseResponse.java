package com.example.crimsonskillboostmobilev2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseResponse {
    @SerializedName("courses")
    private List<CourseModel> courses;

    public List<CourseModel> getCourses() {
        return courses;
    }
}

