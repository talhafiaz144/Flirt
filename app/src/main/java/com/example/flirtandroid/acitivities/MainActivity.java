package com.example.flirtandroid.acitivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.flirtandroid.R;
import com.example.flirtandroid.adapters.PagerAdapter;
import com.example.flirtandroid.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.flirtandroid.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.white));

        // Initialize SharedPreferences and check if it's the first launch
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true);

        // If it's the first launch, show the getStarted screen
        if (isFirstLaunch) {
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(new PagerAdapter());
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    updateIndicator(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            LinearLayout getStartedBtn = findViewById(R.id.get_started);
            getStartedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set the flag indicating the app has been launched before to false
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_first_launch", false);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // If it's not the first launch, go directly to the login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateIndicator(int position) {
        ImageView dot1 = findViewById(R.id.dot_1);
        ImageView dot2 = findViewById(R.id.dot_2);
        ImageView dot3 = findViewById(R.id.dot_3);
        switch (position) {
            case 0:
                dot1.setImageResource(R.drawable.dot_active);
                dot2.setImageResource(R.drawable.dot_inactive);
                dot3.setImageResource(R.drawable.dot_inactive);
                break;
            case 1:
                dot1.setImageResource(R.drawable.dot_inactive);
                dot2.setImageResource(R.drawable.dot_active);
                dot3.setImageResource(R.drawable.dot_inactive);
                break;
            case 2:
                dot1.setImageResource(R.drawable.dot_inactive);
                dot2.setImageResource(R.drawable.dot_inactive);
                dot3.setImageResource(R.drawable.dot_active);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}