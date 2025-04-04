package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
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
            String emailText = email.getText().toString();
            String usernameText = username.getText().toString();
            Toast.makeText(this, "Next clicked\nEmail: " + emailText + "\nUsername: " + usernameText, Toast.LENGTH_SHORT).show();
        });

        backBtn.setOnClickListener(v -> finish());
    }
}

