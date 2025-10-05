package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectDetailsEnrolledCourse extends AppCompatActivity {

    private TextView tvCourseTitle, tvInstructorName, tvInstructorEmail, tvCourseOverview;
    private RecyclerView rvTopics, rvTasks, rvQuizzes;
    private TopicsAdapter topicsAdapter;
    private TasksAdapter tasksAdapter;
    private QuizzesAdapter quizzesAdapter;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_enrolled_course);

        // View bindings
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvInstructorName = findViewById(R.id.tvInstructorName);
        tvInstructorEmail = findViewById(R.id.tvInstructorEmail);
        tvCourseOverview = findViewById(R.id.tvCourseOverview);
        rvTopics = findViewById(R.id.rvTopics);
        rvTasks = findViewById(R.id.rvTasks);
        rvQuizzes = findViewById(R.id.rvQuizzes);
        ImageView ivBack = findViewById(R.id.ivBack);

        // Retrieve courseId
        courseId = getIntent().getStringExtra("course_id");
        Log.d("SubjectDetailsEnrolledCourse", "Received course_id: " + courseId);

        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course ID received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Setup RecyclerViews
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        topicsAdapter = new TopicsAdapter(new ArrayList<>(), (title, description) -> {
            Intent intent = new Intent(this, TopicsPageActivity.class);
            intent.putExtra("topic_title", title);
            intent.putExtra("topic_description", description);
            startActivity(intent);
        });
        rvTopics.setAdapter(topicsAdapter);

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(new ArrayList<>(), task -> {
            Intent intent = new Intent(this, TaskPath2Activity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
        rvTasks.setAdapter(tasksAdapter);

        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizzesAdapter = new QuizzesAdapter(new ArrayList<>(), this, courseId);
        rvQuizzes.setAdapter(quizzesAdapter);

        // Load Firestore data
        loadCourseDetails(courseId);
        loadCourseTopics(courseId);
        loadCourseTasks(courseId);
        loadCourseQuizzes(courseId);
    }

    private void loadCourseDetails(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses").document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("Firestore", "Course details fetched: " + documentSnapshot.getData());
                        tvCourseTitle.setText(documentSnapshot.getString("course_name"));
                        tvInstructorName.setText(documentSnapshot.getString("instructor_name"));

                        // Fetch instructor email using user_id
                        String userId = documentSnapshot.getString("user_id");
                        if (userId != null && !userId.isEmpty()) {
                            firestore.collection("users").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            String email = userDocument.getString("email");
                                            tvInstructorEmail.setText(email != null ? email : "Email Not Available");
                                        } else {
                                            tvInstructorEmail.setText("Email Not Available");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error fetching email: " + e.getMessage(), e);
                                        tvInstructorEmail.setText("Email Not Available");
                                    });
                        } else {
                            tvInstructorEmail.setText("Email Not Available");
                        }

                        tvCourseOverview.setText(documentSnapshot.getString("overview"));
                    } else {
                        Log.e("Firestore", "Course not found for ID: " + courseId);
                        Toast.makeText(this, "Course details not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching course details: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load course details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCourseTopics(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses").document(courseId).collection("topics")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TopicModel> topics = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        TopicModel topic = new TopicModel();
                        topic.setTitle(document.getString("title"));
                        topic.setDescription(document.getString("description"));
                        topic.setCreatedAt(document.getTimestamp("created_at"));
                        topic.setCreatedBy(document.getString("created_by"));
                        topics.add(topic);
                    });
                    topicsAdapter.updateTopics(topics);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseTopicsError", e.getMessage(), e);
                });
    }

    private void loadCourseTasks(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses").document(courseId).collection("tasks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TaskModel> tasks = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        TaskModel task = new TaskModel();
                        task.setTitle(document.getString("title"));

                        // Format the end_date
                        String endDate = document.getString("end_date");
                        if (endDate != null && !endDate.isEmpty()) {
                            try {
                                task.setEndDate(formatDate(endDate)); // Format the date
                            } catch (Exception e) {
                                Log.e("SubjectDetailsEnrolledCourse", "Error formatting end_date for task: " + document.getId(), e);
                                task.setEndDate("Invalid Date");
                            }
                        } else {
                            task.setEndDate("No Due Date");
                        }

                        task.setStatus(document.getString("status"));
                        task.setId(document.getId());
                        task.setCourseId(courseId);
                        tasks.add(task);
                    });
                    tasksAdapter.updateTasks(tasks);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseTasksError", e.getMessage(), e);
                });
    }

    // Helper method to format the date
    private String formatDate(String date) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return outputFormat.format(inputFormat.parse(date));
    }

    private void loadCourseQuizzes(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("quizzes")
                .whereEqualTo("course_id", courseId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<QuizModel> quizzes = new ArrayList<>();
                    querySnapshot.forEach(document -> {
                        QuizModel quiz = new QuizModel();
                        quiz.setId(document.getId());
                        quiz.setTitle(document.getString("title"));
                        quiz.setDescription(document.getString("description"));
                        quiz.setCourseId(document.getString("course_id"));
                        quiz.setPublished(document.getBoolean("published") != null ? document.getBoolean("published") : false);
                        quiz.setAttempts(document.getLong("attempts") != null ? document.getLong("attempts").intValue() : 0);
                        quiz.setCompleted(document.getBoolean("completed") != null ? document.getBoolean("completed") : false);

                        // ✅ Handle new Firestore fields
                        quiz.setStartDate(document.getString("start_date"));
                        quiz.setEndDate(document.getString("end_date"));
                        quiz.setAllowLate(document.getBoolean("allow_late") != null ? document.getBoolean("allow_late") : false);

                        // Format created_at if exists
                        Object createdAtObj = document.get("created_at");
                        if (createdAtObj != null) {
                            try {
                                String formattedDate;
                                if (createdAtObj instanceof com.google.firebase.Timestamp) {
                                    formattedDate = formatDate(((com.google.firebase.Timestamp) createdAtObj).toDate().toString());
                                } else if (createdAtObj instanceof String) {
                                    formattedDate = formatDate((String) createdAtObj);
                                } else {
                                    formattedDate = "Invalid Date";
                                }
                                quiz.setCreatedAt(formattedDate);
                            } catch (Exception e) {
                                Log.e("SubjectDetailsEnrolledCourse", "Error formatting created_at for quiz: " + document.getId(), e);
                                quiz.setCreatedAt("Invalid Date");
                            }
                        } else {
                            quiz.setCreatedAt("No Date Available");
                        }

                        quizzes.add(quiz);
                    });
                    quizzesAdapter.updateQuizzes(quizzes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load quizzes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseQuizzesError", e.getMessage(), e);
                });
    }
}
