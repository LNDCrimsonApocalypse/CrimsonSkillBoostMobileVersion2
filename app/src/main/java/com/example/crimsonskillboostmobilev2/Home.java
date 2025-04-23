package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private Button btnEnrolledCourses, btnAvailableCourses;
    private ImageButton btnHome, btnFlow, btnProfile;
    private EditText searchBar;
    private RecyclerView recyclerViewCourses;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialize UI components
        searchBar = findViewById(R.id.searchBar);
        btnEnrolledCourses = findViewById(R.id.btnEnrolledCourses);
        btnAvailableCourses = findViewById(R.id.btnAvailableCourses);
        btnHome = findViewById(R.id.btnHome);
        btnFlow = findViewById(R.id.btnFlow);
        btnProfile = findViewById(R.id.btnProfile);
        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Default state: show available courses
        showAvailableCourses();

        // Tab switch logic
        btnAvailableCourses.setOnClickListener(v -> showAvailableCourses());
        btnEnrolledCourses.setOnClickListener(v -> showEnrolledCourses());

        // Bottom navigation buttons
        btnHome.setOnClickListener(v -> {
            // Already in Home
        });

        btnFlow.setOnClickListener(v -> {
            // TODO: Navigate to Flow Activity
            // startActivity(new Intent(Home.this, FlowActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            // TODO: Navigate to Profile Activity
            // startActivity(new Intent(Home.this, ProfileActivity.class));
        });
    }

    private void showAvailableCourses() {
        btnAvailableCourses.setBackgroundResource(R.drawable.bg_button_selected);
        btnEnrolledCourses.setBackgroundResource(R.drawable.button_style3);

        recyclerViewCourses.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);

        // Setup RecyclerView with dummy data
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));

        List<CourseModel> dummyCourses = new ArrayList<>();
        dummyCourses.add(new CourseModel("Game Development", "Learn to build games using Unity.", 0, false, R.drawable.gamedev_icon));
        dummyCourses.add(new CourseModel("Pending Course", "Awaiting approval...", 0, true, R.drawable.gamedev_icon));

        LoadCourses adapter = new LoadCourses(this, dummyCourses);
        recyclerViewCourses.setAdapter(adapter);
    }

    private void showEnrolledCourses() {
        btnEnrolledCourses.setBackgroundResource(R.drawable.bg_button_selected);
        btnAvailableCourses.setBackgroundResource(R.drawable.button_style3);

        // Assume no enrolled courses yet
        recyclerViewCourses.setVisibility(View.INVISIBLE);
        emptyStateText.setVisibility(View.VISIBLE);
    }
}
