package com.example.crimsonskillboostmobilev2;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Get all courses
    @GET("auth/get-courses")
    Call<List<CourseModel>> getCourses();

    // Get all lessons
    @GET("auth/get-lessons")
    Call<List<LessonModel>> getLessons();

    // Get all quizzes
    @GET("auth/get-quizzes")
    Call<List<QuizModel>> getQuizzes();

    // Get quiz questions by quiz ID
    Call<List<QuestionModel>> getQuizQuestions(@Query("quizId") int quizId);

    // Get all tasks
    @GET("auth/get-tasks")
    Call<List<TaskModel>> getTasks();

    // Get task details by task ID
    @GET("auth/get-task-details")
    Call<TaskModel> getTaskDetails(@Query("taskId") int taskId);

    // Upload a task
    @Multipart
    @POST("auth/upload-task")
    Call<Void> uploadTask(@Part("taskId") RequestBody taskId, @Part MultipartBody.Part file);

    // Enroll in a course
    @GET("enrollment/enroll-in-course")
    Call<Void> enrollInCourse(
            @Query("student_id") String studentId,
            @Query("course_id") String courseId
    );

    // Get enrolled courses by student ID
    @GET("enrollment/get-enrolled-courses")
    Call<List<CourseModel>> getEnrolledCourses(@Query("student_id") int studentId);

    // Submit an enrollment request
    @POST("enrollment/apiSubmit")
    Call<Void> apiSubmitEnrollment(@Body RequestBody enrollmentRequest);

    // Get student requests by student ID
    @GET("enrollment/apiStudentRequests/{studentId}")
    Call<List<EnrollmentModel>> apiStudentRequests(@Path("studentId") int studentId);

    // Get course requests by course ID
    @GET("enrollment/apiCourseRequests/{courseId}")
    Call<List<EnrollmentModel>> apiCourseRequests(@Path("courseId") int courseId);

    // Update enrollment status by enrollment ID
    @POST("enrollment/apiUpdateStatus/{enrollmentId}")
    Call<Void> apiUpdateStatus(@Path("enrollmentId") int enrollmentId, @Body RequestBody statusUpdate);

    // Get all pending enrollments
    @GET("enrollment/apiPending")
    Call<List<EnrollmentModel>> apiPendingEnrollments();
}