package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.ui.auth.LoginActivity;
import com.example.myapplication.utils.FirebaseUtils;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.ui.student.HomeActivity;
import com.example.myapplication.ui.instructor.InstructorDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);

        if (FirebaseUtils.isLoggedIn() && sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            Intent intent = "instructor".equals(role)
                    ? new Intent(this, InstructorDashboardActivity.class)
                    : new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
