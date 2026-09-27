package com.ruet.cse.smartclassroom.ui.teacher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.ClassSchedule;
import com.ruet.cse.smartclassroom.model.Teacher;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;
import com.ruet.cse.smartclassroom.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherFragment extends Fragment {

    private RecyclerView rvTeachers;
    private TeacherAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view =
                inflater.inflate(
                        R.layout.fragment_teacher,
                        container,
                        false
                );

        rvTeachers =
                view.findViewById(
                        R.id.rvTeachers
                );

        rvTeachers.setLayoutManager(
                new LinearLayoutManager(
                        getContext()
                )
        );

        TextInputEditText etSearch =
                view.findViewById(
                        R.id.etSearchTeacher
                );

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        if (adapter != null) {
                            adapter.filter(
                                    s.toString()
                            );
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        loadTeacherAvailability();

        return view;
    }

    private void loadTeacherAvailability() {

        String today =
                TimeUtil.getTodayDayOfWeek();

        String now =
                TimeUtil.nowAsHHmm();

        FirebaseUtil.getFirestore()
                .collection(
                        FirebaseUtil.COLLECTION_TEACHERS
                )
                .get()
                .addOnSuccessListener(
                        teacherSnapshot -> {

                            Map<String, Teacher> teachers =
                                    new HashMap<>();

                            teacherSnapshot.forEach(
                                    doc -> {

                                        String name =
                                                doc.getString(
                                                        "name"
                                                );

                                        if (name == null) {
                                            return;
                                        }
                                        String code =
                                                doc.getString(
                                                        "code"
                                                );

                                        if (code == null ||
                                                code.trim().isEmpty()) {

                                            code =
                                                    doc.getId();
                                        }

                                        Teacher teacher =
                                                new Teacher(
                                                        doc.getId(),
                                                        code,
                                                        name,
                                                        false,
                                                        null,
                                                        null,
                                                        null,
                                                        null
                                                );

                                        teachers.put(
                                                doc.getId(),
                                                teacher
                                        );
                                    }
                            );

                            FirebaseUtil.getFirestore()
                                    .collection(
                                            FirebaseUtil.COLLECTION_ROUTINES
                                    )
                                    .whereEqualTo(
                                            "dayOfWeek",
                                            today
                                    )
                                    .get()
                                    .addOnSuccessListener(
                                            classSnapshot -> {

                                                List<ClassSchedule>
                                                        todaysClasses =
                                                        new ArrayList<>();

                                                classSnapshot.forEach(
                                                        doc -> {

                                                            ClassSchedule cs =
                                                                    doc.toObject(
                                                                            ClassSchedule.class
                                                                    );

                                                            cs.setId(
                                                                    doc.getId()
                                                            );

                                                            todaysClasses.add(
                                                                    cs
                                                            );
                                                        }
                                                );

                                                List<Teacher> result =
                                                        new ArrayList<>();

                                                for (Teacher teacher :
                                                        teachers.values()) {

                                                    result.add(
                                                            buildTeacherStatus(
                                                                    teacher,
                                                                    todaysClasses,
                                                                    now
                                                            )
                                                    );
                                                }

                                                result.sort(
                                                        Comparator.comparing(
                                                                Teacher::getName
                                                        )
                                                );

                                                adapter =
                                                        new TeacherAdapter(
                                                                result
                                                        );

                                                rvTeachers.setAdapter(
                                                        adapter
                                                );
                                            }
                                    );
                        }
                );
    }

    private Teacher buildTeacherStatus(
            Teacher teacher,
            List<ClassSchedule> todaysClasses,
            String now) {

        ClassSchedule currentClass = null;
        ClassSchedule nextClass = null;

        for (ClassSchedule cs :
                todaysClasses) {
            if (!teacher.getId()
                    .equals(cs.getTeacherId())) {

                continue;
            }

            if (TimeUtil.isBetween(
                    now,
                    cs.getStartTime(),
                    cs.getEndTime())) {

                currentClass = cs;

            } else if (
                    cs.getStartTime()
                            .compareTo(now) > 0) {

                if (nextClass == null ||
                        cs.getStartTime()
                                .compareTo(
                                        nextClass.getStartTime()
                                ) < 0) {

                    nextClass = cs;
                }
            }
        }

        if (currentClass != null) {

            teacher.setCurrentlyTeaching(true);

            teacher.setCurrentCourse(
                    currentClass.getCourseName()
            );

            teacher.setCurrentRoom(
                    currentClass.getRoom()
            );

            teacher.setFreeAfter(
                    TimeUtil.to12Hour(
                            currentClass.getEndTime()
                    )
            );

            teacher.setNextClassInfo(null);

        } else {

            teacher.setCurrentlyTeaching(false);

            teacher.setCurrentCourse(null);
            teacher.setCurrentRoom(null);
            teacher.setFreeAfter(null);

            if (nextClass != null) {

                String info =
                        "Next class: "
                                + nextClass.getCourseName()
                                + " at "
                                + TimeUtil.to12Hour(
                                nextClass.getStartTime()
                        )
                                + ", Room "
                                + nextClass.getRoom();

                teacher.setNextClassInfo(info);

            } else {

                teacher.setNextClassInfo(
                        "No more classes today"
                );
            }
        }

        return teacher;
    }
}