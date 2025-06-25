package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class SubjectDetailsAvailableCourse extends AppCompatActivity {

    private TextView tvCourseTitle, tvInstructorName, tvInstructorEmail, tvCourseOverview, tvTopicOverview, tvRequirements, tvCreatedAt, tvUserId;
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
            finish();
            return;
        }

        // Check enrollment status
        checkEnrollmentStatus(courseId);

        // Back button behavior
        ivBack.setOnClickListener(v -> finish());

        // Enroll button click
        btnEnroll.setOnClickListener(v -> enrollInCourse(courseId));

        // OK button in popup
        btnDialogOK.setOnClickListener(v -> {
            popupContainer.setVisibility(View.GONE);
            scrollView.setAlpha(1f);
        });

        // Load course details
        loadCourseDetails(courseId);
    }

    private void checkEnrollmentStatus(String courseId) {
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("enrollment_requests")
                .whereEqualTo("student_id", studentId)
                .whereEqualTo("course_id", courseId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Enrollment exists, disable the button
                        btnEnroll.setText("Pending Request");
                        btnEnroll.setEnabled(false);
                        btnEnroll.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EnrollmentCheckError", "Error checking enrollment status: " + e.getMessage(), e);
                });
    }

    private void enrollInCourse(String courseId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String studentId = auth.getCurrentUser().getUid();

        if (courseId == null || courseId.isEmpty()) {
            Log.e("EnrollError", "Invalid course ID");
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnroll.setEnabled(false);
        Log.d("EnrollDebug", "Enrollment process started for courseId: " + courseId + ", studentId: " + studentId);

        // Retrieve logged-in user's fullName from Firestore
        firestore.collection("users").document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("fullName");
                        if (studentName == null || studentName.isEmpty()) {
                            studentName = "Unknown Student"; // Fallback value
                        }

                        // Create enrollment data
                        Map<String, Object> enrollmentData = new HashMap<>();
                        enrollmentData.put("student_id", studentId);
                        enrollmentData.put("student_name", studentName);
                        enrollmentData.put("course_id", courseId);
                        enrollmentData.put("status", "pending");
                        enrollmentData.put("created_at", System.currentTimeMillis());

                        // Add enrollment request to Firestore
                        firestore.collection("enrollment_requests")
                                .add(enrollmentData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("EnrollDebug", "Enrollment request successful for courseId: " + courseId);
                                    btnEnroll.setText("Pending Request");
                                    btnEnroll.setEnabled(false);
                                    btnEnroll.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                    Toast.makeText(SubjectDetailsAvailableCourse.this, "Enrollment request sent successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EnrollError", "Error during enrollment: " + e.getMessage(), e);
                                    btnEnroll.setEnabled(true);
                                    Toast.makeText(SubjectDetailsAvailableCourse.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("EnrollError", "Student data not found");
                        Toast.makeText(this, "Failed to retrieve student data.", Toast.LENGTH_SHORT).show();
                        btnEnroll.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EnrollError", "Error retrieving student data: " + e.getMessage(), e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnEnroll.setEnabled(true);
                });
    }

    private void loadCourseDetails(String courseId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("courses").document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvCourseTitle.setText(documentSnapshot.getString("course_name"));
                        tvInstructorName.setText(documentSnapshot.getString("instructor_name"));
                        tvInstructorEmail.setText(documentSnapshot.getString("user_id")); // Assuming user_id is the instructor's email
                        tvCourseOverview.setText(documentSnapshot.getString("overview"));
                        tvRequirements.setText(documentSnapshot.getString("requirements"));

                        String year = documentSnapshot.getString("year");
                        String yearText = YearUtils.getYearText(year);
                        tvTopicOverview.setText("Year: " + yearText +
                                ", Section: " + documentSnapshot.getString("section") +
                                ", Semester: " + documentSnapshot.getString("semester"));
                    } else {
                        Toast.makeText(this, "Course details not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load course details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}