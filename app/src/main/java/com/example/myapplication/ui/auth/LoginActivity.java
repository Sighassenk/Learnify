package com.example.myapplication.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.repositories.AuthRepository;
import com.example.myapplication.ui.instructor.InstructorDashboardActivity;
import com.example.myapplication.ui.student.HomeActivity;
import com.example.myapplication.utils.FirebaseUtils;
import com.example.myapplication.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText      etEmail, etPassword;
    private Button        btnLogin;
    private TextView      tvRegister;
    private ProgressBar   progressBar;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        sessionManager = new SessionManager(this);

        if (FirebaseUtils.isLoggedIn() && sessionManager.isLoggedIn()) {
            redirect(sessionManager.getUserRole());
            return;
        }

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvRegister  = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        authRepository.login(email, password, task -> {
            if (task.isSuccessful()) {
                authRepository.getCurrentUser(user -> {
                    if (user != null) {
                        sessionManager.saveSession(user.getId(), user.getRole(), user.getName());
                        progressBar.setVisibility(View.GONE);
                        redirect(user.getRole());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        Toast.makeText(this, "User data not found in database", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(this, "Login failed: " +
                        task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirect(String role) {
        Intent intent = "instructor".equals(role)
                ? new Intent(this, InstructorDashboardActivity.class)
                : new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
