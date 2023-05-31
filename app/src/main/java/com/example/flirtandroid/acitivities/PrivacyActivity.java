package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.WarningCustomSnackbar;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LinearLayout changePwBtn = findViewById(R.id.chg_pw_btn);
        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the user is logged in
                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                if (prefs.contains("guest")) {
                    // show a Snackbar message if the user is not logged in
                    WarningCustomSnackbar.showSnackbar(PrivacyActivity.this, "Guest users cannot change password");
                    return;
                }
                Intent intent = new Intent(PrivacyActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}