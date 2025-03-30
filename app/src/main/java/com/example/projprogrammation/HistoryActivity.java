package com.example.projprogrammation;

import android.os.Bundle;

public class HistoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_history, findViewById(R.id.container));
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_history;
    }
}
