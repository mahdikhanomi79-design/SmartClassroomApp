package com.ruet.cse.smartclassroom.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import com.ruet.cse.smartclassroom.MainActivity;
import com.ruet.cse.smartclassroom.R;
import com.ruet.cse.smartclassroom.model.Student;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        progressBar = findViewById(R.id.progressBar);

        View btnRegister = findViewById(R.id.btnRegister);
        View tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {

        String email = text(etEmail);
        String password = text(etPassword);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        setLoading(true);

        FirebaseUtil.getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        setLoading(false);

                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

                        Toast.makeText(
                                this,
                                msg,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    FirebaseUser user =
                            FirebaseUtil.getAuth().getCurrentUser();

                    if (user == null) {
                        setLoading(false);
                        Toast.makeText(
                                this,
                                "User creation failed",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String uid = user.getUid();
                    Student student = new Student(
                            uid,
                            email,
                            0,
                            ""
                    );

                    FirebaseUtil.getFirestore()
                            .collection(FirebaseUtil.COLLECTION_USERS)
                            .document(uid)
                            .set(student)
                            .addOnCompleteListener(saveTask -> {

                                setLoading(false);

                                if (!saveTask.isSuccessful()) {

                                    Toast.makeText(
                                            this,
                                            "Account created, but profile setup failed",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                Toast.makeText(
                                        this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                startActivity(
                                        new Intent(
                                                this,
                                                MainActivity.class
                                        )
                                );

                                finish();
                            });
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );
    }

    private String text(TextInputEditText et) {

        if (et.getText() == null) {
            return "";
        }

        return et.getText()
                .toString()
                .trim();
    }
}