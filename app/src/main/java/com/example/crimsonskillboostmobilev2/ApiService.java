package com.example.crimsonskillboostmobilev2;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @GET("auth/get-courses")
    Call<List<CourseModel>> getCourses();

    @GET("auth/get-lessons")
    Call<List<LessonModel>> getLessons();

    @GET("auth/get-quizzes")
    Call<List<QuizModel>> getQuizzes();

    @GET("auth/get-quiz-questions")
    Call<List<QuestionModel>> getQuizQuestions();

    @GET("auth/get-submissions")
    Call<List<SubmissionModel>> getSubmissions();

    @GET("auth/get-quiz-questions")
    Call<List<QuestionModel>> getQuizQuestions(@Query("quizId") int quizId);

    @GET("auth/get-tasks")
    Call<List<TaskModel>> getTasks();

    @GET("auth/get-task-details")
    Call<TaskModel> getTaskDetails(@Query("taskId") int taskId);

    @Multipart
    @POST("auth/upload-task")
    Call<Void> uploadTask(@Part("taskId") RequestBody taskId, @Part MultipartBody.Part file);

    // Updated enrollInCourse method in ApiService.java
    @GET("enrollment/enroll-in-course")
    Call<Void> enrollInCourse(
            @Query("student_id") String studentId,
            @Query("course_id") String courseId
    );

    Call<Void> enrollInCourse(String courseId);

    @GET("enrollment/get-enrolled-courses")
    Call<List<CourseModel>> getEnrolledCourses(@Query("student_id") int studentId);
}