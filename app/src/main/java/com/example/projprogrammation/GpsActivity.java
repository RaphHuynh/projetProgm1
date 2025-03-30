package com.example.projprogrammation;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GpsActivity extends AppCompatActivity {
    private TextView gpsData;
    private TextView gpsTitle;
    private ImageView gpsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        // Initialisation des vues
        gpsData = findViewById(R.id.gps_data);
        gpsTitle = findViewById(R.id.gps_title);
        gpsIcon = findViewById(R.id.gps_icon);

        // Définir le titre et l'icône
        gpsTitle.setText("GPS");
        gpsIcon.setImageResource(R.drawable.ic_gps);

        // Récupérer les données GPS passées via l'intent
        if (getIntent() != null) {
            double latitude = getIntent().getDoubleExtra("latitude", 0.0);
            double longitude = getIntent().getDoubleExtra("longitude", 0.0);

            gpsData.setText(String.format("Latitude: %.6f, Longitude: %.6f", latitude, longitude));
        }
    }
}
