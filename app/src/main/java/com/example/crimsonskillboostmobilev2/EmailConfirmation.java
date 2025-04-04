package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailConfirmation extends AppCompatActivity {

    private ImageView backBtn;
    private EditText codeEditText;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_confirmation);

        // Bind views
        backBtn = findViewById(R.id.backbtn3);
        codeEditText = findViewById(R.id.code);
        confirmBtn = findViewById(R.id.confirmbtn);

        // Back button to finish activity
        backBtn.setOnClickListener(v -> finish());

        // Confirm button
        confirmBtn.setOnClickListener(v -> {
            String enteredCode = codeEditText.getText().toString().trim();

            if (enteredCode.isEmpty()) {
                Toast.makeText(EmailConfirmation.this, "Please enter the confirmation code.", Toast.LENGTH_SHORT).show();
            } else if (enteredCode.length() != 6) { // assuming code is 6 digits
                Toast.makeText(EmailConfirmation.this, "Invalid code. Please check and try again.", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate success — replace with real backend verification if needed
                Toast.makeText(EmailConfirmation.this, "Code confirmed!", Toast.LENGTH_SHORT).show();
                // TODO: Proceed to the next activity (e.g., password creation)
                // Intent intent = new Intent(EmailConfirmation.this, CreatePassword.class);
                // startActivity(intent);
            }
        });
    }
}

