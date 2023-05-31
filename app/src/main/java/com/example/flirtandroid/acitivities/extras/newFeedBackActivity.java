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
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class newFeedBackActivity extends AppCompatActivity {

    private boolean isSaved = false; // initial state
    private static final String PROGRESS_DIALOG = "FeedBackActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feed_back);
        Intent intent = getIntent();
        String line = intent.getStringExtra("line");
        EditText feedbackEditText = findViewById(R.id.feedback_txt);
        String feedback = feedbackEditText.getText().toString();
        int device = 1;
        TextView copyText = findViewById(R.id.thomas_j_pe1);
        copyText.setText(line);
        LinearLayout copyTextBtn = findViewById(R.id.copy_txt1);
        LinearLayout linearLayout = findViewById(R.id.fav_icon1);
        ImageView imageView = findViewById(R.id.fav_img1);
        ImageView BackBtn = findViewById(R.id.back_btn1);
        Button regenerateBtn = findViewById(R.id.regenerate_btn2);
        regenerateBtn.setAllCaps(false);

        regenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                regeneratePickupLineApi(feedback, device);
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linearLayout.setOnClickListener(v -> {
            showProgress();
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
    }

    private void regeneratePickupLineApi(String feedback, int device) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        int lineId = intent.getIntExtra("lineid", 0); // 0 is the default value if the key is not found
        String personName = intent.getStringExtra("name");
        ArrayList<String> interestString = intent.getStringArrayListExtra("interests");
        // Specify the URL of the API endpoint
        String url = GetApiParameters.getUrl();
        // Create a new JsonObjectRequest object to make the POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.REGENERATE_PICKUP_LINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        // Handle the API response here
                        Log.e("TAG", "onResponse regeneratePickupLineApi: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                JSONObject pickupLineData = jsonResponse.getJSONObject("data");
                                Log.e("TAG", "onResponse generatePickupLinesApi: " + response);
                                PickupLine pickupLine = new PickupLine();
                                pickupLine.setLine(pickupLineData.getString("line"));
                                pickupLine.setId(pickupLineData.getInt("id"));
                                pickupLine.setName(pickupLineData.getString("name"));
                                pickupLine.setInterests(Collections.singletonList(pickupLineData.getString("interest")));
                                // save the user data to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("line", pickupLine.getLine());
                                editor.putInt("id", pickupLine.getId());
                                editor.putString("name", pickupLine.getName());
                                editor.putString("interest", String.valueOf(pickupLine.getInterests()));
                                editor.apply();
                                // Get the pickup line from the PickupLine object
                                String line = pickupLine.getLine();
                                // Set the pickup line in the TextView
                                TextView pickupLineTextView = findViewById(R.id.thomas_j_pe1);
                                pickupLineTextView.setText(line);
                                ProjectUtils.googleAnalyticEvent(newFeedBackActivity.this, "share_feedback");
                            } else {
                                dismissProgress();
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(newFeedBackActivity.this, message);
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            ApiErrorSnackBar.showSnackbar(newFeedBackActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response here
                        dismissProgress();
                        Log.e("API Error", error.toString());
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
                params.put("person_name", personName);
                for (int i = 0; i < interestString.size(); i++) {
                    params.put("interest[" + i + "]", interestString.get(i));
                }
                params.put("line_id", String.valueOf(lineId));
                params.put("feedback", feedback);
                params.put("device", String.valueOf(device));
                return params;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                int socketTimeout = 5000; // 5 seconds
                return new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        };
        // Add the JsonObjectRequest to the RequestQueue
        queue.add(stringRequest);
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
                        ProjectUtils.googleAnalyticEvent(newFeedBackActivity.this, "add_to_fav");
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
}