package com.example.projprogrammation;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Environment;
import java.io.File;
import java.nio.file.Files;
import org.json.JSONObject;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.container));

    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_home;
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Récupérer les données de durée
        long totalDuration = getTotalDuration(); // Méthode fictive pour récupérer la durée totale
        long elapsedTime = getElapsedTime(); // Méthode fictive pour récupérer le temps écoulé

        ProgressBar bikeTimerProgress = findViewById(R.id.bike_timer_progress);
        TextView bikeTimerText = findViewById(R.id.bike_timer_text);

        // Calculer le pourcentage de progression
        int progress = (int) ((elapsedTime / (float) totalDuration) * 100);
        bikeTimerProgress.setProgress(progress);

        // Mettre à jour le texte du timer
        int hours = (int) (elapsedTime / 3600);
        int minutes = (int) ((elapsedTime % 3600) / 60);
        int seconds = (int) (elapsedTime % 60);

        bikeTimerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private long getTotalDuration() {
        return 3600; // 1h
    }

    private long getElapsedTime() {
        long totalDuration = 0;
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Records");
        if (directory.exists()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        JSONObject meta = new JSONObject(content);
                        totalDuration += meta.optLong("duration", 0);
                    } catch (Exception ignored) {}
                }
            }
        }

        return (int)totalDuration / 1000;
    }
}
