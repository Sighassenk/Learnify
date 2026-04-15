package com.example.myapplication.ui.instructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.example.myapplication.R;
import com.example.myapplication.models.Course;
import com.example.myapplication.repositories.CourseRepository;
import com.example.myapplication.utils.FirebaseUtils;
import com.example.myapplication.utils.SessionManager;

import java.util.UUID;

public class CreateCourseActivity extends AppCompatActivity {

    private EditText    etTitle, etDescription, etPrice, etCategory;
    private Button      btnCreate, btnPickThumbnail;
    private ImageView   ivPreview;
    private ProgressBar progressBar;

    private CourseRepository courseRepository;
    private SessionManager   sessionManager;
    private Uri              thumbnailUri;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Create Course");

        courseRepository = new CourseRepository();
        sessionManager   = new SessionManager(this);

        etTitle          = findViewById(R.id.etTitle);
        etDescription    = findViewById(R.id.etDescription);
        etPrice          = findViewById(R.id.etPrice);
        etCategory       = findViewById(R.id.etCategory);
        btnCreate        = findViewById(R.id.btnCreate);
        btnPickThumbnail = findViewById(R.id.btnPickThumbnail);
        ivPreview        = findViewById(R.id.ivPreview);
        progressBar      = findViewById(R.id.progressBar);

        btnPickThumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnCreate.setOnClickListener(v -> createCourse());
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_IMAGE && res == RESULT_OK && data != null) {
            thumbnailUri = data.getData();
            Glide.with(this).load(thumbnailUri).into(ivPreview);
        }
    }

    private void createCourse() {
        String title       = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr    = etPrice.getText().toString().trim();
        String category    = etCategory.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Title, description and category are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);

        progressBar.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        if (thumbnailUri != null) {
            uploadThumbnail(title, description, price, category);
        } else {
            saveCourse(title, description, price, category, null);
        }
    }

    private void uploadThumbnail(String title, String desc, double price, String category) {
        StorageReference ref = FirebaseUtils.getStorage()
                .getReference("thumbnails/" + UUID.randomUUID());

        ref.putFile(thumbnailUri)
                .addOnSuccessListener(snap ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                saveCourse(title, desc, price, category, uri.toString())))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnCreate.setEnabled(true);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveCourse(String title, String desc, double price,
                            String category, String thumbUrl) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(desc);
        course.setPrice(price);
        course.setCategory(category);
        course.setThumbnailUrl(thumbUrl);
        course.setInstructorId(sessionManager.getUserId());
        course.setInstructorName(sessionManager.getUserName());
        course.setRating(0f);
        course.setTotalLessons(0);
        course.setCreatedAt(System.currentTimeMillis());

        courseRepository.createCourse(course, task -> {
            progressBar.setVisibility(View.GONE);
            btnCreate.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Course created!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
