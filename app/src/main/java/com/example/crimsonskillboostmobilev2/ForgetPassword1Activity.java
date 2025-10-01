package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword1Activity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendCodeButton;
    private FirebaseAuth mAuth;

    // Pop-up UI
    private View popupOverlay;
    private TextView tvBack, tvTryAgain, tvDialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password1); // XML layout filename

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI elements
        emailInput = findViewById(R.id.emailInput); // ✅ make sure your XML uses this ID
        sendCodeButton = findViewById(R.id.sendcode);
        popupOverlay = findViewById(R.id.popupOverlay);
        tvBack = findViewById(R.id.tvBack);
        tvTryAgain = findViewById(R.id.tvTryAgain);
        tvDialogMessage = findViewById(R.id.tvDialogMessage);
        ImageView backBtn = findViewById(R.id.new_backbtn4);

        sendCodeButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Please enter your email.");
                return;
            }

            // Send password reset email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showPopup(true, null);
                        } else {
                            Exception e = task.getException();
                            showPopup(false, e != null ? e.getMessage() : null);
                        }
                    });
        });

        tvBack.setOnClickListener(v -> {
            popupOverlay.setVisibility(View.GONE);
            finish(); // Go back to login or previous screen
        });

        tvTryAgain.setOnClickListener(v -> {
            popupOverlay.setVisibility(View.GONE);
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void showPopup(boolean success, String errorMessage) {
        popupOverlay.setVisibility(View.VISIBLE);
        if (success) {
            tvDialogMessage.setText("Password reset link sent to your email. Please check your inbox.");
        } else {
            tvDialogMessage.setText("Failed to send reset email. " +
                    (errorMessage != null ? errorMessage : "Please check your email address and try again."));
        }
    }
}
