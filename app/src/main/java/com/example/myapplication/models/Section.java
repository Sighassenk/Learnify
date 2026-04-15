package com.example.myapplication.models;

public class Section {
    private String id;
    private String title;
    private String courseId;
    private int order;

    public Section() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}