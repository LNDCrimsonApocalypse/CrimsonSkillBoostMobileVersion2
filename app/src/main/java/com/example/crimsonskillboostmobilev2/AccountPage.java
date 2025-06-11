package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountPage extends AppCompatActivity {

    // Personal Info
    private TextView tvName, tvUsername, tvEmail, tvYear, tvSection, bioEditText;

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

        // Settings button functionality
        ImageView settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfile.class);
            startActivityForResult(intent, 1); // Start EditProfile with request code 1
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload user data after editing
            loadUserData();
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

        courseTitle1 = findViewById(R.id.textViewCourseTitle1);
        progress1 = findViewById(R.id.textViewProgress1);
        progressBar1 = findViewById(R.id.progressBar1);

        // Initialize all ImageButtons
        btnQuiz = findViewById(R.id.btnQuiz);
        btnTask = findViewById(R.id.btnTask);
        btnCode = findViewById(R.id.btnCode);
        btnHome = findViewById(R.id.btnHome);
        btnFlow = findViewById(R.id.btnFlow);

        // Verify initialization
        if (btnQuiz == null || btnTask == null || btnCode == null || btnHome == null || btnFlow == null) {
            throw new NullPointerException("One or more ImageButtons are not properly initialized. Check layout IDs.");
        }
    }

    private void setupNavigation() {
        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(AccountPage.this, QuizListActivity.class);
            startActivity(intent);
        });

        btnTask.setOnClickListener(v -> {
            Intent intent = new Intent(AccountPage.this, TaskPath1Activity.class);
            startActivity(intent);
        });

        btnCode.setOnClickListener(v -> {
            Intent intent = new Intent(AccountPage.this, CodingPathActivity.class);
            startActivity(intent);
        });
        // Bottom navigation buttons
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(AccountPage.this, Home.class);
            startActivity(intent);
        });

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
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fix in AccountPage.java
    private void populateFields(DocumentSnapshot document) {
        tvName.setText(document.getString("fullName"));
        tvUsername.setText(document.getString("username") != null ? document.getString("username") : "Not set");
        tvEmail.setText(document.getString("email") != null ? document.getString("email") : "Not set");

        // Ensure year, section, and bio are retrieved correctly
        tvYear.setText(document.getString("year") != null ? document.getString("year") : "Not set");
        tvSection.setText(document.getString("section") != null ? document.getString("section") : "Not set");
        bioEditText.setText(document.getString("bio") != null ? document.getString("bio") : "No bio available");
    }
}