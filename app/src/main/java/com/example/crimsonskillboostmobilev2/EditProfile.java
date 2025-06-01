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

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    // Sample dropdown data
    private final String[] years = {"1st Year", "2nd Year", "3rd Year", "4th Year"};
    private final String[] sections = {"Section A", "Section B", "Section C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page2);

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
    }

    private void setupDropdowns() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(yearAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sections);
        sectionDropdown.setAdapter(sectionAdapter);
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
                        bioEditText.setText(document.getString("bio"));
                        yearDropdown.setText(document.getString("year"), false);
                        sectionDropdown.setText(document.getString("section"), false);
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
        updates.put("bio", bioEditText.getText().toString().trim());
        updates.put("year", yearDropdown.getText().toString().trim());
        updates.put("section", sectionDropdown.getText().toString().trim());

        firestore.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
