// CreateAccount.java
package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    EditText email, username;
    Button nextBtn;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account2);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        nextBtn = findViewById(R.id.nextbtn2);
        backBtn = findViewById(R.id.backbtn2);

        nextBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String fullNameText = "Default Name"; // Replace with actual full name input if available

            if (emailText.isEmpty() || usernameText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CreateAccount.this, CreatePassword.class);
            intent.putExtra("email", emailText);
            intent.putExtra("username", usernameText);
            intent.putExtra("fullName", fullNameText); // Pass fullName to next activity
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
