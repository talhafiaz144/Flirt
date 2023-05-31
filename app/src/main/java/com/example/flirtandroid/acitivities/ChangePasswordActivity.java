package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flirtandroid.R;
import com.example.flirtandroid.SnackBar.ApiErrorSnackBar;
import com.example.flirtandroid.SnackBar.WarningCustomSnackbar;
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.fragments.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText currentPassword, newPassword, confirmPassword;
    private ImageView eyeIcon1, eyeIcon2, eyeIcon3;
    private static final String PROGRESS_DIALOG = "ChangePasswordActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        currentPassword = findViewById(R.id.current_pw);
        newPassword = findViewById(R.id.new_pw);
        confirmPassword = findViewById(R.id.confirm_password);
        ImageView backBtn = findViewById(R.id.back_btn);
        eyeIcon1 = findViewById(R.id.eyeIcon1);
        eyeIcon2 = findViewById(R.id.eyeIcon2);
        eyeIcon3 = findViewById(R.id.eyeIcon3);

        View.OnClickListener eyeIconClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEditText;
                ImageView eyeIcon;

                switch (v.getId()) {
                    case R.id.eyeIcon1:
                        passwordEditText = currentPassword;
                        eyeIcon = eyeIcon1;
                        break;
                    case R.id.eyeIcon2:
                        passwordEditText = newPassword;
                        eyeIcon = eyeIcon2;
                        break;
                    case R.id.eyeIcon3:
                        passwordEditText = confirmPassword;
                        eyeIcon = eyeIcon3;
                        break;
                    default:
                        return;
                }

                if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Hide the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.eye_icon);
                } else {
                    // Show the password
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.eye_icon_visible);
                }
                // Move the cursor to the end of the password text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        };

        eyeIcon1.setOnClickListener(eyeIconClickListener);
        eyeIcon2.setOnClickListener(eyeIconClickListener);
        eyeIcon3.setOnClickListener(eyeIconClickListener);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button saveButton = findViewById(R.id.Save_chg_Btn);
        saveButton.setAllCaps(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = currentPassword.getText().toString();
                String password = newPassword.getText().toString();
                String passwordConfirmation = confirmPassword.getText().toString();

                if (isValidUser(oldPassword, password, passwordConfirmation)) {
                    showProgress();
                    changePasswordApi();
                } else {
                    dismissProgress();
                    WarningCustomSnackbar.showSnackbar(ChangePasswordActivity.this, "Password field are required");
                }
            }
        });
    }

    private void changePasswordApi() {
        String oldPassword = currentPassword.getText().toString();
        String password = newPassword.getText().toString();
        String passwordConfirmation = confirmPassword.getText().toString();
        String url = GetApiParameters.getUrl();
        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        dismissProgress();
                        // Handle the API response here
                        Log.e("TAG", "onResponse changePasswordApi: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                ProjectUtils.googleAnalyticEvent(ChangePasswordActivity.this, "change_ap_password");
                                // Navigate back
                                onBackPressed();
                                // Show toast message
                                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                ProjectUtils.googleAnalyticEvent(ChangePasswordActivity.this, "change_password");
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                                dismissProgress();
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(ChangePasswordActivity.this, message);
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            ApiErrorSnackBar.showSnackbar(ChangePasswordActivity.this, "Error parsing response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                dismissProgress();
                // Show toast message
                Toast.makeText(ChangePasswordActivity.this, "Password check your internet connection", Toast.LENGTH_SHORT).show();
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
                params.put("old_password", oldPassword);
                params.put("password", password);
                params.put("password_confirmation", passwordConfirmation);
                return params;
            }
        };
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

    private boolean isValidUser(String current_pw, String new_pw, String c_new_pw) {
        // Perform basic null validation
        if (current_pw == null || current_pw.isEmpty() || new_pw == null || new_pw.isEmpty() || c_new_pw == null || c_new_pw.isEmpty()) {
            dismissProgress();
            return false;
        }
        return true;
    }
}