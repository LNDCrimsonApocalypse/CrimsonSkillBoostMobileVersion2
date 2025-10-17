package com.example.crimsonskillboostmobilev2;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountPath extends AppCompatActivity {

    private EditText fullnameEditText, birthdayEditText;
    private AutoCompleteTextView genderDropdown;
    private Button nextBtn;
    private ImageView backBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_path);

        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");

        // Bind views
        fullnameEditText = findViewById(R.id.fullname);
        birthdayEditText = findViewById(R.id.birthday);
        genderDropdown = findViewById(R.id.genderDropdown);
        nextBtn = findViewById(R.id.nextbtn);
        backBtn = findViewById(R.id.backbtn1);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Gender dropdown
        String[] genderOptions = {"Male", "Female", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderDropdown.setAdapter(adapter);
        genderDropdown.setOnClickListener(v -> genderDropdown.showDropDown());

        // Date picker for birthday
        birthdayEditText.setOnClickListener(v -> showDatePickerDialog());

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Next button
        nextBtn.setOnClickListener(v -> {
            String fullName = fullnameEditText.getText().toString().trim();
            String birthday = birthdayEditText.getText().toString().trim();
            String gender = genderDropdown.getText().toString().trim();

            if (fullName.isEmpty() || birthday.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = currentUser.getUid();

            Map<String, Object> profile = new HashMap<>();
            profile.put("fullName", fullName);
            profile.put("bio", "");
            profile.put("birthday", birthday);
            profile.put("gender", gender);
            profile.put("email", email); // Ensure email is passed correctly
            profile.put("username", username); // Ensure username is passed correctly
            profile.put("role", "student");
            profile.put("updatedAt", com.google.firebase.Timestamp.now());

            firestore.collection("users").document(uid).set(profile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAccountPath.this, SetupProfile.class));
                } else {
                    Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay);
                    birthdayEditText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }
}
