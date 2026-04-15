package com.example.myapplication.models;

import java.util.List;
import java.util.ArrayList;

public class Enrollment {
    private String id;
    private String userId;
    private String courseId;
    private float progress;
    private long enrolledAt;
    private List<String> completedLessons;

    public Enrollment() {
        this.completedLessons = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }

    public long getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(long enrolledAt) { this.enrolledAt = enrolledAt; }

    public List<String> getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(List<String> completedLessons) {
        this.completedLessons = completedLessons;
    }
}