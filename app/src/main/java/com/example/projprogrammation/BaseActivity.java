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
        bottomNavigationView.setSelectedItemId(getActiveMenuItemId());
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == getActiveMenuItemId()) {
                return true;
            }
            
            if (itemId == R.id.navigation_home) {
                navigateToActivity(HomeActivity.class);
            } else if (itemId == R.id.navigation_record) {
                navigateToActivity(RecordActivity.class);
            } else if (itemId == R.id.navigation_history) {
                navigateToActivity(HistoryActivity.class);
            } else if (itemId == R.id.navigation_sensor) {
                navigateToActivity(SensorActivity.class);
            }
            return true;
        });
    }

    protected abstract int getActiveMenuItemId();

    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Désactiver le bouton retour
    }
}
