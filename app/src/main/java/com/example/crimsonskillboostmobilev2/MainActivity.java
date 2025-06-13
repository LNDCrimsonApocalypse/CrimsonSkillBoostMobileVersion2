package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView logo;
    TextView appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.Logo);
        appName = findViewById(R.id.AppName);

        appName.setText("Crimson Skill Boost");

        // Add sample data to Firebase
        addSampleDataToFirebase();

        // Transition after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

    private void addSampleDataToFirebase() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add sample course
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("course_name", "Sample Course");
        courseData.put("overview", "This is a sample course overview.");
        courseData.put("instructor_name", "John Doe");
        courseData.put("requirements", "Basic knowledge of programming.");
        courseData.put("created_at", Timestamp.now());

        firestore.collection("courses").add(courseData).addOnSuccessListener(courseRef -> {
            String courseId = courseRef.getId();
            Log.d("Firestore", "Course added with ID: " + courseId);

            // Add sample lessons/topics
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> lessonData = new HashMap<>();
                lessonData.put("title", "Lesson " + i);
                lessonData.put("description", "Description for Lesson " + i);
                lessonData.put("content", "Content for Lesson " + i);
                lessonData.put("course_id", courseId);
                lessonData.put("created_at", Timestamp.now());

                firestore.collection("lessons").add(lessonData).addOnSuccessListener(lessonRef -> {
                    Log.d("Firestore", "Lesson added with ID: " + lessonRef.getId());
                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding lesson: " + e.getMessage());
                });
            }

            // Add sample tasks
            for (int i = 1; i <= 2; i++) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("title", "Task " + i);
                taskData.put("description", "Description for Task " + i);
                taskData.put("due_date", "2023-12-31");
                taskData.put("status", "Pending");
                taskData.put("course_id", courseId);

                firestore.collection("tasks").add(taskData).addOnSuccessListener(taskRef -> {
                    Log.d("Firestore", "Task added with ID: " + taskRef.getId());
                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding task: " + e.getMessage());
                });
            }

            // Add sample quiz
            Map<String, Object> quizData = new HashMap<>();
            quizData.put("title", "Sample Quiz");
            quizData.put("description", "This is a sample quiz.");
            quizData.put("course_id", courseId);

            firestore.collection("quizzes").add(quizData).addOnSuccessListener(quizRef -> {
                String quizId = quizRef.getId();
                Log.d("Firestore", "Quiz added with ID: " + quizId);

                // Add 5 sample questions to the quiz
                for (int i = 1; i <= 5; i++) {
                    Map<String, Object> questionData = new HashMap<>();
                    questionData.put("question_text", "Question " + i);
                    questionData.put("options", Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4"));
                    questionData.put("correct_answer", 1); // Index of the correct answer
                    questionData.put("quiz_id", quizId);

                    firestore.collection("quizzes").document(quizId).collection("questions").add(questionData).addOnSuccessListener(questionRef -> {
                        Log.d("Firestore", "Question added with ID: " + questionRef.getId());
                    }).addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding question: " + e.getMessage());
                    });
                }
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error adding quiz: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error adding course: " + e.getMessage());
        });
    }
}
