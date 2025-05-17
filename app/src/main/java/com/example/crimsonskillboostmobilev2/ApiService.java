package com.example.crimsonskillboostmobilev2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/courses")
    Call<List<CourseModel>> getCourses();
}

