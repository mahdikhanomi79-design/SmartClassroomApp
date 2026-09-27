package com.ruet.cse.smartclassroom.ui.routine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.ClassSchedule;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;
import com.ruet.cse.smartclassroom.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RoutineFragment extends Fragment {

    private RecyclerView rvRoutine;
    private TextView tvEmptyRoutine;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_routine,
                container,
                false
        );

        rvRoutine = view.findViewById(R.id.rvRoutine);
        tvEmptyRoutine = view.findViewById(R.id.tvEmptyRoutine);

        rvRoutine.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        loadTodayRoutine();

        return view;
    }

    private void loadTodayRoutine() {

        String uid = FirebaseUtil.currentUid();

        if (uid == null) {
            return;
        }

        FirebaseUtil.getFirestore()
                .collection(FirebaseUtil.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {

                    if (!userDoc.exists()) {
                        showEmpty("Please complete your profile");
                        return;
                    }

                    Long semesterLong =
                            userDoc.getLong("semester");

                    String section =
                            userDoc.getString("section");

                    /*
                     * User hasn't selected semester or section yet.
                     */
                    if (semesterLong == null ||
                            semesterLong == 0 ||
                            section == null ||
                            section.trim().isEmpty()) {

                        showEmpty(
                                "Please select your semester and section from Profile"
                        );

                        return;
                    }

                    int semester =
                            semesterLong.intValue();

                    String today =
                            TimeUtil.getTodayDayOfWeek();

                    FirebaseUtil.getFirestore()
                            .collection(FirebaseUtil.COLLECTION_ROUTINES)

                            .whereEqualTo(
                                    "semester",
                                    semester
                            )

                            .whereEqualTo(
                                    "section",
                                    section
                            )

                            .whereEqualTo(
                                    "dayOfWeek",
                                    today
                            )

                            .get()

                            .addOnSuccessListener(snapshot -> {

                                List<ClassSchedule> list =
                                        new ArrayList<>();

                                snapshot.forEach(doc -> {

                                    ClassSchedule cs =
                                            doc.toObject(
                                                    ClassSchedule.class
                                            );

                                    cs.setId(doc.getId());

                                    list.add(cs);
                                });

                                Collections.sort(
                                        list,
                                        Comparator.comparing(
                                                ClassSchedule::getStartTime
                                        )
                                );

                                render(list);
                            })

                            .addOnFailureListener(e ->
                                    showEmpty(
                                            "Failed to load today's routine"
                                    )
                            );
                })

                .addOnFailureListener(e ->
                        showEmpty(
                                "Failed to load profile"
                        )
                );
    }

    private void render(List<ClassSchedule> list) {

        if (list.isEmpty()) {

            showEmpty(
                    "No classes scheduled for today 🎉"
            );

        } else {

            tvEmptyRoutine.setVisibility(View.GONE);

            rvRoutine.setVisibility(View.VISIBLE);

            rvRoutine.setAdapter(
                    new RoutineAdapter(list)
            );
        }
    }

    private void showEmpty(String message) {

        tvEmptyRoutine.setText(message);

        tvEmptyRoutine.setVisibility(View.VISIBLE);

        rvRoutine.setVisibility(View.GONE);
    }
}