package com.example.projprogrammation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SensorDataAdapter extends RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder> {

    private final List<String> sensorDataList;

    public SensorDataAdapter(List<String> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }

    @NonNull
    @Override
    public SensorDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SensorDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorDataViewHolder holder, int position) {
        holder.sensorDataTextView.setText(sensorDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return sensorDataList.size();
    }

    static class SensorDataViewHolder extends RecyclerView.ViewHolder {
        TextView sensorDataTextView;

        public SensorDataViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorDataTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
