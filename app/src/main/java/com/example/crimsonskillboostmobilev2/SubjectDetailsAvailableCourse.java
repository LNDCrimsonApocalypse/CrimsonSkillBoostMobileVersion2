package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectDetailsAvailableCourse extends AppCompatActivity {

    private TextView tvCourseTitle, tvInstructorName, tvInstructorEmail, tvCourseOverview, tvTopicOverview, tvRequirements;
    private Button btnEnroll, btnDialogOK;
    private ConstraintLayout popupContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details_available_course);

        // View bindings
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvInstructorName = findViewById(R.id.tvInstructorName);
        tvInstructorEmail = findViewById(R.id.tvInstructorEmail);
        tvCourseOverview = findViewById(R.id.tvCourseOverview);
        tvTopicOverview = findViewById(R.id.tvTopicOverview);
        tvRequirements = findViewById(R.id.tvRequirements);
        btnEnroll = findViewById(R.id.btnEnroll);
        btnDialogOK = findViewById(R.id.btnDialogOK);
        popupContainer = findViewById(R.id.popupContainer);
        scrollView = findViewById(R.id.scrollView2);
        ImageView ivBack = findViewById(R.id.ivBack);

        // Retrieve courseId from intent
        String courseId = getIntent().getStringExtra("course_id");

        // Validate courseId
        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course ID received", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if courseId is invalid
            return;
        }

        // Back button behavior
        ivBack.setOnClickListener(v -> finish());

        // Enroll button click
        btnEnroll.setOnClickListener(v -> enrollInCourse(courseId));

        // OK button in popup
        btnDialogOK.setOnClickListener(v -> {
            popupContainer.setVisibility(View.GONE);
            scrollView.setAlpha(1f); // Reset background dim
        });
    }

    private void enrollInCourse(String courseId) {
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (courseId == null || courseId.isEmpty()) {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnroll.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.enrollInCourse(studentId, courseId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    btnEnroll.setText("Pending Request");
                    btnEnroll.setEnabled(false);
                    btnEnroll.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    Toast.makeText(SubjectDetailsAvailableCourse.this, "Enrollment request sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    btnEnroll.setEnabled(true);
                    Toast.makeText(SubjectDetailsAvailableCourse.this, "Failed to enroll: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnEnroll.setEnabled(true);
                Toast.makeText(SubjectDetailsAvailableCourse.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignStudentId(String documentId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String studentId = mAuth.getCurrentUser().getUid(); // Get Firebase user ID

        // Update the student_id field in Firestore
        firestore.collection("students").document(documentId)
                .update("student_id", studentId)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Student ID assigned successfully!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error assigning Student ID: " + e.getMessage());
                });
    }
}