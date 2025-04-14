package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText fullname, editTextPassword;
    Button loginbtn, createbtn;
    TextView forgot;

    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        fullname = findViewById(R.id.fullname);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginbtn = findViewById(R.id.loginbtn);
        createbtn = findViewById(R.id.createbtn);
        forgot = findViewById(R.id.Forgot);

        loginbtn.setOnClickListener(v -> {
            String username = fullname.getText().toString();
            String password = editTextPassword.getText().toString();

            // Sign in with email and password
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                            // You can start a new activity here, like your main app screen
                            // Example:
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Optional: Close the login activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
