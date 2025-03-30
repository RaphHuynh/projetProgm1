package com.example.projprogrammation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Charger le fragment d'accueil par défaut
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        // Configurer la navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_record) {
                selectedFragment = new RecordFragment();
            } else if (item.getItemId() == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (item.getItemId() == R.id.navigation_sensor) {
                selectedFragment = new SensorFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // Méthode pour charger un fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Ajouter à la pile pour gérer le bouton retour
        transaction.commit();
    }

    // Méthode pour mettre à jour l'état de la barre de navigation
    public void updateBottomNavigation(int menuItemId) {
        bottomNavigationView.setSelectedItemId(menuItemId);
    }

    @Override
    public void onBackPressed() {
        // Vérifier si la pile de fragments contient plus d'un élément
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack(); // Supprimer le fragment actuel de la pile

            // Mettre à jour la barre de navigation en fonction du fragment visible
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                updateBottomNavigation(R.id.navigation_home);
            } else if (currentFragment instanceof RecordFragment) {
                updateBottomNavigation(R.id.navigation_record);
            } else if (currentFragment instanceof HistoryFragment) {
                updateBottomNavigation(R.id.navigation_history);
            } else if (currentFragment instanceof SensorFragment) {
                updateBottomNavigation(R.id.navigation_sensor);
            }
        } else {
            // Si c'est le dernier fragment, quitter l'application
            super.onBackPressed();
        }
    }
}