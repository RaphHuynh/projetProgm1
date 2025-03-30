package com.example.projprogrammation;

import android.os.Bundle;

public class RecordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_record, findViewById(R.id.container));
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_record;
    }
}
