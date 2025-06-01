package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

public class StructuredPathActivity extends AppCompatActivity {

    private PhotoView zoomableImage;
    private ImageButton btnHome, btnFlow, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.structured_path); // Make sure this matches your XML filename

        zoomableImage = findViewById(R.id.zoomableImage);
        btnHome = findViewById(R.id.btnHome);
        btnFlow = findViewById(R.id.btnFlow);
        btnProfile = findViewById(R.id.btnProfile);

        // Optional: Set zoomable image manually if needed
        // zoomableImage.setImageResource(R.drawable.structured_path);

        // Navigation handling
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StructuredPathActivity.this, Home.class));
                finish(); // Optional: Close current activity
            }
        });

        btnFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is current page – you may want to disable or highlight it
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StructuredPathActivity.this, AccountPage.class));
                finish();
            }
        });
    }
}
