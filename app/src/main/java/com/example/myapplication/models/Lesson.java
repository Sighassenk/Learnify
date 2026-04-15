package com.example.myapplication.models;

public class Lesson {
    private String id;
    private String title;
    private String videoUrl;
    private int duration;
    private String sectionId;
    private String courseId;
    private int order;

    public Lesson() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}