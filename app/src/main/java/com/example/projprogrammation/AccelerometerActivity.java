package com.example.projprogrammation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccelerometerActivity extends AppCompatActivity {
    private TextView accelerometerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer); // Assurez-vous que ce layout existe

        accelerometerData = findViewById(R.id.accelerometer_data);

        // Récupérer les données passées via l'intent
        if (getIntent() != null) {
            float x = getIntent().getFloatExtra("x", 0.0f);
            float y = getIntent().getFloatExtra("y", 0.0f);
            float z = getIntent().getFloatExtra("z", 0.0f);

            accelerometerData.setText(String.format("X: %.2f, Y: %.2f, Z: %.2f", x, y, z));
        }
    }
}
