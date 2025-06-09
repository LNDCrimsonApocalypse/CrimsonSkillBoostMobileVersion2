package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SubjectDetailsEnrolledCourse extends AppCompatActivity {

    private TextView tvCourseTitle, tvInstructorName, tvInstructorEmail, tvCourseOverview;
    private RecyclerView rvTopics, rvTasks, rvQuizzes;
    private TopicsAdapter topicsAdapter;
    private TasksAdapter tasksAdapter;
    private QuizzesAdapter quizzesAdapter;

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

        // Retrieve courseId from intent
        String courseId = getIntent().getStringExtra("course_id");

        // Validate courseId
        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course ID received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Back button behavior
        ivBack.setOnClickListener(v -> finish());

        // Initialize RecyclerViews
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        topicsAdapter = new TopicsAdapter(new ArrayList<>());
        rvTopics.setAdapter(topicsAdapter);

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(new ArrayList<>());
        rvTasks.setAdapter(tasksAdapter);

        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizzesAdapter = new QuizzesAdapter(new ArrayList<>());
        rvQuizzes.setAdapter(quizzesAdapter);

        // Load course details, topics, tasks, and quizzes
        loadCourseDetails(courseId);
        loadCourseTopics(courseId);
        loadCourseTasks(courseId);
        loadCourseQuizzes(courseId);
    }

    private void loadCourseDetails(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch course details from Firestore
        firestore.collection("enrolled_courses").document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvCourseTitle.setText(documentSnapshot.getString("title"));
                        tvInstructorName.setText(documentSnapshot.getString("instructor_name"));
                        tvInstructorEmail.setText(documentSnapshot.getString("instructor_email"));
                        tvCourseOverview.setText(documentSnapshot.getString("overview"));
                    } else {
                        Toast.makeText(this, "Course details not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load course details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCourseTopics(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch topics from Firestore
        firestore.collection("enrolled_courses").document(courseId).collection("topics")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> topics = new ArrayList<>();
                    querySnapshot.forEach(document -> topics.add(document.getString("name")));
                    topicsAdapter.updateTopics(topics);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCourseTasks(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch tasks from Firestore
        firestore.collection("enrolled_courses").document(courseId).collection("tasks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> tasks = new ArrayList<>();
                    querySnapshot.forEach(document -> tasks.add(document.getString("name")));
                    tasksAdapter.updateTasks(tasks);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCourseQuizzes(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch quizzes from Firestore
        firestore.collection("enrolled_courses").document(courseId).collection("quizzes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> quizzes = new ArrayList<>();
                    querySnapshot.forEach(document -> quizzes.add(document.getString("name")));
                    quizzesAdapter.updateQuizzes(quizzes);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load quizzes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}