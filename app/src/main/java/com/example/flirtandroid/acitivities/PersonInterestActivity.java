package com.example.flirtandroid.acitivities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.ApiErrorSnackBar;
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.PickupLine;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PersonInterestActivity extends AppCompatActivity {
    private boolean isSaved = false; // initial state
    private static final String PROGRESS_DIALOG = "PersonInterestActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_interest);
        TextView copyText = findViewById(R.id.thomas_j_pe_copy);
        LinearLayout copyTextBtn = findViewById(R.id.copy_txt);
        LinearLayout linearLayout = findViewById(R.id.fav_icon);
        ImageView imageView = findViewById(R.id.fav_img);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linearLayout.setOnClickListener(v -> {
            showProgress();
            isSaved = !isSaved;
            imageView.setBackgroundResource(isSaved ? R.drawable.s_saved : R.drawable.saved);
            int favorite = isSaved ? 1 : 0;
            changeFavStatusApi(favorite);
            Toast.makeText(getApplicationContext(), isSaved ? "Save Successfully" : "Removed Successfully", Toast.LENGTH_SHORT).show();
        });

        copyTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick: copyText");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", copyText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied", Toast.LENGTH_SHORT).show();
            }
        });

        TextView feedBackBtn = findViewById(R.id.feedback_btn);
        feedBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonInterestActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the interests and chip tag from the Intent
        Intent intent = getIntent();
        ArrayList<String> interests = intent.getStringArrayListExtra("interests");

        TextView personName = findViewById(R.id.presons_name);
        String newText = getIntent().getStringExtra("text");
        personName.setText(newText);

        // Find the ChipGroup view in the layout
        ChipGroup chipGroup = findViewById(R.id.chip_group);
        // Loop through the interests and create a chip for each one
        for (String interest : interests) {
            Chip chip = new Chip(this);
            chip.setText(interest);
            chip.setChipBackgroundColorResource(R.color.tag);
            chip.setCloseIconResource(R.drawable.white_cross);
            chip.setCloseIconTintResource(R.color.white);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setCloseIconVisible(true);
            chip.setCloseIconSize(getResources().getDimensionPixelSize(R.dimen.close_icon_size));
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
        showProgress();
        Integer device = 1;
        generatePickupLinesApi(newText, interests, device);

        Button regenerateBtn = findViewById(R.id.regenerate_btn);
        regenerateBtn.setAllCaps(false);
        regenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                generatePickupLinesApi(newText, interests, device);
            }
        });
    }

    private void generatePickupLinesApi(String personName, ArrayList<String> interests, Integer device) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Get the access token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", "");
        // Specify the URL of the API endpoint
        String url = GetApiParameters.getUrl();
        // Create a new StringRequest object to make a POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.GENERATE_PICKUP_LINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                JSONObject pickupLineData = jsonResponse.getJSONObject("data");
                                Log.e("TAG", "onResponse userData: " + pickupLineData);
                                Log.e("TAG", "onResponse generatePickupLinesApi: " + response);
                                PickupLine pickupLine = new PickupLine();
                                pickupLine.setLine(pickupLineData.getString("line"));
                                pickupLine.setId(pickupLineData.getInt("id"));
                                pickupLine.setName(pickupLineData.getString("name"));
                                pickupLine.setInterests(Collections.singletonList(pickupLineData.getString("interest")));
                                pickupLine.setFavorite(pickupLineData.getInt("favorite"));
                                // save the user data to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("line", pickupLine.getLine());
                                editor.putInt("id", pickupLine.getId());
                                editor.putString("name", pickupLine.getName());
                                editor.putString("interest", String.valueOf(pickupLine.getInterests()));
                                editor.putInt("favorite", pickupLine.getFavorite());
                                editor.apply();
                                // Get the pickup line from the PickupLine object
                                String line = pickupLine.getLine();
                                // Set the pickup line in the TextView
                                TextView pickupLineTextView = findViewById(R.id.thomas_j_pe_copy);
                                pickupLineTextView.setText(line);
                                ProjectUtils.googleAnalyticEvent(PersonInterestActivity.this, "generate_pickup_line");
                            } else {
                                dismissProgress();
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(PersonInterestActivity.this, "Please check you internet connection  "+message);
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            ApiErrorSnackBar.showSnackbar(PersonInterestActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle error response here
                        Log.e("API Error", error.toString());
                        ApiErrorSnackBar.showSnackbar(PersonInterestActivity.this, "Error: "+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Set the request parameters, including the person_name and interests
                Map<String, String> params = new HashMap<>();
                params.put("person_name", personName);
                for (int i = 0; i < interests.size(); i++) {
                    params.put("interest[" + i + "]", interests.get(i));
                }
                params.put("device", String.valueOf(device));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                int socketTimeout = 5000; // 5 seconds
                return new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        };
        // Add the StringRequest object to the RequestQueue
        queue.add(stringRequest);
    }

    private void changeFavStatusApi(int favorite) {
        // Get the access token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int lineId = prefs.getInt("id", 0); // 0 is the default value if the key is not found
        Log.e("TAG", "changeFavStatusApi: " + lineId);
        String url = GetApiParameters.getUrl();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.CHANGE_FAV_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the API response
                        dismissProgress();
                        Log.e("TAG", "onResponse changeFavStatusApi: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String accessToken = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", "");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("line_id", String.valueOf(lineId));
                params.put("favorite", String.valueOf(favorite));
                return params;
            }
        };
        requestQueue.add(stringRequest);
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

