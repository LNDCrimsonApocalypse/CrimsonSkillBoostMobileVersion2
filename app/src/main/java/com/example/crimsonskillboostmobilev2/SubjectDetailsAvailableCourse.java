package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

        // Optional: get data from intent
        String courseTitle = getIntent().getStringExtra("title");
        String instructorName = getIntent().getStringExtra("instructor_name");
        String instructorEmail = getIntent().getStringExtra("instructor_email");
        String courseOverview = getIntent().getStringExtra("overview");
        String topicOverview = getIntent().getStringExtra("topic");
        String requirements = getIntent().getStringExtra("requirements");

        // Set text fields (fallbacks if null)
        tvCourseTitle.setText(courseTitle != null ? courseTitle : "Course Title");
        tvInstructorName.setText(instructorName != null ? instructorName : "Prof. Name");
        tvInstructorEmail.setText(instructorEmail != null ? instructorEmail : "Email");
        tvCourseOverview.setText(courseOverview != null ? courseOverview : "Course overview here.");
        tvTopicOverview.setText(topicOverview != null ? topicOverview : "Topic overview here.");
        tvRequirements.setText(requirements != null ? requirements : "Requirements listed here.");

        // Back button behavior
        ivBack.setOnClickListener(v -> finish());

        // Enroll button click
        btnEnroll.setOnClickListener(v -> showPopup());

        // OK button in popup
        btnDialogOK.setOnClickListener(v -> {
            popupContainer.setVisibility(View.GONE);
            scrollView.setAlpha(1f); // Reset background dim

            btnEnroll.setText("Pending Request");
            btnEnroll.setEnabled(false);
            btnEnroll.setClickable(false);
            btnEnroll.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        });

        Button btnEnroll = findViewById(R.id.btnEnroll);
        Button btnDialogOK = findViewById(R.id.btnDialogOK); // Make sure this is declared and points to the right ID

        btnDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change text to indicate request is pending
                btnEnroll.setText("Pending Request");

                // Disable the button
                btnEnroll.setEnabled(false);
                btnEnroll.setClickable(false);

                // Optional: Change button color to a disabled-like appearance
                btnEnroll.setBackgroundColor(getResources().getColor(R.color.black)); // Ensure gray exists in colors.xml
            }
        });
    }

    private void showPopup() {
        popupContainer.setVisibility(View.VISIBLE);
        scrollView.setAlpha(0.2f); // Dim background

        // You could trigger an API request here for real enrollment
        // For example: enrollStudentInCourse(studentId, courseId);
    }
}
