package com.example.projprogrammation;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class NetworkHandler {
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView networkData;
    private long previousRxBytes = 0;
    private long previousTxBytes = 0;

    public NetworkHandler(TextView networkData) {
        this.networkData = networkData;
    }

    public void startNetworkUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long rxBytes = TrafficStats.getTotalRxBytes();
                long txBytes = TrafficStats.getTotalTxBytes();

                if (rxBytes != TrafficStats.UNSUPPORTED && txBytes != TrafficStats.UNSUPPORTED) {
                    long rxDiff = rxBytes - previousRxBytes;
                    long txDiff = txBytes - previousTxBytes;

                    previousRxBytes = rxBytes;
                    previousTxBytes = txBytes;

                    double rxSpeed = (double) rxDiff / 1024.0; // KB/s
                    double txSpeed = (double) txDiff / 1024.0; // KB/s

                    networkData.setText(String.format("Réseau:\n↓ %.2f KB/s\n↑ %.2f KB/s", rxSpeed, txSpeed));
                }

                handler.postDelayed(this, 1000); // Update every 1 second
            }
        });
    }
}