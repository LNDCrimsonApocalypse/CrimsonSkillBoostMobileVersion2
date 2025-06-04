package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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
        loadUserData();  // Fetch user data from Firestore
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

    private void populateFields(DocumentSnapshot document) {
        tvName.setText(document.getString("fullName"));
        tvUsername.setText(document.getString("username"));
        tvEmail.setText(document.getString("email"));

        if (document.contains("year")) {
            tvYear.setText(document.getString("year"));
        }

        if (document.contains("section")) {
            tvSection.setText(document.getString("section"));
        }

        if (document.contains("bio")) {
            bioEditText.setText(document.getString("bio"));
        }
    }
}