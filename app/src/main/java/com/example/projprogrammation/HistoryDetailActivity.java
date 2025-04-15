package com.example.projprogrammation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class HistoryDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        TextView dateView = findViewById(R.id.detailDate);
        TextView durationView = findViewById(R.id.detailDuration);

        long date = getIntent().getLongExtra("date", 0);
        long duration = getIntent().getLongExtra("duration", 0);

        dateView.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", date));
        durationView.setText("Durée : " + (duration > 0 ? (duration / 1000) + " sec" : "En cours"));
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_history;
    }
}
