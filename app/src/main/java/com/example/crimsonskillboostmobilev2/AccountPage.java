package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageButton btnHome, btnFlow, btnProfile;

    // Top bar icons
    private ImageView backBtn, settingsBtn, notificationBtn;

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

        // If needed, configure more than one progress component
        courseTitle2 = courseTitle1;
        progress2 = progress1;
        progressBar2 = progressBar1;

        btnHome = findViewById(R.id.btnHome);
        btnFlow = findViewById(R.id.btnFlow);
        btnProfile = findViewById(R.id.btnProfile);

        backBtn = findViewById(R.id.backbtnA1);
        settingsBtn = findViewById(R.id.settings);
        notificationBtn = findViewById(R.id.notification);
    }

    private void setupNavigation() {
        btnHome.setOnClickListener(v -> {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Home.class));
            finish();
        });

        btnFlow.setOnClickListener(v -> {
            startActivity(new Intent(this, StructuredPathActivity.class));
            finish();
            Toast.makeText(this, "Flow clicked", Toast.LENGTH_SHORT).show();
        });

        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());

        settingsBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        notificationBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
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