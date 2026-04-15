package com.example.myapplication.ui.student;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.myapplication.R;
import com.example.myapplication.repositories.EnrollmentRepository;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer            player;
    private PlayerView           playerView;
    private EnrollmentRepository enrollmentRepo;

    private String enrollmentId, lessonId, lessonTitle;
    private int    totalLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        enrollmentId = getIntent().getStringExtra("enrollmentId");
        lessonId     = getIntent().getStringExtra("lessonId");
        lessonTitle  = getIntent().getStringExtra("lessonTitle");
        totalLessons = getIntent().getIntExtra("totalLessons", 1);
        String videoUrl = getIntent().getStringExtra("videoUrl");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(lessonTitle);

        enrollmentRepo = new EnrollmentRepository();
        playerView     = findViewById(R.id.playerView);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    markComplete();
                }
            }
        });
    }

    private void markComplete() {
        if (enrollmentId == null || lessonId == null) return;
        enrollmentRepo.completeLesson(enrollmentId, lessonId, totalLessons, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "✓ Lesson completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}