package com.example.crimsonskillboostmobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndCondition extends AppCompatActivity {

    private TextView terms;
    private Button iAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_condition);

        // Bind views
        terms = findViewById(R.id.terms);
        iAgree = findViewById(R.id.iagree);

        // Set HTML-style links if you’re using html in your strings.xml
        // Example string: <a href='https://yourdomain.com/terms'>Click here</a>
        // terms.setText(Html.fromHtml(getString(R.string.html_text)));
        // terms.setMovementMethod(LinkMovementMethod.getInstance());

        // Agree button logic
        iAgree.setOnClickListener(v -> {
            // Move to next activity (e.g., SetupProfile)
            Intent intent = new Intent(TermsAndCondition.this, SetupProfile.class);
            startActivity(intent);
            finish(); // optional: prevent going back to this screen
        });
    }
}

