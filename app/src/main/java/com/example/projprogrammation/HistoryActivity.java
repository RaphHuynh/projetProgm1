package com.example.projprogrammation;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<RecordMeta> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_history, findViewById(R.id.container));

        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(records);
        recyclerView.setAdapter(adapter);

        loadRecords();
    }

    private void loadRecords() {
        records.clear();
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Records");
        if (directory.exists()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String content = new String(Files.readAllBytes(file.toPath()));
                        JSONObject meta = new JSONObject(content);
                        long start = meta.optLong("start", 0);
                        long end = meta.optLong("end", 0);
                        long duration = meta.optLong("duration", 0);
                        String filename = meta.optString("filename", "");
                        records.add(new RecordMeta(start, end, duration, filename));
                    } catch (Exception ignored) {}
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getActiveMenuItemId() {
        return R.id.navigation_history;
    }

    static class RecordMeta {
        long start, end, duration;
        String filename;
        RecordMeta(long start, long end, long duration, String filename) {
            this.start = start;
            this.end = end;
            this.duration = duration;
            this.filename = filename;
        }
    }

    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        private final List<RecordMeta> records;
        HistoryAdapter(List<RecordMeta> records) { this.records = records; }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_record, parent, false);
            return new HistoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            RecordMeta meta = records.get(position);
            holder.dateView.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", meta.start));
            holder.durationView.setText("Durée : " + (meta.duration > 0 ? (meta.duration / 1000) + " sec" : "En cours"));
        }

        @Override
        public int getItemCount() { return records.size(); }

        static class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView dateView, durationView;
            CardView cardView;
            HistoryViewHolder(View v) {
                super(v);
                cardView = v.findViewById(R.id.historyCard);
                dateView = v.findViewById(R.id.historyDate);
                durationView = v.findViewById(R.id.historyDuration);
            }
        }
    }
}
