package com.example.flirtandroid.acitivities.extras;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flirtandroid.R;
import com.example.flirtandroid.Utils.PickupLine;
import com.example.flirtandroid.adapters.newAddInterestAdapter;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Upload_AddInterestActivity extends AppCompatActivity {
    private PickupLine pickupLine;
    private static final String PROGRESS_DIALOG = "Upload_AddInterestActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_add_interest);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button generateBtn = findViewById(R.id.generate_btn);
        generateBtn.setAllCaps(false);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        List<String> list = new ArrayList<>();
        newAddInterestAdapter adapter = new newAddInterestAdapter(list);
        recyclerView.setAdapter(adapter);
       // adapter.setOnDragListener(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView personName = findViewById(R.id.persons_name);
        String newText = getIntent().getStringExtra("text");
        personName.setText(newText);

        // Retrieve the pickupLine string from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pickupLine")) {
            String pickupLineJson = intent.getStringExtra("pickupLine");

            Gson gson = new Gson();
            this.pickupLine = gson.fromJson(pickupLineJson, PickupLine.class);
            Log.d("TAG", "onCreate pickupLine: " + pickupLine);

            if (pickupLine != null && pickupLine.getWords() != null) {
                List<String> words = pickupLine.getWords();
                Log.d("TAG", "onCreate words: " + words);
                adapter = new newAddInterestAdapter(words);
                recyclerView.setAdapter(adapter);
//                adapter.setOnDragListener(recyclerView);
                generateBtn.setVisibility(View.VISIBLE);
            }
        }

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        Intent intent = new Intent(Upload_AddInterestActivity.this, newPersonInterestActivity.class);
                        intent.putExtra("text", newText);
                        // Storing the pickupLine object in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String pickupLineJson = gson.toJson(pickupLine);
                        editor.putString("pickupLine", pickupLineJson);
                        editor.apply();
                        startActivity(intent);
                    }
                }, 700);
            }
        });
    }

    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }
}
