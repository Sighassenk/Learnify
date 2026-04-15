package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    private List<Course> courses;
    private List<Course> allCourses;
    private final OnCourseClickListener listener;

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses    = new ArrayList<>(courses);
        this.allCourses = new ArrayList<>(courses);
        this.listener   = listener;
    }

    @NonNull @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.tvTitle.setText(course.getTitle());
        holder.tvInstructor.setText(course.getInstructorName());
        holder.tvCategory.setText(course.getCategory());
        holder.tvRating.setText(String.format("%.1f ★", course.getRating()));
        holder.tvPrice.setText(course.getPrice() == 0
                ? "Free" : String.format("$%.2f", course.getPrice()));

        if (course.getThumbnailUrl() != null && !course.getThumbnailUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(course.getThumbnailUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> listener.onCourseClick(course));
    }

    @Override
    public int getItemCount() { return courses.size(); }

    public void filter(String query) {
        courses.clear();
        if (query.isEmpty()) {
            courses.addAll(allCourses);
        } else {
            String lower = query.toLowerCase().trim();
            for (Course c : allCourses) {
                if ((c.getTitle() != null && c.getTitle().toLowerCase().contains(lower)) ||
                        (c.getCategory() != null && c.getCategory().toLowerCase().contains(lower))) {
                    courses.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(List<Course> newCourses) {
        allCourses = new ArrayList<>(newCourses);
        courses    = new ArrayList<>(newCourses);
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView  tvTitle, tvInstructor, tvPrice, tvRating, tvCategory;

        CourseViewHolder(View itemView) {
            super(itemView);
            ivThumbnail  = itemView.findViewById(R.id.ivThumbnail);
            tvTitle      = itemView.findViewById(R.id.tvTitle);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvPrice      = itemView.findViewById(R.id.tvPrice);
            tvRating     = itemView.findViewById(R.id.tvRating);
            tvCategory   = itemView.findViewById(R.id.tvCategory);
        }
    }
}
