package com.example.crimsonskillboostmobilev2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("auth/get-courses")
    Call<List<CourseModel>> getCourses();
}

