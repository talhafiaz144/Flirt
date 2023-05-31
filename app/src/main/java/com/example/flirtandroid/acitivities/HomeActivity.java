package com.example.flirtandroid.acitivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.flirtandroid.R;
import com.example.flirtandroid.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.flirtandroid.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rounded_bottom_navigation));
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Set the selected icon tint color to null
        navView.setItemIconTintList(null);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setIcon(R.drawable.home_tab_icon);
                    Navigation.findNavController(this, R.id.nav_host_fragment_activity_home)
                            .navigate(R.id.navigation_home);
                    return true;
                case R.id.navigation_dashboard:
                    item.setIcon(R.drawable.saved_tab_icon);
                    Navigation.findNavController(this, R.id.nav_host_fragment_activity_home)
                            .navigate(R.id.navigation_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    item.setIcon(R.drawable.settings_tab_icon);
                    Navigation.findNavController(this, R.id.nav_host_fragment_activity_home)
                            .navigate(R.id.navigation_notifications);
                    return true;
                default:
                    return false;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);
    }

    public void navigateToHomeTab() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onBackPressed() {
    }
}