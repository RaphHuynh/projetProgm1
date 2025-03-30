package com.example.projprogrammation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SensorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vérifiez si l'Activity parent implémente une interface ou utilisez un cast générique
        if (getActivity() instanceof SensorActivity) {
            SensorActivity sensorActivity = (SensorActivity) getActivity();
            // ...utilisez sensorActivity si nécessaire...
        } else {
            Log.e("SensorFragment", "Parent activity is not an instance of SensorActivity");
        }
    }
}
