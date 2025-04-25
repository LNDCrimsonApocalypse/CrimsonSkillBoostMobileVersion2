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

public class CreatePassword extends AppCompatActivity {

    private EditText passField;
    private Button confirmBtn;
    private ImageView backBtn;

    private FirebaseAuth mAuth;
    private String email, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password);

        passField = findViewById(R.id.pass);
        confirmBtn = findViewById(R.id.confirmbtn2);
        backBtn = findViewById(R.id.backbtn4);

        mAuth = FirebaseAuth.getInstance();

        // Get email and username from intent
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        confirmBtn.setOnClickListener(view -> {
            String password = passField.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            } else {
                // Create user in Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                // You can save the username to Firebase database if you want (optional)
                                // e.g., FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("username").setValue(username);

                                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                                // Redirect to home or login
                                startActivity(new Intent(this, Home.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to create account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        backBtn.setOnClickListener(view -> finish());
    }
}
