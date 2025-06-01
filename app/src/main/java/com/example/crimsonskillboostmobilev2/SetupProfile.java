package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetupProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView, cameraIcon;
    private TextView fullname, username, email;
    private AutoCompleteTextView yearDropdown, sectionDropdown;
    private EditText bioEditText;
    private Button saveBtn;

    private Uri selectedImageUri;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_profile);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // View Binding
        profileImageView = findViewById(R.id.profileImageView);
        cameraIcon = findViewById(R.id.cameraIcon);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        yearDropdown = findViewById(R.id.yeardropdown);
        sectionDropdown = findViewById(R.id.sectionDropdown);
        bioEditText = findViewById(R.id.bioEditText);
        saveBtn = findViewById(R.id.savebtn);

        // Dropdown options
        String[] years = {"First Year", "Second Year", "Third Year", "Fourth Year"};
        String[] sections = {"ACSAD", "BCSAD", "CCSAD", "DCSAD"};

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sections);

        yearDropdown.setAdapter(yearAdapter);
        sectionDropdown.setAdapter(sectionAdapter);

        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());
        sectionDropdown.setOnClickListener(v -> sectionDropdown.showDropDown());

        // Profile picture selector
        cameraIcon.setOnClickListener(v -> openImagePicker());

        // Save button
        saveBtn.setOnClickListener(v -> {
            String year = yearDropdown.getText().toString().trim();
            String section = sectionDropdown.getText().toString().trim();
            String bio = bioEditText.getText().toString().trim();

            if (year.isEmpty() || section.isEmpty() || bio.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("year", year);
            updates.put("section", section);
            updates.put("bio", bio);

            db.collection("users").document(currentUser.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SetupProfile.this, Home.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Load Firebase user data
        loadUserInfo();
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            fullname.setText(documentSnapshot.getString("fullName"));
                            username.setText(documentSnapshot.getString("username"));
                            email.setText(documentSnapshot.getString("email"));
                        } else {
                            Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No authenticated user.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveProfile() {
        String year = yearDropdown.getText().toString().trim();
        String section = sectionDropdown.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();

        if (year.isEmpty() || section.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to save
        Map<String, Object> updates = new HashMap<>();
        updates.put("year", year);
        updates.put("section", section);
        updates.put("bio", bio);

        db.collection("users").document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupProfile.this, Home.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
