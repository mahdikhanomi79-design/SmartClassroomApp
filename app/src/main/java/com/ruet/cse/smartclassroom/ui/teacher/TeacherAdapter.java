package com.ruet.cse.smartclassroom.ui.teacher;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeacherAdapter
        extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private final List<Teacher> allItems;

    private final List<Teacher> filtered;

    public TeacherAdapter(List<Teacher> items) {

        this.allItems = items;

        this.filtered =
                new ArrayList<>(items);
    }

    public void filter(String query) {

        filtered.clear();

        if (query == null ||
                query.trim().isEmpty()) {

            filtered.addAll(allItems);

        } else {

            String q =
                    query.toLowerCase(
                            Locale.US
                    ).trim();

            for (Teacher teacher :
                    allItems) {

                String name =
                        teacher.getName() == null
                                ? ""
                                : teacher.getName();

                String code =
                        teacher.getCode() == null
                                ? ""
                                : teacher.getCode();

                if (name.toLowerCase(
                                Locale.US)
                        .contains(q)
                        ||
                        code.toLowerCase(
                                        Locale.US)
                                .contains(q)) {

                    filtered.add(teacher);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_teacher,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Teacher teacher =
                filtered.get(position);

        holder.tvName.setText(
                teacher.getName()
        );

        holder.tvCode.setText(
                "Code: " + teacher.getCode()
        );

        if (teacher.isCurrentlyTeaching()) {

            holder.tvStatus.setText(
                    "Teaching now"
            );

            holder.tvStatus.setTextColor(
                    0xFFDC2626
            );

            holder.tvDetail.setText(
                    teacher.getCurrentCourse()
                            + " • Room "
                            + teacher.getCurrentRoom()
                            + " • Available after "
                            + teacher.getFreeAfter()
            );

        } else {

            holder.tvStatus.setText(
                    "Currently Free"
            );

            holder.tvStatus.setTextColor(
                    0xFF16A34A
            );

            holder.tvDetail.setText(
                    teacher.getNextClassInfo() != null
                            ? teacher.getNextClassInfo()
                            : "No more classes today"
            );
        }

        /*
         * Clicking a teacher shows full information.
         */
        holder.itemView.setOnClickListener(
                v -> showTeacherDialog(
                        holder.itemView,
                        teacher
                )
        );
    }

    private void showTeacherDialog(
            View view,
            Teacher teacher) {

        String message =
                "Teacher Code: "
                        + teacher.getCode()
                        + "\n\n"
                        + "Status: "
                        + (
                        teacher.isCurrentlyTeaching()
                                ? "Teaching now"
                                : "Currently Free"
                );

        if (teacher.isCurrentlyTeaching()) {

            message +=
                    "\n\nCourse: "
                            + teacher.getCurrentCourse()
                            + "\nRoom: "
                            + teacher.getCurrentRoom()
                            + "\nAvailable after: "
                            + teacher.getFreeAfter();

        } else {

            message +=
                    "\n\n"
                            + (
                            teacher.getNextClassInfo() != null
                                    ? teacher.getNextClassInfo()
                                    : "No more classes today"
                    );
        }

        new AlertDialog.Builder(
                view.getContext()
        )
                .setTitle(
                        teacher.getName()
                )
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    @Override
    public int getItemCount() {

        return filtered.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCode;
        TextView tvStatus;
        TextView tvDetail;

        ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvName =
                    itemView.findViewById(
                            R.id.tvTeacherName
                    );

            tvCode =
                    itemView.findViewById(
                            R.id.tvTeacherCode
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tvTeacherStatus
                    );

            tvDetail =
                    itemView.findViewById(
                            R.id.tvTeacherDetail
                    );
        }
    }
}