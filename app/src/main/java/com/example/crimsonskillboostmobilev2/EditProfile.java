package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText bioEditText;
    private AutoCompleteTextView yearDropdown, sectionDropdown;
    private Button saveBtn;
    private ImageView backBtn;

    private EditText tvName, tvUsername, tvEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private final String[] years = {"First Year", "Second Year", "Third Year", "Fourth Year"};
    private final String[] sections = {"ACSAD", "BCSAD", "CCSAD", "DCSAD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page2); // Ensure this layout file exists

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initViews();
        setupDropdowns();
        loadUserData();

        saveBtn.setOnClickListener(v -> saveProfileChanges());
        backBtn.setOnClickListener(v -> finish());
    }

    private void initViews() {
        bioEditText = findViewById(R.id.bioEditText);
        yearDropdown = findViewById(R.id.yeardropdown);
        sectionDropdown = findViewById(R.id.sectionDropdown);
        saveBtn = findViewById(R.id.savebtn);
        backBtn = findViewById(R.id.backbtnA1);

        tvName = findViewById(R.id.fullname);
        tvUsername = findViewById(R.id.username);
        tvEmail = findViewById(R.id.email);

        // Check for null views
        if (bioEditText == null || yearDropdown == null || sectionDropdown == null || saveBtn == null || backBtn == null ||
                tvName == null || tvUsername == null || tvEmail == null) {
            throw new NullPointerException("One or more views are not properly initialized. Check layout IDs.");
        }
    }

    private void setupDropdowns() {
        // Adapter for Year Dropdown
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(yearAdapter);
        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown()); // Show dropdown on click

        // Adapter for Section Dropdown
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sections);
        sectionDropdown.setAdapter(sectionAdapter);
        sectionDropdown.setOnClickListener(v -> sectionDropdown.showDropDown()); // Show dropdown on click
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
                        // Safely retrieve and set each field
                        tvName.setText(document.getString("fullName") != null ? document.getString("fullName") : "");
                        tvUsername.setText(document.getString("username") != null ? document.getString("username") : "");
                        tvEmail.setText(document.getString("email") != null ? document.getString("email") : "");
                        bioEditText.setText(document.getString("bio") != null ? document.getString("bio") : "");
                        yearDropdown.setText(document.getString("year") != null ? document.getString("year") : "", false);
                        sectionDropdown.setText(document.getString("section") != null ? document.getString("section") : "", false);
                    } else {
                        Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileChanges() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", tvName.getText().toString().trim());
        updates.put("username", tvUsername.getText().toString().trim());
        updates.put("bio", bioEditText.getText().toString().trim());
        updates.put("year", yearDropdown.getText().toString().trim());
        updates.put("section", sectionDropdown.getText().toString().trim());

        firestore.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set result to OK
                    finish(); // Close EditProfile
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}