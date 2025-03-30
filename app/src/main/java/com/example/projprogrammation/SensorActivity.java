package com.example.projprogrammation;

import android.os.Bundle;

public class SensorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chargez le contenu de activity_sensor dans le conteneur de BaseActivity
        getLayoutInflater().inflate(R.layout.activity_sensor, findViewById(R.id.container));
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_sensor;
    }
}
