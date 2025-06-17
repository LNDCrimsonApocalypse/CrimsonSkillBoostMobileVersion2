package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class TopicsPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton navButton;
    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_page);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.navButton);
        headerTitle = findViewById(R.id.headerTitle);

        // Set up navigation button click listener
        navButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.navigation_view))) {
                drawerLayout.closeDrawer(findViewById(R.id.navigation_view));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.navigation_view));
            }
        });

        // Set header title dynamically (optional)
        headerTitle.setText(getString(R.string.topic_name));
    }
}