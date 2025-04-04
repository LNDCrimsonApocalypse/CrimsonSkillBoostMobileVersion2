package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText fullname, editTextPassword;
    Button loginbtn, createbtn;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        fullname = findViewById(R.id.fullname);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginbtn = findViewById(R.id.loginbtn);
        createbtn = findViewById(R.id.createbtn);
        forgot = findViewById(R.id.Forgot);

        loginbtn.setOnClickListener(v -> {
            String username = fullname.getText().toString();
            String password = editTextPassword.getText().toString();
            Toast.makeText(this, "Logging in as " + username, Toast.LENGTH_SHORT).show();
        });

        createbtn.setOnClickListener(v -> {
            Toast.makeText(this, "Redirect to Create Account", Toast.LENGTH_SHORT).show();
        });

        forgot.setOnClickListener(v -> {
            Toast.makeText(this, "Redirect to Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }
}

