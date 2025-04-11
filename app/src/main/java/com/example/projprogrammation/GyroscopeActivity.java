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

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xData;
    private TextView yData;
    private TextView zData;
    private SwitchCompat gyroscopeSwitch;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private SensorPreferences sensorPreferences;
    private static float xDataValue = 0.0f;
    private static float yDataValue = 0.0f;
    private static float zDataValue = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);

        xData = findViewById(R.id.gyro_x_data);
        yData = findViewById(R.id.gyro_y_data);
        zData = findViewById(R.id.gyro_z_data);
        gyroscopeSwitch = findViewById(R.id.gyroscope_switch);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        // Charger l'état sauvegardé du switch
        gyroscopeSwitch.setChecked(sensorPreferences.isGyroscopeEnabled());

        // Ajouter un écouteur de changement d'état pour le switch
        gyroscopeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sensorPreferences.setGyroscopeEnabled(isChecked);
                updateGyroscopeState(isChecked);
            }
        });

        // Initialiser l'état du gyroscope selon le switch
        updateGyroscopeState(gyroscopeSwitch.isChecked());
    }

    private void updateGyroscopeState(boolean enabled) {
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
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // S'assurer que le switch reflète la préférence actuelle
        gyroscopeSwitch.setChecked(sensorPreferences.isGyroscopeEnabled());
        if (gyroscopeSwitch.isChecked()) {
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
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE && gyroscopeSwitch.isChecked()) {
            xDataValue = event.values[0];
            yDataValue = event.values[1];
            zDataValue = event.values[2];

            xData.setText(String.format("%.2f", xDataValue));
            yData.setText(String.format("%.2f", yDataValue));
            zData.setText(String.format("%.2f", zDataValue));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }

    public static float getXData() {
        return xDataValue;
    }

    public static float getYData() {
        return yDataValue;
    }

    public static float getZData() {
        return zDataValue;
    }
}
