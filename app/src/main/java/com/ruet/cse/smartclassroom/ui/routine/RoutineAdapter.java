package com.ruet.cse.smartclassroom.ui.routine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.ClassSchedule;
import com.ruet.cse.smartclassroom.utils.TimeUtil;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    private final List<ClassSchedule> items;

    public RoutineAdapter(List<ClassSchedule> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_schedule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassSchedule item = items.get(position);
        holder.tvTime.setText(TimeUtil.to12Hour(item.getStartTime()));
        holder.tvCourse.setText(item.getCourseName());
        holder.tvTeacher.setText(item.getTeacherName());
        holder.tvRoom.setText("Room " + item.getRoom());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvCourse, tvTeacher, tvRoom;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCourse = itemView.findViewById(R.id.tvCourse);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }
}
