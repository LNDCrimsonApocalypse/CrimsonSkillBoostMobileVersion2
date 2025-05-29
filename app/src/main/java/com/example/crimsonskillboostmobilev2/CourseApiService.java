package com.example.crimsonskillboostmobilev2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CourseApiService {
    @GET("getAvailableCourses.php")
    Call<List<CourseModel>> getAvailableCourses();

    @GET("getEnrolledCourses.php")
    Call<List<CourseModel>> getEnrolledCourses(@Query("student_id") int studentId);
}

