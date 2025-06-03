package com.example.crimsonskillboostmobilev2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("auth/get-courses")
    Call<List<CourseModel>> getCourses();

    @GET("auth/get-lessons")
    Call<List<LessonModel>> getLessons();

    @GET("auth/get-tasks")
    Call<List<TaskModel>> getTasks();

    @GET("auth/get-quizzes")
    Call<List<QuizModel>> getQuizzes();

    @GET("auth/get-quiz-questions")
    Call<List<QuestionModel>> getQuizQuestions();

    @GET("auth/get-submissions")
    Call<List<SubmissionModel>> getSubmissions();

    @GET("auth/get-quiz-questions")
    Call<List<QuestionModel>> getQuizQuestions(@Query("quizId") int quizId);
}