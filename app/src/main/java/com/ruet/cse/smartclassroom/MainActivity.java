package com.ruet.cse.smartclassroom;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ruet.cse.smartclassroom.notification.ReminderScheduler;
import com.ruet.cse.smartclassroom.ui.classroom.ClassroomFragment;
import com.ruet.cse.smartclassroom.ui.profile.ProfileFragment;
import com.ruet.cse.smartclassroom.ui.routine.RoutineFragment;
import com.ruet.cse.smartclassroom.ui.teacher.TeacherFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_classroom) {
                fragment = new ClassroomFragment();
            } else if (id == R.id.nav_teacher) {
                fragment = new TeacherFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else {
                fragment = new RoutineFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_routine);

        ReminderScheduler.scheduleTodayAndTomorrowReminders(getApplicationContext());
    }
}
