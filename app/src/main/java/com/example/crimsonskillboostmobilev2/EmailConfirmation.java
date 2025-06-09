package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailConfirmation extends AppCompatActivity {

    private ImageView backBtn;
    private Button checkVerificationBtn;
    private EditText codeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_confirmation);

        backBtn = findViewById(R.id.backbtn3);
        checkVerificationBtn = findViewById(R.id.confirmbtn);
        codeInput = findViewById(R.id.code);

        // Retrieve email and username from Intent
        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");

        backBtn.setOnClickListener(v -> finish());

        checkVerificationBtn.setOnClickListener(v -> {
            String enteredCode = codeInput.getText().toString().trim();

            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Please enter the confirmation code.", Toast.LENGTH_SHORT).show();
                return;
            }

            String expectedCode = "123456"; // Example code logic
            if (!enteredCode.equals(expectedCode)) {
                Toast.makeText(this, "Invalid confirmation code.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, CreateAccountPath.class);
                            intent.putExtra("email", email); // Pass email to next activity
                            intent.putExtra("username", username); // Pass username to next activity
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to reload user data.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}