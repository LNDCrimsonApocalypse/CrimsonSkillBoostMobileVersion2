package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AccountPage extends AppCompatActivity {

    // Personal Info
    private TextView tvName, tvUsername, tvEmail, tvYear, tvSection, bioEditText;
    private ImageView profileImageView;

    // Navigation Buttons
    private ImageButton btnHome, btnQuiz, btnTask, btnFlow, btnCode;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initViews();
        setupNavigation();
        loadUserData();
        loadCourseProgress();

        // Settings button functionality
        ImageView settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(this::showSettingsMenu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadUserData(); // Reload user data after editing
        }
    }

    private void showSettingsMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_edit_profile) {
                startActivity(new Intent(this, EditProfile.class));
                return true;
            } else if (id == R.id.menu_password_change) {
                Toast.makeText(this, "Password Change selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menu_signout) {
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvYear = findViewById(R.id.tvYear);
        tvSection = findViewById(R.id.tvSection);
        bioEditText = findViewById(R.id.bioEditText);
        profileImageView = findViewById(R.id.profileImageView);

        btnQuiz = findViewById(R.id.btnQuiz);
        btnTask = findViewById(R.id.btnTask);
        btnCode = findViewById(R.id.btnCode);
        btnHome = findViewById(R.id.btnHome);
        btnFlow = findViewById(R.id.btnFlow);
    }

    private void setupNavigation() {
        btnQuiz.setOnClickListener(v -> startActivity(new Intent(AccountPage.this, QuizListActivity.class)));
        btnTask.setOnClickListener(v -> startActivity(new Intent(AccountPage.this, TaskPath1Activity.class)));
        btnCode.setOnClickListener(v -> startActivity(new Intent(AccountPage.this, CodingPathActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(AccountPage.this, Home.class)));
        btnFlow.setOnClickListener(v -> {
            startActivity(new Intent(AccountPage.this, StructuredPathActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        populateFields(document);
                    } else {
                        Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCourseProgress() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("courses").get().addOnSuccessListener(courseQuery -> {
            final int[] shownCourses = {0};

            for (DocumentSnapshot courseDoc : courseQuery.getDocuments()) {
                String courseId = courseDoc.getId();
                String courseTitle = courseDoc.getString("title");

                firestore.collection("courses")
                        .document(courseId)
                        .collection("tasks")
                        .get()
                        .addOnSuccessListener(taskQuery -> {
                            int totalTasks = taskQuery.size();

                            firestore.collection("courses")
                                    .document(courseId)
                                    .collection("tasks")
                                    .get()
                                    .addOnSuccessListener(tasksSnapshot -> {
                                        final int[] submittedTasks = {0};
                                        List<DocumentSnapshot> tasks = tasksSnapshot.getDocuments();

                                        for (DocumentSnapshot task : tasks) {
                                            task.getReference()
                                                    .collection("submissions")
                                                    .whereEqualTo("userId", userId)
                                                    .get()
                                                    .addOnSuccessListener(submissionSnap -> {
                                                        if (!submissionSnap.isEmpty()) {
                                                            submittedTasks[0]++;
                                                        }

                                                        if (submittedTasks[0] + 1 >= tasks.size()) {
                                                            firestore.collection("quizzes")
                                                                    .whereEqualTo("course_id", courseId)
                                                                    .get()
                                                                    .addOnSuccessListener(quizQuery -> {
                                                                        int totalQuizzes = quizQuery.size();
                                                                        final int[] submittedQuizzes = {0};

                                                                        List<DocumentSnapshot> quizzes = quizQuery.getDocuments();
                                                                        for (DocumentSnapshot quiz : quizzes) {
                                                                            quiz.getReference()
                                                                                    .collection("submissions")
                                                                                    .whereEqualTo("userId", userId)
                                                                                    .get()
                                                                                    .addOnSuccessListener(quizSubSnap -> {
                                                                                        if (!quizSubSnap.isEmpty()) {
                                                                                            submittedQuizzes[0]++;
                                                                                        }

                                                                                        if (submittedQuizzes[0] + 1 >= quizzes.size()) {
                                                                                            int totalItems = totalTasks + totalQuizzes;
                                                                                            int completedItems = submittedTasks[0] + submittedQuizzes[0];

                                                                                            int progress = totalItems > 0 ? (int) ((completedItems * 100.0f) / totalItems) : 0;

//                                                                                            if (shownCourses[0] == 0) {
//                                                                                                courseTitle1.setText(courseTitle);
//                                                                                                progress1.setText(progress + "%");
//                                                                                                progressBar1.setProgress(progress);
//                                                                                            } else if (shownCourses[0] == 1) {
//                                                                                                courseTitle2.setText(courseTitle);
//                                                                                                progress2.setText(progress + "%");
//                                                                                                progressBar2.setProgress(progress);
//                                                                                            }

                                                                                            shownCourses[0]++;
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        });
            }

            if (courseQuery.isEmpty()) {
                Toast.makeText(this, "No courses found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("CourseProgressError", "Error loading courses: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to load course progress.", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateFields(DocumentSnapshot document) {
        tvName.setText(document.getString("fullName"));
        tvUsername.setText(document.getString("username") != null ? document.getString("username") : "Not set");
        tvEmail.setText(document.getString("email") != null ? document.getString("email") : "Not set");
        tvYear.setText(document.getString("year") != null ? document.getString("year") : "Not set");
        tvSection.setText(document.getString("section") != null ? document.getString("section") : "Not set");
        bioEditText.setText(document.getString("bio") != null ? document.getString("bio") : "No bio available");

        String photoURL = document.getString("photoURL");
        if (photoURL != null && !photoURL.isEmpty()) {
            Glide.with(this)
                    .load(photoURL)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile);
        }
    }
}
