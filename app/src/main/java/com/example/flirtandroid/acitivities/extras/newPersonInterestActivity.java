package com.example.flirtandroid.acitivities.extras;

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
import com.example.flirtandroid.acitivities.FeedBackActivity;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class newPersonInterestActivity extends AppCompatActivity {
    private boolean isSaved = false; // initial state
    private PickupLine pickupLine;
    private static final String PROGRESS_DIALOG = "newPersonInterestActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person_interest);
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
            isSaved = !isSaved;
            int favorite = isSaved ? 1 : 0;
            changeFavStatusApi(favorite);
            imageView.setBackgroundResource(isSaved ? R.drawable.s_saved : R.drawable.saved);
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
                Intent intent = new Intent(newPersonInterestActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });

        TextView personName = findViewById(R.id.presons_name);
        String newText = getIntent().getStringExtra("text");
        personName.setText(newText);
        showProgress();

        // Find the ChipGroup view in the layout
        ChipGroup chipGroup = findViewById(R.id.chip_group);

        // Retrieving the pickupLine object from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String pickupLineJson = sharedPreferences.getString("pickupLine", null);
        if (pickupLineJson != null) {
            Gson gson = new Gson();
            this.pickupLine = gson.fromJson(pickupLineJson, PickupLine.class);
            // Use the pickupLine object as needed
            List<String> words = this.pickupLine.getWords();
            Log.d("TAG", "onCreate words 1: " + words);
            // Do something with the words
            if (words != null) {
                // Loop through the words and create a chip for each one
                for (String word : words) {
                    Chip chip = new Chip(this);
                    chip.setText(word);
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
            }
        }

        List<String> interests = pickupLine.getWords(); // Get the interests from pickupLine object
        newGeneratePickupLines(newText, interests);

        Button regenerateBtn = findViewById(R.id.regenerate_btn);
        regenerateBtn.setAllCaps(false);
        regenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                newGeneratePickupLines(newText, interests);
            }
        });
    }

    private List<String> getInterestsFromJson(JSONObject pickupLineData) throws JSONException {
        List<String> interests = new ArrayList<>();
        JSONArray interestsArray = pickupLineData.getJSONArray("interest");
        for (int i = 0; i < interestsArray.length(); i++) {
            String interest = interestsArray.getString(i);
            interests.add(interest);
        }
        return interests;
    }

    private void newGeneratePickupLines(String person_Name, List<String> interests) {
        int device = 1;
        Log.d("TAG", "onClick: " + interests);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(newPersonInterestActivity.this);
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
                        // Handle the API response
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                JSONObject pickupLineData = jsonResponse.getJSONObject("data");
                                pickupLine.setInterests(getInterestsFromJson(pickupLineData));
                                Log.e("TAG", "onResponse new_generatePickupLinesApi: " + response);
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
                                ProjectUtils.googleAnalyticEvent(newPersonInterestActivity.this, "generate_pickup_line");
                            } else {
                                dismissProgress();
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(newPersonInterestActivity.this, "Please check you internet connection  "+message);
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            e.printStackTrace();
                            Toast.makeText(newPersonInterestActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle the error response
                        Toast.makeText(newPersonInterestActivity.this, "API Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Set the request parameters, including the person_name and interests
                Map<String, String> params = new HashMap<>();
                params.put("person_name", person_Name);
                for (int i = 0; i < interests.size(); i++) {
                    params.put("interest[" + i + "]", interests.get(i));
                }
                params.put("device", String.valueOf(device));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                // Set the request headers, including the access token
                Map<String, String> headers = new HashMap<>();
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
                        // Handle the API response here
                        dismissProgress();
                        Log.e("TAG", "onResponse changeFavStatusApi: " + response);
                        ProjectUtils.googleAnalyticEvent(newPersonInterestActivity.this, "add_to_fav");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle the API error here
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