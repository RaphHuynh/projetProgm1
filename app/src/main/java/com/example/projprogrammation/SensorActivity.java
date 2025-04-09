package com.example.projprogrammation;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class SensorActivity extends BaseActivity {
    
    private SensorPreferences sensorPreferences;
    
    // Cartes pour chaque capteur
    private CardView cardAccelerometer;
    private CardView cardGyroscope;
    private CardView cardMagnetometer;
    private CardView cardTemperature;
    private CardView cardPressure;
    private CardView cardHumidity;
    private CardView cardGps;
    
    // Icônes pour chaque capteur
    private ImageView accelerometerIcon;
    private ImageView gyroscopeIcon;
    private ImageView magnetometerIcon;
    private ImageView temperatureIcon;
    private ImageView pressureIcon;
    private ImageView humidityIcon;
    private ImageView gpsIcon;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chargez le contenu de activity_sensor dans le conteneur de BaseActivity
        getLayoutInflater().inflate(R.layout.activity_sensor, findViewById(R.id.container));
        
        // Initialiser les préférences
        sensorPreferences = new SensorPreferences(this);
        
        // Initialiser les références aux vues
        initializeViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Mettre à jour les couleurs des cartes selon l'état des capteurs
        updateCardColors();
    }
    
    private void initializeViews() {
        // Initialiser les cartes
        cardAccelerometer = findViewById(R.id.card_accelerometer);
        cardGyroscope = findViewById(R.id.card_gyroscope);
        cardMagnetometer = findViewById(R.id.card_magnetometer);
        cardTemperature = findViewById(R.id.card_temperature);
        cardPressure = findViewById(R.id.card_pressure);
        cardHumidity = findViewById(R.id.card_humidity);
        cardGps = findViewById(R.id.card_gps);
        
        // Initialiser les icônes
        accelerometerIcon = findViewById(R.id.accelerometer_icon);
        gyroscopeIcon = findViewById(R.id.gyroscope_icon);
        magnetometerIcon = findViewById(R.id.magnetometer_icon);
        temperatureIcon = findViewById(R.id.temperature_icon);
        pressureIcon = findViewById(R.id.pressure_icon);
        humidityIcon = findViewById(R.id.humidity_icon);
        gpsIcon = findViewById(R.id.gps_icon);
    }
    
    private void updateCardColors() {
        // Couleurs pour les cartes activées/désactivées
        int activeColor = ContextCompat.getColor(this, R.color.sensor_active);
        int inactiveColor = ContextCompat.getColor(this, android.R.color.white);
        
        // Couleurs pour les icônes et textes
        int activeTextColor = ContextCompat.getColor(this, android.R.color.white);
        int inactiveTextColor = ContextCompat.getColor(this, android.R.color.black);
        
        // Mettre à jour chaque carte selon son état
        updateCardAppearance(cardAccelerometer, accelerometerIcon, 
                sensorPreferences.isAccelerometerEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardGyroscope, gyroscopeIcon, 
                sensorPreferences.isGyroscopeEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardMagnetometer, magnetometerIcon, 
                sensorPreferences.isMagnetometerEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardTemperature, temperatureIcon, 
                sensorPreferences.isTemperatureEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardPressure, pressureIcon, 
                sensorPreferences.isPressureEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardHumidity, humidityIcon, 
                sensorPreferences.isHumidityEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
        
        updateCardAppearance(cardGps, gpsIcon, 
                sensorPreferences.isGpsEnabled(), 
                activeColor, inactiveColor, activeTextColor, inactiveTextColor);
    }
    
    private void updateCardAppearance(CardView card, ImageView icon, boolean isEnabled, 
                                     int activeCardColor, int inactiveCardColor,
                                     int activeIconColor, int inactiveIconColor) {
        if (card != null && icon != null) {
            if (isEnabled) {
                // Capteur activé - couleur active
                card.setCardBackgroundColor(activeCardColor);
                icon.setColorFilter(activeIconColor);
                
                // Mettre à jour les TextViews directement
                View cardContent = card.getChildAt(0);
                if (cardContent instanceof LinearLayout) {
                    for (int i = 0; i < ((LinearLayout) cardContent).getChildCount(); i++) {
                        View child = ((LinearLayout) cardContent).getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(activeIconColor);
                        }
                    }
                }
            } else {
                // Capteur désactivé - couleur inactive
                card.setCardBackgroundColor(inactiveCardColor);
                icon.setColorFilter(inactiveIconColor);
                
                // Mettre à jour les TextViews directement
                View cardContent = card.getChildAt(0);
                if (cardContent instanceof LinearLayout) {
                    for (int i = 0; i < ((LinearLayout) cardContent).getChildCount(); i++) {
                        View child = ((LinearLayout) cardContent).getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(inactiveIconColor);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_sensor;
    }

    public void openAccelerometerActivity(View view) {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }

    public void openTemperatureActivity(View view) {
        Intent intent = new Intent(this, TemperatureActivity.class);
        startActivity(intent);
    }

    public void openPressureActivity(View view) {
        Intent intent = new Intent(this, PressureActivity.class);
        startActivity(intent);
    }

    public void openHumidityActivity(View view) {
        Intent intent = new Intent(this, HumidityActivity.class);
        startActivity(intent);
    }

    public void openMagnetometerActivity(View view) {
        Intent intent = new Intent(this, MagnetometerActivity.class);
        startActivity(intent);
    }

    public void openGyroscopeActivity(View view) {
        Intent intent = new Intent(this, GyroscopeActivity.class);
        startActivity(intent);
    }

    public void openGpsActivity(View view) {
        Intent intent = new Intent(this, GpsActivity.class);
        startActivity(intent);
    }
}
