package com.example.crimsonskillboostmobilev2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CodingPathActivity extends AppCompatActivity {

    private EditText codeEditor;
    private TextView outputView;
    private Button runCodeBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coding_path);

        // Bind UI elements
        codeEditor = findViewById(R.id.codeEditor);
        outputView = findViewById(R.id.outputView);
        runCodeBtn = findViewById(R.id.runCodeBtn);
        backBtn = findViewById(R.id.bckbttn);

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Run code button functionality
        runCodeBtn.setOnClickListener(v -> {
            String code = codeEditor.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter some code to run.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate code execution (replace with actual logic if needed)
            String output = executeCode(code);
            outputView.setText(output);
        });
    }

    private String executeCode(String code) {
        // Placeholder for code execution logic
        // You can integrate a code interpreter or compiler here
        return "Executed code:\n" + code;
    }
}