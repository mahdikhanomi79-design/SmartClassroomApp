package com.ruet.cse.smartclassroom.ui.classroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.ClassSchedule;
import com.ruet.cse.smartclassroom.model.Classroom;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;
import com.ruet.cse.smartclassroom.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassroomFragment extends Fragment {

    private RecyclerView rvClassrooms;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom, container, false);
        rvClassrooms = view.findViewById(R.id.rvClassrooms);
        rvClassrooms.setLayoutManager(new LinearLayoutManager(getContext()));
        loadClassroomAvailability();
        return view;
    }

    private void loadClassroomAvailability() {
        String today = TimeUtil.getTodayDayOfWeek();
        String now = TimeUtil.nowAsHHmm();

        FirebaseUtil.getFirestore()
                .collection(FirebaseUtil.COLLECTION_CLASSROOMS)
                .get()
                .addOnSuccessListener(roomSnapshot -> {
                    List<String> allRooms = new ArrayList<>();
                    roomSnapshot.forEach(doc -> {
                        String roomNumber = doc.getString("roomNumber");
                        if (roomNumber != null) allRooms.add(roomNumber);
                    });

                    FirebaseUtil.getFirestore()
                            .collection(FirebaseUtil.COLLECTION_ROUTINES)
                            .whereEqualTo("dayOfWeek", today)
                            .get()
                            .addOnSuccessListener(classSnapshot -> {
                                Map<String, ClassSchedule> occupiedRooms = new HashMap<>();
                                classSnapshot.forEach(doc -> {
                                    ClassSchedule cs = doc.toObject(ClassSchedule.class);
                                    if (TimeUtil.isBetween(now, cs.getStartTime(), cs.getEndTime())) {
                                        occupiedRooms.put(cs.getRoom(), cs);
                                    }
                                });

                                List<Classroom> result = new ArrayList<>();
                                for (String room : allRooms) {
                                    ClassSchedule occupying = occupiedRooms.get(room);
                                    if (occupying != null) {
                                        result.add(new Classroom(room, true, occupying.getCourseName(),
                                                TimeUtil.to12Hour(occupying.getEndTime())));
                                    } else {
                                        result.add(new Classroom(room, false, null, "end of day"));
                                    }
                                }
                                rvClassrooms.setAdapter(new ClassroomAdapter(result));
                            });
                });
    }
}
