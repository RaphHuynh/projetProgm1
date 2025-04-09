package com.example.projprogrammation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.CompoundButton;

public class MagnetometerActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xData;
    private TextView yData;
    private TextView zData;
    private SwitchCompat magnetometerSwitch;
    private SensorManager sensorManager;
    private Sensor magnetometer;
    private SensorPreferences sensorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        xData = findViewById(R.id.mag_x_data);
        yData = findViewById(R.id.mag_y_data);
        zData = findViewById(R.id.mag_z_data);
        magnetometerSwitch = findViewById(R.id.magnetometer_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        // Charger l'état sauvegardé du switch
        magnetometerSwitch.setChecked(sensorPreferences.isMagnetometerEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        magnetometerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setMagnetometerEnabled(isChecked);
                updateMagnetometerState(isChecked);
            }
        });

        // Initialiser l'état du magnétomètre selon le switch
        updateMagnetometerState(magnetometerSwitch.isChecked());
    }

    private void updateMagnetometerState(boolean enabled) {
        if (enabled) {
            registerListener();
        } else {
            unregisterListener();
            // Réinitialiser les valeurs affichées
            xData.setText("--");
            yData.setText("--");
            zData.setText("--");
        }
    }

    private void registerListener() {
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        magnetometerSwitch.setChecked(sensorPreferences.isMagnetometerEnabled());
        if (magnetometerSwitch.isChecked()) {
            registerListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && magnetometerSwitch.isChecked()) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xData.setText(String.format("%.2f", x));
            yData.setText(String.format("%.2f", y));
            zData.setText(String.format("%.2f", z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}
