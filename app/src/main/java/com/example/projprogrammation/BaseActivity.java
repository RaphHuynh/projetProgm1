package com.example.projprogrammation;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Configurer la navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                if (!(this instanceof HomeActivity)) {
                    navigateToActivity(HomeActivity.class);
                }
                return true;
            } else if (itemId == R.id.navigation_record) {
                if (!(this instanceof RecordActivity)) {
                    navigateToActivity(RecordActivity.class);
                }
                return true;
            } else if (itemId == R.id.navigation_history) {
                if (!(this instanceof HistoryActivity)) {
                    navigateToActivity(HistoryActivity.class);
                }
                return true;
            } else if (itemId == R.id.navigation_sensor) {
                if (!(this instanceof SensorActivity)) {
                    navigateToActivity(SensorActivity.class);
                }
                return true;
            }
            return false;
        });

        // Mettre en surbrillance l'élément actif
        int activeItemId = getActiveMenuItemId();
        if (activeItemId != 0) {
            bottomNavigationView.setSelectedItemId(activeItemId);
        }
    }

    // Méthode abstraite pour définir l'élément actif
    protected abstract int getActiveMenuItemId();

    // Méthode pour naviguer vers une activité avec gestion correcte du bouton retour
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Désactiver le bouton retour
    @Override
    public void onBackPressed() {
        // Ne rien faire pour désactiver le bouton retour
    }
}
