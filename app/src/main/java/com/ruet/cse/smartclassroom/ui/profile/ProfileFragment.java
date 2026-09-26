package com.ruet.cse.smartclassroom.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.auth.LoginActivity;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;

public class ProfileFragment extends Fragment {

    private Spinner spinnerSemester;
    private Spinner spinnerSection;

    private TextView tvEmail;
    private MaterialButton btnSave;
    private MaterialButton btnLogout;

    private final String[] semesterNames = {
            "Select Semester",
            "1st Year - Odd Semester",
            "1st Year - Even Semester",
            "2nd Year - Odd Semester",
            "2nd Year - Even Semester",
            "3rd Year - Odd Semester",
            "3rd Year - Even Semester",
            "4th Year - Odd Semester",
            "4th Year - Even Semester"
    };

    private final String[] sections = {
            "Select Section",
            "A",
            "B",
            "C"
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        tvEmail = view.findViewById(R.id.tvEmail);

        spinnerSemester = view.findViewById(R.id.spinnerSemester);
        spinnerSection = view.findViewById(R.id.spinnerSection);

        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);

        setupSpinners();

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void setupSpinners() {

        ArrayAdapter<String> semesterAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        semesterNames
                );

        semesterAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSemester.setAdapter(semesterAdapter);


        ArrayAdapter<String> sectionAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        sections
                );

        sectionAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSection.setAdapter(sectionAdapter);
    }

    private void loadProfile() {

        String uid = FirebaseUtil.currentUid();

        if (uid == null) {
            return;
        }

        FirebaseUtil.getFirestore()
                .collection(FirebaseUtil.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        return;
                    }

                    String email = doc.getString("email");

                    if (email != null) {
                        tvEmail.setText(email);
                    }

                    Long semesterLong =
                            doc.getLong("semester");

                    if (semesterLong != null) {

                        int semester =
                                semesterLong.intValue();

                        if (semester >= 1 && semester <= 8) {

                            spinnerSemester.setSelection(
                                    semester
                            );
                        }
                    }

                    String section =
                            doc.getString("section");

                    if (section != null) {

                        for (int i = 0; i < sections.length; i++) {

                            if (sections[i].equalsIgnoreCase(section)) {

                                spinnerSection.setSelection(i);

                                break;
                            }
                        }
                    }
                });
    }

    private void saveProfile() {

        int semester =
                spinnerSemester.getSelectedItemPosition();

        String section =
                spinnerSection.getSelectedItem().toString();

        if (semester == 0) {

            Toast.makeText(
                    requireContext(),
                    "Please select your semester",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (section.equals("Select Section")) {

            Toast.makeText(
                    requireContext(),
                    "Please select your section",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = FirebaseUtil.currentUid();

        if (uid == null) {
            return;
        }

        btnSave.setEnabled(false);

        FirebaseUtil.getFirestore()
                .collection(FirebaseUtil.COLLECTION_USERS)
                .document(uid)
                .update(
                        "semester", semester,
                        "section", section
                )
                .addOnCompleteListener(task -> {

                    btnSave.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                requireContext(),
                                "Profile saved successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                requireContext(),
                                "Failed to save profile",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void logout() {

        FirebaseUtil.getAuth().signOut();

        Intent intent =
                new Intent(
                        getActivity(),
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
    }
}