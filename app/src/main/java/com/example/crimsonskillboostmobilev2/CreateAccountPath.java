package com.example.crimsonskillboostmobilev2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateAccountPath extends AppCompatActivity {

    private EditText fullnameEditText, birthdayEditText;
    private AutoCompleteTextView genderDropdown;
    private Button nextBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_path);

        // Bind views
        fullnameEditText = findViewById(R.id.fullname);
        birthdayEditText = findViewById(R.id.birthday);
        genderDropdown = findViewById(R.id.genderDropdown);
        nextBtn = findViewById(R.id.nextbtn);
        backBtn = findViewById(R.id.backbtn1);

        // Gender dropdown
        String[] genderOptions = {"Male", "Female", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderDropdown.setAdapter(adapter);

        // Date picker for birthday
        birthdayEditText.setOnClickListener(v -> showDatePickerDialog());

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Next button functionality
        nextBtn.setOnClickListener(v -> {
            String fullName = fullnameEditText.getText().toString().trim();
            String birthday = birthdayEditText.getText().toString().trim();
            String gender = genderDropdown.getText().toString().trim();

            if (fullName.isEmpty() || birthday.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account Info Accepted!", Toast.LENGTH_SHORT).show();
                // TODO: Proceed to next screen or save the data
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    birthdayEditText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }
}

