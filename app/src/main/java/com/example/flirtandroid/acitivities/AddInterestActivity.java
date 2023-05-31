package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flirtandroid.R;
import com.example.flirtandroid.Utils.PickupLine;
import com.example.flirtandroid.adapters.AddInterestAdapter;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddInterestActivity extends AppCompatActivity {
    private List<String> list;
    private RecyclerView recyclerView;
    private AddInterestAdapter adapter;
    private static final String PROGRESS_DIALOG = "AddInterestActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        recyclerView = findViewById(R.id.recycler_view);
        EditText editText = findViewById(R.id.write_text);
        EditText editText2 = findViewById(R.id.write_text2);
        LinearLayout interestEditText = findViewById(R.id.interest_editText);
        ImageView addInterestIcon = findViewById(R.id.add_interest_icon);
        Button generateBtn = findViewById(R.id.generate_btn);
        generateBtn.setAllCaps(false);
        ImageView backBtn = findViewById(R.id.back_btn);
        generateBtn.setEnabled(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list = new ArrayList<>();
        adapter = new AddInterestAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnDragListener(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // Get the input method manager
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String item = v.getText().toString().trim();
                    if (!item.isEmpty()) {
                        list.add(item);
                        adapter.notifyItemInserted(list.size() - 1);
                        v.setText("");
                        int itemCount = recyclerView.getAdapter().getItemCount();
                        generateBtn.setEnabled(itemCount > 1);
                    }

                    // Get the corresponding interestEditText based on the view's ID
                    LinearLayout interestEditText = null;
                    switch (v.getId()) {
                        case R.id.write_text:
                            interestEditText = findViewById(R.id.interest_editText);
                            break;
                        case R.id.write_text2:
                            interestEditText = findViewById(R.id.interest_editText2);
                            break;
                    }
                    if (interestEditText != null) {
                        interestEditText.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        };

       // Set the common editor action listener to each EditText
        editText.setOnEditorActionListener(editorActionListener);
        editText2.setOnEditorActionListener(editorActionListener);

        TextView personName = findViewById(R.id.persons_name);
        String newText = getIntent().getStringExtra("text");
        personName.setText(newText);
        addInterestIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestEditText.setVisibility(View.VISIBLE);
            }
        });
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                // Get the chip tag text from the previous activity
                EditText chipTagEditText = findViewById(R.id.write_text);
                String chipTag = chipTagEditText.getText().toString().trim();
                // Delay hiding the progress bar by half second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        Intent intent = new Intent(AddInterestActivity.this, PersonInterestActivity.class);
                        intent.putStringArrayListExtra("interests", new ArrayList<>(list));
                        intent.putExtra("text", newText);
                        intent.putExtra("chipTag", chipTag);
                        startActivity(intent);
                    }
                }, 700);
            }
        });

        // Retrieve the pickupLine string from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pickupLine")) {
            String pickupLineJson = intent.getStringExtra("pickupLine");
            Gson gson = new Gson();
            PickupLine pickupLine = gson.fromJson(pickupLineJson, PickupLine.class);
            if (pickupLine != null && pickupLine.getWords() != null) {
                List<String> words = pickupLine.getWords();
                adapter = new AddInterestAdapter(words);
                recyclerView.setAdapter(adapter);
                adapter.setOnDragListener(recyclerView);
                generateBtn.setEnabled(true);
                addInterestIcon.setVisibility(View.GONE);
            }
        }
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
