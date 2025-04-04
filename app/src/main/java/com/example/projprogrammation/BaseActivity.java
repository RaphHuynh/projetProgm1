package com.example.projprogrammation;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home && !(this instanceof HomeActivity)) {
                navigateToActivity(HomeActivity.class);
            } else if (itemId == R.id.navigation_record && !(this instanceof RecordActivity)) {
                navigateToActivity(RecordActivity.class);
            } else if (itemId == R.id.navigation_history && !(this instanceof HistoryActivity)) {
                navigateToActivity(HistoryActivity.class);
            } else if (itemId == R.id.navigation_sensor && !(this instanceof SensorActivity)) {
                navigateToActivity(SensorActivity.class);
            }
            return true;
        });

        int activeItemId = getActiveMenuItemId();
        if (activeItemId != 0) {
            bottomNavigationView.setSelectedItemId(activeItemId);
        }
    }

    protected abstract int getActiveMenuItemId();

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Désactiver le bouton retour
    }
}
