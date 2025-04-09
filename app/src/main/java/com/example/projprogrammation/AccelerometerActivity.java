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

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xData;
    private TextView yData;
    private TextView zData;
    private SwitchCompat accelerometerSwitch;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorPreferences sensorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        xData = findViewById(R.id.x_data);
        yData = findViewById(R.id.y_data);
        zData = findViewById(R.id.z_data);
        accelerometerSwitch = findViewById(R.id.accelerometer_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Charger l'état sauvegardé du switch
        accelerometerSwitch.setChecked(sensorPreferences.isAccelerometerEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        accelerometerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setAccelerometerEnabled(isChecked);
                updateAccelerometerState(isChecked);
            }
        });

        // Initialiser l'état de l'accéléromètre selon le switch
        updateAccelerometerState(accelerometerSwitch.isChecked());
    }

    private void updateAccelerometerState(boolean enabled) {
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
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        accelerometerSwitch.setChecked(sensorPreferences.isAccelerometerEnabled());
        if (accelerometerSwitch.isChecked()) {
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && accelerometerSwitch.isChecked()) {
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
