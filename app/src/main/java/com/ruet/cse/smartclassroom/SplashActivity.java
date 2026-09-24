package com.ruet.cse.smartclassroom;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ruet.cse.smartclassroom.auth.LoginActivity;
import com.ruet.cse.smartclassroom.utils.FirebaseUtil;

/** Decides whether to send the user to MainActivity (already logged in) or LoginActivity. */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean loggedIn = FirebaseUtil.getAuth().getCurrentUser() != null;
        Intent intent = new Intent(this, loggedIn ? MainActivity.class : LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
