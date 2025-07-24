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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    private EditText tvName, tvUsername, tvEmail, bioEditText;
    private AutoCompleteTextView yearDropdown, sectionDropdown;
    private Button saveBtn;
    private ImageView backBtn, profileImageView, cameraIcon;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private Uri selectedImageUri;

    private final String[] years = {"First Year", "Second Year", "Third Year", "Fourth Year"};
    private final String[] sections = {"ACSAD", "BCSAD", "CCSAD", "DCSAD"};

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                    profileImageView.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page2); // your edit profile layout

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        // ✅ follow your storage rules: profile_pics/<uid>/<filename>
        storageReference = FirebaseStorage.getInstance().getReference("profile_pics");

        initViews();
        setupDropdowns();
        loadUserData();

        cameraIcon.setOnClickListener(v -> openImagePicker());
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

        profileImageView = findViewById(R.id.profileImageView);
        cameraIcon = findViewById(R.id.cameraIcon);

        if (bioEditText == null || yearDropdown == null || sectionDropdown == null || saveBtn == null ||
                backBtn == null || tvName == null || tvUsername == null || tvEmail == null ||
                profileImageView == null || cameraIcon == null) {
            throw new NullPointerException("One or more views are not properly initialized. Check layout IDs.");
        }
    }

    private void setupDropdowns() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(yearAdapter);
        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, sections);
        sectionDropdown.setAdapter(sectionAdapter);
        sectionDropdown.setOnClickListener(v -> sectionDropdown.showDropDown());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvName.setText(document.getString("fullName") != null ? document.getString("fullName") : "");
                        tvUsername.setText(document.getString("username") != null ? document.getString("username") : "");
                        tvEmail.setText(document.getString("email") != null ? document.getString("email") : "");
                        bioEditText.setText(document.getString("bio") != null ? document.getString("bio") : "");
                        yearDropdown.setText(document.getString("year") != null ? document.getString("year") : "", false);
                        sectionDropdown.setText(document.getString("section") != null ? document.getString("section") : "", false);

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
                    } else {
                        Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfileChanges() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = tvName.getText().toString().trim();
        String username = tvUsername.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();
        String year = yearDropdown.getText().toString().trim();
        String section = sectionDropdown.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || year.isEmpty() || section.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            // ✅ store under profile_pics/<uid>/<random filename>
            String filename = UUID.randomUUID().toString() + ".jpg";
            StorageReference fileRef = storageReference.child(user.getUid()).child(filename);

            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                updateFirestore(user.getUid(), fullName, username, bio, year, section, uri.toString());
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // just update without photo
            updateFirestore(user.getUid(), fullName, username, bio, year, section, null);
        }
    }

    private void updateFirestore(String uid, String fullName, String username, String bio,
                                 String year, String section, @Nullable String photoURL) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("username", username);
        updates.put("bio", bio);
        updates.put("year", year);
        updates.put("section", section);
        if (photoURL != null) {
            updates.put("photoURL", photoURL);
        }

        firestore.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
