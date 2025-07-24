package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountPage extends AppCompatActivity {

    // Personal Info
    private TextView tvName, tvUsername, tvEmail, tvYear, tvSection, bioEditText;
    private ImageView profileImageView;

    // Progress Texts
    private TextView courseTitle1, progress1, courseTitle2, progress2;

    // Progress Bars
    private ProgressBar progressBar1, progressBar2;

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
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
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
        profileImageView = findViewById(R.id.profileImageView); // add this in your layout

        courseTitle1 = findViewById(R.id.textViewCourseTitle1);
        progress1 = findViewById(R.id.textViewProgress1);
        progressBar1 = findViewById(R.id.progressBar1);

        courseTitle2 = findViewById(R.id.textViewCourseTitle2);
        progress2 = findViewById(R.id.textViewProgress2);
        progressBar2 = findViewById(R.id.progressBar2);

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
        firestore.collection("course_progress")
                .whereEqualTo("student_id", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String courseTitle = document.getString("course_title");
                            int progress = document.getLong("progress").intValue();

                            if ("Course 1".equals(courseTitle)) {
                                courseTitle1.setText(courseTitle);
                                progress1.setText(progress + "%");
                                progressBar1.setProgress(progress);
                            } else if ("Course 2".equals(courseTitle)) {
                                courseTitle2.setText(courseTitle);
                                progress2.setText(progress + "%");
                                progressBar2.setProgress(progress);
                            }
                        }
                    } else {
                        courseTitle1.setText("");
                        progress1.setText("");
                        progressBar1.setProgress(0);

                        courseTitle2.setText("");
                        progress2.setText("");
                        progressBar2.setProgress(0);

                        Toast.makeText(this, "No progress data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProgressLoadError", "Error loading progress: " + e.getMessage(), e);
                    Toast.makeText(this, "Error loading progress.", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateFields(DocumentSnapshot document) {
        tvName.setText(document.getString("fullName"));
        tvUsername.setText(document.getString("username") != null ? document.getString("username") : "Not set");
        tvEmail.setText(document.getString("email") != null ? document.getString("email") : "Not set");
        tvYear.setText(document.getString("year") != null ? document.getString("year") : "Not set");
        tvSection.setText(document.getString("section") != null ? document.getString("section") : "Not set");
        bioEditText.setText(document.getString("bio") != null ? document.getString("bio") : "No bio available");

        // ✅ Load profile image if available
        String photoURL = document.getString("photoURL");
        if (photoURL != null && !photoURL.isEmpty()) {
            Glide.with(this)
                    .load(photoURL)
                    .placeholder(R.drawable.profile) // add a default icon in drawable
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile);
        }
    }
}
