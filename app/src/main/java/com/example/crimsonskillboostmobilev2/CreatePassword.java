package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePassword extends AppCompatActivity {

    private EditText passField;
    private Button confirmBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password);

        passField = findViewById(R.id.pass);
        confirmBtn = findViewById(R.id.confirmbtn2);
        backBtn = findViewById(R.id.backbtn4);

        confirmBtn.setOnClickListener(view -> {
            String password = passField.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Password set successfully!", Toast.LENGTH_SHORT).show();
                // Proceed to next step (e.g., open next activity or save password)
            }
        });

        backBtn.setOnClickListener(view -> finish());
    }
}
