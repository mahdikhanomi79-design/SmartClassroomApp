# Smart Classroom — RUET CSE

Android app implementing the three core features from the project proposal:

1. **Smart Class Reminder** — 10-min-before notification for every class, plus a
   "night before" (10 PM) notification for the next day's first class.
2. **Empty Classroom Finder** — real-time room availability across all 8 semesters.
3. **Faculty Availability / Teacher Locator** — search a teacher, see if they're
   teaching now, where, and when they're free.

IoT occupancy (Feature 4) is intentionally **not** implemented — kept as future
work per your team's decision.

Stack: **Java, Android Studio, Firebase (Auth, Firestore, Cloud Messaging), WorkManager.**

---

## 1. Open the project

Unzip and open the `SmartClassroomApp` folder directly in Android Studio
(File → Open). Let Gradle sync — it will pull all dependencies automatically.

## 2. Connect Firebase (required before it will run)

The project has no `google-services.json` yet — you need to generate your own:

1. Go to the [Firebase Console](https://console.firebase.google.com/) → Create project.
2. Add an Android app with package name `com.ruet.cse.smartclassroom`.
3. Download `google-services.json` and place it in `app/google-services.json`.
4. In the Firebase Console, enable:
   - **Authentication** → Sign-in method → Email/Password.
   - **Firestore Database** → Create database (start in test mode while developing).
   - **Cloud Messaging** — enabled by default once the app is registered.

## 3. Firestore data shape

The app expects three top-level collections. Set these up manually in the
Firestore console (or run the seed script below) so the app has data to show.

### `classrooms` — static room list
```
classrooms/{autoId}
  roomNumber: "104"   (string)
```
Add one document per room (101, 102, 103, 104, C-402, ...).

### `teachers` — faculty directory
```
teachers/{teacherId}     <- use a short readable id, e.g. "boshir_ahmed"
  name: "Prof. Dr. Boshir Ahmed"   (string)
```

### `routines` — every class period, flattened (one doc per period per day)
```
routines/{autoId}
  courseName: "Digital Logic Design"
  courseCode: "CSE 2103"
  teacherId:  "boshir_ahmed"      <- must match a teachers/{id}
  teacherName: "Prof. Dr. Boshir Ahmed"
  room: "104"
  dayOfWeek: "SATURDAY"           <- SUNDAY..SATURDAY, all caps
  startTime: "08:40"              <- 24h "HH:mm"
  endTime:   "10:00"
  semester: 2                     <- number, 1-8
```

### `users` — one doc per student, keyed by Firebase Auth UID
Created automatically by `RegisterActivity` on sign-up:
```
users/{uid}
  uid, name, studentId, email, semester
```

## 4. Seed sample data (optional, for quick testing)

`scripts/seed_firestore.py` populates rooms, one teacher, and one class period
matching the proposal's own examples (DLD, Room 104, 8:40 AM). Requires a
Firebase service account key:

```bash
pip install firebase-admin
python scripts/seed_firestore.py path/to/serviceAccountKey.json
```

(Download the service account key from Firebase Console → Project Settings →
Service Accounts → Generate new private key.)

## 5. Notes on how the reminder logic works

- `ReminderScheduler` runs every time `MainActivity` opens (and again after a
  device reboot, via `BootReceiver`). It reads the student's `semester`, pulls
  today's and tomorrow's classes, and schedules one `WorkManager` job per
  reminder with the correct delay.
- Notifications fire locally via `ClassReminderWorker` → `NotificationHelper`.
- `FcmService` is wired up separately for **push** notifications (e.g. an
  admin broadcasting "Room changed" from a future admin panel or Cloud
  Function) — that's additive, not required for the three core features.

## 6. What's stubbed vs. what's left for your team

Already implemented: Auth (login/register), the three feature screens with
live Firestore queries, local reminder scheduling, bottom nav, launcher icon.

Left for you (per your week-by-week plan): an admin flow to upload/edit
routines (right now data entry happens directly in the Firestore console),
Firestore security rules (currently assumed test-mode/open — lock these down
before submission), and polishing empty/error states.
