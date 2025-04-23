package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText fullname, editTextPassword;
    Button loginbtn, createbtn;
    TextView forgot;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, Home.class));
            finish(); // Close login activity
            return; // Exit onCreate
        }

        // If not logged in, continue to show login screen
        setContentView(R.layout.login_interface);

        fullname = findViewById(R.id.fullname);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginbtn = findViewById(R.id.loginbtn);
        createbtn = findViewById(R.id.createbtn);
        forgot = findViewById(R.id.Forgot);

        loginbtn.setOnClickListener(v -> {
            String username = fullname.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, Home.class);
                            startActivity(intent);
                            finish(); // Close login activity
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        createbtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
            startActivity(intent);
        });

        forgot.setOnClickListener(v -> {
            Toast.makeText(this, "Redirect to Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }
}
