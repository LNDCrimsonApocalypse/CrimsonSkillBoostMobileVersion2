// CreatePassword.java
package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CreatePassword extends AppCompatActivity {

    private EditText passField;
    private Button confirmBtn;
    private ImageView backBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password);

        passField = findViewById(R.id.pass);
        confirmBtn = findViewById(R.id.confirmbtn2);
        backBtn = findViewById(R.id.backbtn4);

        mAuth = FirebaseAuth.getInstance();

        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");

        confirmBtn.setOnClickListener(view -> {
            String password = passField.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CreatePassword.this, CreateAccountPath.class);
                                intent.putExtra("email", email);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        backBtn.setOnClickListener(view -> finish());
    }
}
