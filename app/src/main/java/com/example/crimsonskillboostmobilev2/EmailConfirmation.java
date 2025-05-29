package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailConfirmation extends AppCompatActivity {

    private ImageView backBtn;
    private Button checkVerificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_confirmation);

        backBtn = findViewById(R.id.backbtn3);
        checkVerificationBtn = findViewById(R.id.confirmbtn); // re-use the same button

        backBtn.setOnClickListener(v -> finish());

        checkVerificationBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show();
                        // Go to next screen
                        Intent intent = new Intent(this, CreateAccountPath.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
