package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView logo;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.Logo);
        appName = findViewById(R.id.AppName);

        appName.setText("MyAcademicallyGenius");

        // Transition after 3 seconds (3000 milliseconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the splash screen so user can't go back to it
            }
        }, 3000); // Delay in milliseconds
    }
}
