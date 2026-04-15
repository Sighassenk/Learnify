package com.example.myapplication.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.repositories.AuthRepository;
import com.example.myapplication.ui.instructor.InstructorDashboardActivity;
import com.example.myapplication.ui.student.HomeActivity;
import com.example.myapplication.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText    etName, etEmail, etPassword;
    private RadioGroup  rgRole;
    private Button      btnRegister;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();
        sessionManager = new SessionManager(this);

        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        rgRole      = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> register());
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
    }

    private void register() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        int selectedId  = rgRole.getCheckedRadioButtonId();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = (selectedId == R.id.rbInstructor) ? "instructor" : "student";

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        authRepository.register(name, email, password, role, task -> {
            if (task.isSuccessful()) {
                authRepository.getCurrentUser(user -> {
                    sessionManager.saveSession(user.getId(), user.getRole(), user.getName());
                    progressBar.setVisibility(View.GONE);
                    Intent intent = "instructor".equals(role)
                            ? new Intent(this, InstructorDashboardActivity.class)
                            : new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } else {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Registration failed: " +
                        task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
