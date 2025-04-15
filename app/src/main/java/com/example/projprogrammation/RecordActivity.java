package com.example.projprogrammation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class RecordActivity extends BaseActivity {

    private Button startChronoButton;
    private TextView chronoTextView;
    private CardView chronoCardView;
    private Handler handler = new Handler();
    private long startTime = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_record, findViewById(R.id.container));

        startChronoButton = findViewById(R.id.startChronoButton);
        chronoTextView = findViewById(R.id.chronoTextView);
        chronoCardView = findViewById(R.id.chronoCardView);

        // Récupérer l'état du chronomètre
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        startTime = prefs.getLong("startTime", 0);
        isRunning = prefs.getBoolean("isRunning", false);

        if (isRunning) {
            updateChrono();
        }

        startChronoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    isRunning = true;
                    startChronoButton.setText("Stop");
                    updateChrono();
                } else {
                    isRunning = false;
                    handler.removeCallbacksAndMessages(null);
                    startChronoButton.setText("Lancer l'enregistrement");
                }
            }
        });
    }

    private void updateChrono() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedMillis = System.currentTimeMillis() - startTime;
                    int seconds = (int) (elapsedMillis / 1000) % 60;
                    int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
                    int hours = (int) (elapsedMillis / (1000 * 60 * 60));
                    String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    chronoTextView.setText(time);
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Sauvegarder l'état du chronomètre
        SharedPreferences prefs = getSharedPreferences("ChronoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTime", startTime);
        editor.putBoolean("isRunning", isRunning);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_record;
    }
}
