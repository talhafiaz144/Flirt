package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.ApiErrorSnackBar;
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.fragments.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class VerifyCodeActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private TextView timerText;
    private LinearLayout regenerateBtn;
    private RequestQueue requestQueue;
    private static final String PROGRESS_DIALOG = "VerifyCodeActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(this);
        // Retrieve the saved response message from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String responseMessage = sharedPreferences.getString("responseMessage", "");
        // Show the response message in a Toast
        Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show();

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        enterOTPSet();
        Button doneBtn = findViewById(R.id.verify_btn);
        doneBtn.setAllCaps(false);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                verifyEmailOTPApi();
            }
        });

        // Find the timer and regenerate button views
        timerText = findViewById(R.id.timer_text);
        regenerateBtn = findViewById(R.id.regenerate_btn);
        // Start the timer for 1 minute
        startTimer(30);
        // Set the onClickListener for the regenerate button
        regenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                // Get the email from the previous activity
                Intent intent = getIntent();
                String email = intent.getStringExtra("email");
                // Regenerate the code
                regenerateCodeApi(email);
                // Hide the regenerate button
                regenerateBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void regenerateCodeApi(String email) {
        // Set up the API URL and request parameters
        String url = GetApiParameters.getUrl();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create a JsonObjectRequest object to make your API call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + AppConstants.REGENERATE_OTP, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful response
                        Log.e("TAG", "onResponse regenerateCodeApi: " + response);
                        dismissProgress();
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                // Reset the timer
                                startTimer(30);
                                // Show the response message in a Toast
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                ProjectUtils.googleAnalyticEvent(VerifyCodeActivity.this, "regenerate_OTP");
                            } else {
                                dismissProgress();
                                message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(VerifyCodeActivity.this, "Regenerate OTP failed: " + message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        dismissProgress();
                        ApiErrorSnackBar.showSnackbar(VerifyCodeActivity.this, "Regenerate OTP failed: " + error.getMessage());
                    }
                });
        // Add the JsonObjectRequest to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    // Method to start the timer for the given duration in seconds
    private void startTimer(int duration) {
        // Cancel any existing timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Create a new timer with the given duration
        countDownTimer = new CountDownTimer(duration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer text with the remaining time
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                timerText.setText(String.format("(%02d:%02d)", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Show the regenerate button when the timer is finished
                regenerateBtn.setVisibility(View.VISIBLE);
                timerText.setText("(00:00)");
            }
        };
        // Start the timer
        countDownTimer.start();
    }

    private void verifyEmailOTPApi() {
        EditText text_edit1 = findViewById(R.id.text_edit1);
        EditText text_edit2 = findViewById(R.id.text_edit2);
        EditText text_edit3 = findViewById(R.id.text_edit3);
        EditText text_edit4 = findViewById(R.id.text_edit4);
        // Get the OTP code from the EditText fields
        String otpCode = text_edit1.getText().toString()
                + text_edit2.getText().toString()
                + text_edit3.getText().toString()
                + text_edit4.getText().toString();
        // Get the email from the previous activity
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        // Create the JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("otp_code", otpCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set up the API URL and request parameters
        String url = GetApiParameters.getUrl();
        // Make the API request using Volley
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url + AppConstants.VERIFY_OTP,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse verifyOTPApi: " + response);
                        dismissProgress();
                        try {
                            boolean status = response.getBoolean("status");
                            if (!status) {
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(VerifyCodeActivity.this, "Error: " + message);
                            } else {
                                Intent intent = new Intent(VerifyCodeActivity.this, RegisterActivity.class);
                                intent.putExtra("text", email);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ApiErrorSnackBar.showSnackbar(VerifyCodeActivity.this, "Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        ApiErrorSnackBar.showSnackbar(VerifyCodeActivity.this, "Error: " + error.getMessage());
                    }
                });
        queue.add(request);
    }

    private void enterOTPSet() {
        EditText text_edit1 = findViewById(R.id.text_edit1);
        EditText text_edit2 = findViewById(R.id.text_edit2);
        EditText text_edit3 = findViewById(R.id.text_edit3);
        EditText text_edit4 = findViewById(R.id.text_edit4);
        text_edit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    text_edit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        text_edit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    text_edit3.requestFocus();
                } else if (s.length() == 0) {
                    text_edit1.requestFocus();
                    text_edit1.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        text_edit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    text_edit4.requestFocus();
                } else if (s.length() == 0) {
                    text_edit2.requestFocus();
                    text_edit2.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        text_edit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    text_edit3.requestFocus();
                    text_edit3.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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