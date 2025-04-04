package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

        // Example logic (optional)
        appName.setText("MyAcademicallyGenius");
    }
}
