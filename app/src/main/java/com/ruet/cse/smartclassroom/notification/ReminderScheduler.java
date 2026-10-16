package com.ruet.cse.smartclassroom.notification;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.ruet.cse.smartclassroom.model.ClassSchedule;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;
import com.ruet.cse.smartclassroom.utils.TimeUtil;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    public static void scheduleTodayAndTomorrowReminders(
            Context context) {

        String uid = FirebaseUtil.currentUid();

        if (uid == null) {
            return;
        }

        FirebaseUtil.getFirestore()
                .collection(FirebaseUtil.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {

                    Long semesterLong =
                            userDoc.getLong("semester");

                    String section =
                            userDoc.getString("section");

                    if (semesterLong == null ||
                            semesterLong == 0 ||
                            section == null ||
                            section.trim().isEmpty()) {

                        return;
                    }

                    int semester =
                            semesterLong.intValue();

                    scheduleForDay(
                            context,
                            semester,
                            section,
                            TimeUtil.getTodayDayOfWeek(),
                            false
                    );

                    scheduleForDay(
                            context,
                            semester,
                            section,
                            dayAfter(
                                    TimeUtil.getTodayDayOfWeek()
                            ),
                            true
                    );
                });
    }

    private static void scheduleForDay(
            Context context,
            int semester,
            String section,
            String dayOfWeek,
            boolean isTomorrow) {

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
                        dayOfWeek
                )

                .get()

                .addOnSuccessListener(snapshot -> {

                    List<ClassSchedule> classes =
                            snapshot.toObjects(
                                    ClassSchedule.class
                            );

                    if (classes.isEmpty()) {
                        return;
                    }
                    classes.sort(
                            (a, b) ->
                                    a.getStartTime()
                                            .compareTo(
                                                    b.getStartTime()
                                            )
                    );

                    for (int i = 0;
                         i < classes.size();
                         i++) {

                        ClassSchedule cs =
                                classes.get(i);
                        int reminderId =
                                (isTomorrow ? 10000 : 0) + i;
                        enqueueReminder(
                                context,
                                cs,
                                minutesBeforeDelayMs(
                                        cs.getStartTime(),
                                        10,
                                        isTomorrow
                                ),
                                "Class in 10 minutes",
                                reminderId
                        );
                        if (i == 0 && isTomorrow) {

                            enqueueReminder(
                                    context,
                                    cs,
                                    tonightAt10PmDelayMs(),
                                    "Tomorrow's First Class",
                                    reminderId + 1
                            );
                        }
                    }
                });
    }

    private static void enqueueReminder(
            Context context,
            ClassSchedule cs,
            long delayMs,
            String title,
            int notificationId) {
        if (delayMs <= 0) {
            return;
        }
        String courseName =
                cs.getCourseName() != null
                        ? cs.getCourseName()
                        : "Class";

        String teacherName =
                cs.getTeacherName() != null
                        ? cs.getTeacherName()
                        : "Unknown";

        String room =
                cs.getRoom() != null
                        ? cs.getRoom()
                        : "Unknown";

        String startTime =
                cs.getStartTime() != null
                        ? cs.getStartTime()
                        : "--:--";

        String body =
                courseName
                        + "\nTeacher: "
                        + teacherName
                        + "\nRoom: "
                        + room
                        + "\nTime: "
                        + TimeUtil.to12Hour(
                        startTime
                );

        Data input =
                new Data.Builder()
                        .putString(
                                ClassReminderWorker.KEY_TITLE,
                                title
                        )
                        .putString(
                                ClassReminderWorker.KEY_BODY,
                                body
                        )
                        .putInt(
                                ClassReminderWorker.KEY_NOTIFICATION_ID,
                                notificationId
                        )
                        .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(
                        ClassReminderWorker.class
                )
                        .setInitialDelay(
                                delayMs,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(input)
                        .build();

        WorkManager
                .getInstance(context)
                .enqueue(request);
    }

    private static long minutesBeforeDelayMs(
            String startTimeHHmm,
            int minutesBefore,
            boolean isTomorrow) {
        if (startTimeHHmm == null ||
                startTimeHHmm.trim().isEmpty() ||
                !startTimeHHmm.contains(":")) {

            return -1;
        }

        Calendar target =
                Calendar.getInstance();

        if (isTomorrow) {
            target.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        String[] parts =
                startTimeHHmm.split(":");
        if (parts.length < 2) {
            return -1;
        }

        try {

            int hour =
                    Integer.parseInt(
                            parts[0].trim()
                    );

            int minute =
                    Integer.parseInt(
                            parts[1].trim()
                    );

            target.set(
                    Calendar.HOUR_OF_DAY,
                    hour
            );

            target.set(
                    Calendar.MINUTE,
                    minute
            );

            target.set(
                    Calendar.SECOND,
                    0
            );

            target.set(
                    Calendar.MILLISECOND,
                    0
            );

            target.add(
                    Calendar.MINUTE,
                    -minutesBefore
            );

        } catch (NumberFormatException e) {

            return -1;
        }

        return target.getTimeInMillis()
                - System.currentTimeMillis();
    }

    private static long tonightAt10PmDelayMs() {

        Calendar target =
                Calendar.getInstance();

        target.set(
                Calendar.HOUR_OF_DAY,
                22
        );

        target.set(
                Calendar.MINUTE,
                0
        );

        target.set(
                Calendar.SECOND,
                0
        );

        target.set(
                Calendar.MILLISECOND,
                0
        );
        return target.getTimeInMillis()
                - System.currentTimeMillis();
    }

    private static String dayAfter(
            String dayOfWeek) {

        String[] days = {
                "SUNDAY",
                "MONDAY",
                "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY"
        };

        for (int i = 0;
             i < days.length;
             i++) {

            if (days[i].equals(dayOfWeek)) {

                return days[(i + 1) % 7];
            }
        }

        return dayOfWeek;
    }
}