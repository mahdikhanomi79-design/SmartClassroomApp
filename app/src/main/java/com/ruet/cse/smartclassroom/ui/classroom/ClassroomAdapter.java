package com.ruet.cse.smartclassroom.ui.classroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.Classroom;

import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ViewHolder> {

    private final List<Classroom> items;

    public ClassroomAdapter(List<Classroom> items) {
        this.items = items;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classroom, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Classroom c = items.get(position);
        holder.tvRoomNumber.setText("Room " + c.getRoomNumber());
        if (c.isOccupied()) {
            holder.tvStatus.setText("Occupied");
            holder.tvStatus.setTextColor(0xFFDC2626);
            holder.tvStatusDetail.setText(c.getOccupiedByCourse() + " • Free at " + c.getFreeAt());
        } else {
            holder.tvStatus.setText("Empty");
            holder.tvStatus.setTextColor(0xFF16A34A);
            holder.tvStatusDetail.setText("Free until " + c.getFreeAt());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvStatus, tvStatusDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStatusDetail = itemView.findViewById(R.id.tvStatusDetail);
        }
    }
}
