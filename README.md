<div align="center">

<h1>Smart Classroom Application</h1>
<h3>Department of Computer Science & Engineering<br>Rajshahi University of Engineering & Technology</h3>

<br>

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2024%20%E2%80%94%20Android%207.0-4CAF50?style=for-the-badge)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-API%2034%20%E2%80%94%20Android%2014-1976D2?style=for-the-badge)](https://developer.android.com/about/versions/14)
[![Build](https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org)

</div>

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Feature Specification](#2-feature-specification)
3. [Technology Stack](#3-technology-stack)
4. [System Architecture](#4-system-architecture)
5. [Project Structure](#5-project-structure)
6. [Installation & Setup](#6-installation--setup)
7. [Firebase Configuration](#7-firebase-configuration)
8. [Firestore Data Schema](#8-firestore-data-schema)
9. [Notification System — Technical Design](#9-notification-system--technical-design)
10. [Database Seeding](#10-database-seeding)
11. [Team Collaboration Guide](#11-team-collaboration-guide)
12. [Development Roadmap](#12-development-roadmap)
13. [Permissions Reference](#13-permissions-reference)

---

## 1. Project Overview

The **Smart Classroom Application** is a native Android client developed for students of the Computer Science & Engineering department at Rajshahi University of Engineering & Technology (RUET). The application addresses three operational pain points within the academic environment through real-time cloud data and locally scheduled device notifications.

The system integrates with **Firebase Cloud Firestore** as its primary data store, **Firebase Authentication** for identity management, and **Android WorkManager** for reliable, battery-optimised background task execution. All notifications are fired locally on-device — no server-side trigger is required for the core feature set.

### Scope

This release implements the following three features as defined in the project proposal:

| Feature | Description |
|---|---|
| **Smart Class Reminder** | Proactive local notifications scheduled 10 minutes before each class, with an additional night-before alert |
| **Empty Classroom Finder** | Real-time display of classroom availability derived from the active routine schedule |
| **Faculty Availability Locator** | Live teacher status board indicating current teaching location and next available slot |

> **Note:** IoT-based physical occupancy detection (Proposal Feature 4) is explicitly excluded from this release and is designated for future development.

---

## 2. Feature Specification

### 2.1 Smart Class Reminder

The reminder system operates by scheduling `WorkManager` jobs calibrated to each class period in the student's semester and section.

- A **10-minute pre-class notification** is enqueued for every class period on the current day and the following day.
- A **night-before notification** fires at **22:00** on the current evening, alerting the student to the first class of the following day.
- All reminders are re-scheduled on every application launch and upon device reboot via a registered `BroadcastReceiver`.

### 2.2 Empty Classroom Finder

- Retrieves the complete list of registered classrooms from the `classrooms` Firestore collection.
- Cross-references the current timestamp against all `routines` entries for the current day using `TimeUtil.isBetween()`.
- Renders each room with its current occupancy status, occupying course name, and expected free-from time.

### 2.3 Faculty Availability Locator

- Retrieves all teacher records from the `teachers` Firestore collection.
- Resolves each teacher's current status against today's `routines` by matching on `teacherId`.
- Presents one of three states per faculty member:
  - **Currently teaching** — displays course name, room number, and free-after time.
  - **Between classes** — displays next scheduled class details.
  - **No remaining classes** — indicates the faculty member is free for the remainder of the day.
- Supports real-time text-based filtering via a `TextWatcher` bound to the search field.

### 2.4 User Profile Management

- Enables the student to select their **semester** (1st Year Odd through 4th Year Even; 8 total) and **section** (A, B, or C).
- Profile data is persisted to `users/{uid}` in Firestore and drives the query parameters for all three feature screens.
- Provides a secure **logout** function that clears the Firebase Auth session and navigates the user back to the login screen with a cleared activity back-stack.

### 2.5 Authentication

- Implements **Firebase email and password authentication** for both sign-in and account registration.
- On successful registration, a corresponding document is written to the `users/{uid}` Firestore collection with default profile values (`semester: 0`, `section: ""`).
- `SplashActivity` evaluates the current Firebase Auth session on every cold start and routes the user to either `MainActivity` or `LoginActivity` accordingly, with no intermediate UI delay.

---

## 3. Technology Stack

### Runtime & Framework

| Component | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| UI Framework | Android Views — Material Design 3 | Material 1.11.0 |
| Navigation | `BottomNavigationView` + `FragmentManager` | AndroidX Fragment 1.6.2 |
| Background Tasks | Android WorkManager | 2.9.0 |
| List Rendering | RecyclerView | 1.3.2 |

### Firebase Services

| Service | Purpose |
|---|---|
| Firebase Authentication | User identity — email/password sign-in and registration |
| Cloud Firestore | Primary data store — routines, classrooms, teachers, users |
| Firebase Cloud Messaging (FCM) | Server-push notification channel for admin broadcasts |
| Firebase Analytics | Usage telemetry |
| Firebase In-App Messaging | In-app notification display support |

### Build Configuration

| Parameter | Value |
|---|---|
| Firebase BOM | 34.17.0 |
| Compile SDK | 34 |
| Target SDK | 34 |
| Minimum SDK | 24 (Android 7.0 Nougat) |
| Application ID | `com.ruet.cse.smartclassroom` |
| `versionCode` | 1 |
| `versionName` | 1.0 |

---

## 4. System Architecture

The application follows a **single-Activity, multi-Fragment** pattern with feature-based package organisation. `MainActivity` acts as the sole navigation host; all feature screens are implemented as `Fragment` instances loaded into a container view.

### Activity & Fragment Navigation

```
SplashActivity  (Launcher — evaluates Firebase Auth state)
      │
      ├── [Not authenticated] ──► LoginActivity
      │                              └── RegisterActivity
      │
      └── [Authenticated] ──────► MainActivity (BottomNavigationView)
                                        │
                          ┌─────────────┼──────────────┬──────────────┐
                          ▼             ▼              ▼              ▼
                   RoutineFragment  ClassroomFragment  TeacherFragment  ProfileFragment
```

### Notification Pipeline

```
MainActivity.onCreate()
      │
      └── ReminderScheduler.scheduleTodayAndTomorrowReminders(context)
                │
                ├── Firestore: users/{uid} → semester, section
                │
                ├── Firestore: routines (today, semester, section)
                │       └── Per class: WorkManager.enqueue(OneTimeWorkRequest)
                │               delay = classStartTime − 10 min − System.currentTimeMillis()
                │
                └── Firestore: routines (tomorrow, semester, section)
                        ├── Per class: WorkManager.enqueue(OneTimeWorkRequest) [+1 day offset]
                        └── First class only: enqueue tonight-22:00 reminder
                                          │
                                          ▼
                                ClassReminderWorker.doWork()
                                          │
                                          ▼
                                NotificationHelper.showReminder()
                                (channel: class_reminders, IMPORTANCE_HIGH)
```

### Data Flow

```
Firestore Collections
  ├── classrooms  ──────────────────────────────► ClassroomFragment
  ├── teachers    ──────────────────────────────► TeacherFragment
  ├── routines    ──► RoutineFragment
  │               ──► ClassroomFragment (occupancy cross-reference)
  │               ──► TeacherFragment  (availability cross-reference)
  │               ──► ReminderScheduler (notification scheduling)
  └── users       ──► RoutineFragment  (semester/section filter)
                  ──► ProfileFragment  (read/write profile)
                  ──► ReminderScheduler (semester/section filter)
```

---

## 5. Project Structure

```
SmartClassroomApp/
│
├── app/
│   ├── build.gradle                          ← Application-level dependencies and build config
│   ├── google-services.json                  ← Firebase project configuration [EXCLUDED FROM VCS]
│   ├── proguard-rules.pro
│   │
│   └── src/main/
│       ├── AndroidManifest.xml               ← Permissions, component declarations
│       ├── ic_launcher-playstore.png
│       │
│       └── java/com/ruet/cse/smartclassroom/
│           │
│           ├── SplashActivity.java           ← Entry point; evaluates auth state and routes accordingly
│           ├── MainActivity.java             ← BottomNavigationView host; triggers reminder scheduling
│           │
│           ├── auth/
│           │   ├── LoginActivity.java        ← Firebase email/password sign-in flow
│           │   └── RegisterActivity.java     ← Account creation; writes initial Firestore user document
│           │
│           ├── model/                        ← Plain Java data transfer objects (DTOs)
│           │   ├── ClassSchedule.java        ← Maps to Firestore `routines` collection documents
│           │   ├── Classroom.java            ← UI model: room number + occupied/free status + metadata
│           │   ├── Student.java              ← Maps to Firestore `users` collection documents
│           │   └── Teacher.java             ← UI model: teacher identity + current class status
│           │
│           ├── notification/
│           │   ├── ReminderScheduler.java    ← Core scheduling logic; enqueues WorkManager jobs
│           │   ├── ClassReminderWorker.java  ← WorkManager Worker implementation; dispatches notification
│           │   ├── NotificationHelper.java   ← Notification channel creation and display
│           │   ├── FcmService.java           ← Firebase Cloud Messaging service handler
│           │   └── BootReceiver.java         ← BOOT_COMPLETED receiver; restores reminders post-restart
│           │
│           ├── ui/
│           │   ├── routine/
│           │   │   ├── RoutineFragment.java  ← Displays today's class schedule for the current student
│           │   │   └── RoutineAdapter.java   ← RecyclerView adapter for class period items
│           │   │
│           │   ├── classroom/
│           │   │   ├── ClassroomFragment.java ← Real-time room availability list
│           │   │   └── ClassroomAdapter.java  ← RecyclerView adapter with occupied/free status rendering
│           │   │
│           │   ├── teacher/
│           │   │   ├── TeacherFragment.java  ← Faculty availability board with live text search
│           │   │   └── TeacherAdapter.java   ← RecyclerView adapter with client-side filtering
│           │   │
│           │   └── profile/
│           │       └── ProfileFragment.java  ← Semester and section selection; account logout
│           │
│           └── utils/
│               ├── FirebaseUtil.java         ← Singleton accessors for FirebaseAuth and Firestore;
│               │                               collection name constants
│               └── TimeUtil.java            ← Time parsing utilities: HH:mm ↔ 12-hour format,
│                                               isBetween(), getTodayDayOfWeek(), nowAsHHmm()
│
├── scripts/
│   └── seed_firestore_new_routine.py        ← Python utility to populate Firestore with sample data
│
├── build.gradle                             ← Root build file; applies Google Services plugin
├── settings.gradle                          ← Project name and module declarations
├── gradle.properties                        ← Gradle configuration properties
├── gradlew / gradlew.bat                    ← Gradle wrapper executables
└── README.md
```

---

## 6. Installation & Setup

### Prerequisites

| Requirement | Specification |
|---|---|
| IDE | Android Studio Hedgehog (2023.1.1) or later |
| JDK | Java 17 (bundled with Android Studio) |
| Device / Emulator | Android API 24 or higher |
| Firebase Project | Required — see [Section 7](#7-firebase-configuration) |

### Step 1 — Clone the Repository

```bash
git clone https://github.com/mahdikhanomi79-design/SmartClassroomApp.git
cd SmartClassroomApp
```

### Step 2 — Open in Android Studio

Navigate to **File → Open** and select the `SmartClassroomApp` directory. Allow Gradle synchronisation to complete; all declared dependencies will be resolved automatically.

### Step 3 — Add Firebase Configuration

The `google-services.json` file is excluded from version control. Follow [Section 7](#7-firebase-configuration) to generate and place the file before attempting to build.

### Step 4 — Build and Run

Connect a physical device or launch an emulator, then execute **Run → Run 'app'** in Android Studio.

> Upon first launch, the application will present the login screen. Register a new account, then navigate to the **Profile** tab to configure your semester and section. All three feature screens depend on this profile data.

---

## 7. Firebase Configuration

> This step is **mandatory**. The application will not compile or run without a valid `google-services.json` file.

### 7.1 Create a Firebase Project

1. Navigate to [console.firebase.google.com](https://console.firebase.google.com) and select **Add project**.
2. Assign a project name and complete the project creation wizard.

### 7.2 Register the Android Application

1. Within the Firebase project dashboard, select **Add app → Android**.
2. Enter the package name: `com.ruet.cse.smartclassroom`
3. Download the generated `google-services.json` file.
4. Place the file at the following path within the project: `app/google-services.json`

> **Security Notice:** This file contains sensitive API credentials. It is listed in `.gitignore` and must never be committed to version control.

### 7.3 Enable Required Services

| Service | Console Navigation | Required Configuration |
|---|---|---|
| **Authentication** | Build → Authentication → Sign-in method | Enable **Email/Password** provider |
| **Cloud Firestore** | Build → Firestore Database → Create database | Select **Start in test mode** during development |
| **Cloud Messaging** | Build → Cloud Messaging | Enabled automatically upon app registration |

---

## 8. Firestore Data Schema

The application reads from four top-level Firestore collections. The schema for each is defined below. Collections may be created manually via the Firestore Console or populated using the [seed script](#10-database-seeding).

### 8.1 `users` Collection

Created automatically by `RegisterActivity` upon successful account registration.

```
users/{uid}
  uid:       string    — Firebase Authentication UID
  email:     string    — User's registered email address
  semester:  number    — Academic semester (1–8); default 0 (unset)
  section:   string    — Class section ("A", "B", or "C"); default "" (unset)
```

### 8.2 `classrooms` Collection

Static registry of all physical classrooms on campus.

```
classrooms/{autoId}
  roomNumber: string   — Room identifier, e.g. "101", "C-402"
```

Add one document per classroom. The `roomNumber` value must match the `room` field in `routines` documents.

### 8.3 `teachers` Collection

Faculty directory. The document ID is used as the foreign key in `routines`.

```
teachers/{teacherId}           ← Use a stable, readable ID, e.g. "boshir_ahmed"
  name: string                 — Full name, e.g. "Prof. Dr. Boshir Ahmed"
  code: string                 — Optional short code, e.g. "BA"
```

### 8.4 `routines` Collection

One document per class period per day. This is the central collection queried by all three feature screens.

```
routines/{autoId}
  courseName:  string   — Full course name, e.g. "Digital Logic Design"
  courseCode:  string   — Course code, e.g. "CSE 2103"
  teacherId:   string   — Must match a document ID in teachers/{teacherId}
  teacherName: string   — Denormalised teacher name for display purposes
  room:        string   — Must match a roomNumber value in classrooms
  dayOfWeek:   string   — One of: SUNDAY | MONDAY | TUESDAY | WEDNESDAY |
                          THURSDAY | FRIDAY | SATURDAY  (uppercase only)
  startTime:   string   — 24-hour format "HH:mm", e.g. "08:40"
  endTime:     string   — 24-hour format "HH:mm", e.g. "10:00"
  semester:    number   — Integer 1–8
  section:     string   — "A", "B", or "C"
```

#### Data Constraints

| Field | Constraint |
|---|---|
| `dayOfWeek` | Must be **all uppercase**. `"SATURDAY"` is valid; `"Saturday"` is not. |
| `startTime` / `endTime` | Must be zero-padded 24-hour `"HH:mm"`. `"08:40"` is valid; `"8:40"` may cause comparison failures. |
| `teacherId` | Must exactly match a document ID in the `teachers` collection. |
| `room` | Must match a `roomNumber` value in the `classrooms` collection. |

#### Required Composite Indexes

Firestore will prompt index creation on first query execution. The following composite indexes are required:

| Collection | Fields |
|---|---|
| `routines` | `semester` ASC, `section` ASC, `dayOfWeek` ASC |
| `routines` | `dayOfWeek` ASC |

---

## 9. Notification System — Technical Design

### 9.1 Scheduling Mechanism

The notification system is built on **Android WorkManager**, which provides guaranteed execution that is resilient to process death, device restarts, and Doze mode. All notifications are fired locally on-device; no server-side trigger is required.

`ReminderScheduler.scheduleTodayAndTomorrowReminders()` is invoked by `MainActivity.onCreate()` on every application launch, and by `BootReceiver` following a device restart.

### 9.2 Scheduling Logic

```
scheduleTodayAndTomorrowReminders(context)
  │
  ├── Read users/{uid} → { semester, section }
  │
  ├── scheduleForDay(context, semester, section, TODAY, isTomorrow=false)
  │     ├── Query: routines WHERE semester=? AND section=? AND dayOfWeek=?
  │     └── Per result, sorted by startTime:
  │           enqueueReminder(delay = startTime − 10min − now, title = "Class in 10 minutes")
  │
  └── scheduleForDay(context, semester, section, TOMORROW, isTomorrow=true)
        ├── Query: routines WHERE semester=? AND section=? AND dayOfWeek=?
        └── Per result, sorted by startTime:
              enqueueReminder(delay = tomorrow's startTime − 10min − now)
              └── For index 0 (first class) only:
                    enqueueReminder(delay = tonight 22:00 − now, title = "Tomorrow's First Class")
```

### 9.3 Notification Types

| Notification | Trigger Condition | Payload |
|---|---|---|
| **Pre-class Alert** | 10 minutes before each scheduled class period | Course name, teacher, room, start time |
| **Night-Before Alert** | Tonight at 22:00, for the first class of the following day | Course name, teacher, room, start time |

### 9.4 Boot Persistence

`BootReceiver` is registered in `AndroidManifest.xml` to receive `android.intent.action.BOOT_COMPLETED`. Upon device restart, all pending WorkManager jobs are cleared by the OS. `BootReceiver.onReceive()` calls `ReminderScheduler.scheduleTodayAndTomorrowReminders()` to restore all reminders for the authenticated user.

### 9.5 FCM Integration

`FcmService` extends `FirebaseMessagingService` and is registered for the `com.google.firebase.MESSAGING_EVENT` intent. This service handles server-originated push messages — for example, an administrator broadcasting a room change notification from a future admin panel or Cloud Function. FCM is supplementary infrastructure; it does not replace the WorkManager-based local reminder system.

---

## 10. Database Seeding

The script `scripts/seed_firestore_new_routine.py` provides an automated mechanism for populating Firestore with representative sample data, including classrooms, faculty, and a complete weekly routine schedule.

### Requirements

```bash
pip install firebase-admin
```

### Usage

```bash
python scripts/seed_firestore_new_routine.py path/to/serviceAccountKey.json
```

### Obtaining a Service Account Key

1. Open the Firebase Console → **Project Settings → Service Accounts**.
2. Click **Generate new private key** and download the resulting JSON file.
3. Pass the absolute path to this file as the script argument.

> **Security Notice:** The service account key grants administrative access to the Firebase project. It must not be committed to version control.

---

## 11. Team Collaboration Guide

### Repository Information

| Property | Value |
|---|---|
| Repository | [github.com/mahdikhanomi79-design/SmartClassroomApp](https://github.com/mahdikhanomi79-design/SmartClassroomApp) |
| Default Branch | `main` |
| Branching Strategy | Single-branch (all contributions target `main` directly) |
| Commit Convention | [Conventional Commits](https://www.conventionalcommits.org/) |

### Push Segments

The commit history is structured into five discrete segments, each representing a logical unit of work. Segments must be applied in order.

| Segment | Scope | Assigned To |
|---|---|---|
| **Push 1** | Project scaffold — build configuration, data models, utility classes, core activities | Team Member 1 |
| **Push 2** | Authentication flows (login, registration) and user profile management | Team Member 1 / 2 |
| **Push 3** | Feature screens — Routine, Classroom Finder, Faculty Locator | Team Member 2 |
| **Push 4** | Notification system — WorkManager scheduling, FCM, boot persistence | Team Member 3 |
| **Push 5** | Tooling — Firestore seed script, Firebase configuration template, documentation | Team Member 1 |

### Cloning and Local Setup (for Team Members)

```bash
# 1. Clone the repository
git clone https://github.com/mahdikhanomi79-design/SmartClassroomApp.git

# 2. Provision Firebase configuration
cp app/google-services.json.template app/google-services.json
# Edit app/google-services.json and replace placeholder values with
# credentials from the Firebase Console → Project Settings → General

# 3. Open in Android Studio and synchronise Gradle
```

### Commit Message Format

```
<type>(<scope>): <imperative summary in present tense>

[optional body: explain what and why, not how]
```

**Permitted types:**

| Type | Usage |
|---|---|
| `feat` | A new feature or user-visible capability |
| `fix` | A defect correction |
| `chore` | Build, dependency, or tooling changes with no production code impact |
| `refactor` | Code restructuring with no behaviour change |
| `docs` | Documentation-only changes |
| `style` | Formatting changes with no logic impact |
| `test` | Addition or modification of tests |

**Permitted scopes:** `auth`, `ui`, `notifications`, `model`, `utils`, `firebase`, `build`, `scripts`

**Examples:**
```
feat(auth): enforce minimum password length of 6 characters on registration
fix(notifications): cancel existing WorkManager jobs before re-enqueuing on launch
chore(build): upgrade Firebase BOM from 34.16.0 to 34.17.0
docs: add composite index requirements to Firestore schema documentation
```

---

## 12. Development Roadmap

### Completed — Release 1.0

| Item | Status |
|---|---|
| Firebase email/password authentication | Complete |
| Automatic Firestore user document creation on registration | Complete |
| Smart Class Reminder — 10-minute pre-class local notification | Complete |
| Smart Class Reminder — night-before 22:00 alert | Complete |
| Boot-persistent reminder rescheduling via `BootReceiver` | Complete |
| Empty Classroom Finder — real-time Firestore availability | Complete |
| Faculty Availability Locator — live status and search | Complete |
| User profile — semester and section selection with Firestore persistence | Complete |
| FCM infrastructure for server-push notification support | Complete |

### Known Defects

| Defect | Description | Affected Components |
|---|---|---|
| Missing `section` field in `ClassSchedule` POJO | `RoutineFragment` and `ReminderScheduler` both issue Firestore queries filtered by `section`, but `ClassSchedule.java` does not declare a `section` field. Firestore `.toObject()` silently drops unmapped fields, causing queries to return unfiltered results. **Resolution:** add `private String section;` with corresponding getter and setter to `ClassSchedule.java`. | `ClassSchedule.java`, `RoutineFragment`, `ReminderScheduler` |
| Firestore security rules in test mode | Database is currently configured in open test mode. All read/write operations are permitted without authentication checks. | Entire Firestore database |

### Planned — Release 2.0

| Item | Priority |
|---|---|
| Firestore security rules — role-based read/write enforcement | High |
| Admin module — in-app routine management UI to replace direct Firestore Console access | High |
| Resolution of `ClassSchedule.section` POJO defect | High |
| Offline data persistence via Firestore local cache | Medium |
| System-aware dark mode theme | Medium |
| Illustrated empty states and retry affordances across all fragments | Medium |
| Multi-department support (extend beyond CSE) | Low |
| IoT-based physical room occupancy detection (Proposal Feature 4) | Low |

---

## 13. Permissions Reference

The application declares the following permissions in `AndroidManifest.xml`:

| Permission | Rationale |
|---|---|
| `android.permission.INTERNET` | Required for all Firebase network operations — authentication, Firestore queries, FCM token registration |
| `android.permission.POST_NOTIFICATIONS` | Required on Android 13 (API 33) and above to display local class reminder notifications |
| `android.permission.RECEIVE_BOOT_COMPLETED` | Required to receive the system `BOOT_COMPLETED` broadcast and restore scheduled reminders following a device restart |

---

<div align="center">

Developed as an academic project for the Department of Computer Science & Engineering<br>
Rajshahi University of Engineering & Technology (RUET)<br>
&copy; 2026 Mahdi Khan Omi & Team. All rights reserved.

</div>
