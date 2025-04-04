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

import java.io.IOException;

public class SetupProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView, cameraIcon;
    private TextView fullname, username, email;
    private AutoCompleteTextView yearDropdown, sectionDropdown;
    private EditText bioEditText;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_profile);

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

        // Set dropdown options (static example)
        String[] years = {"Grade 11", "Grade 12"};
        String[] sections = {"A", "B", "C", "D"};

        yearDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years));
        sectionDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sections));

        // Profile picture selector
        cameraIcon.setOnClickListener(v -> openImagePicker());

        // Save button
        saveBtn.setOnClickListener(v -> saveProfile());
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
            Uri selectedImageUri = data.getData();

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
        String year = yearDropdown.getText().toString();
        String section = sectionDropdown.getText().toString();
        String bio = bioEditText.getText().toString();

        if (year.isEmpty() || section.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate saving
        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();

        // TODO: Save to Firebase or SQLite or SharedPreferences
        // Example:
        // saveToFirebase(year, section, bio, imageUri);

        // Optionally: go to main dashboard
        // startActivity(new Intent(this, DashboardActivity.class));
        // finish();
    }
}

