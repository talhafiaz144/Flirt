package com.example.flirtandroid.acitivities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

import java.util.HashMap;
import java.util.Map;

public class ManageAccountActivity extends AppCompatActivity {

    private LinearLayout forgetMeBtn, rememberMeBtn, deleteAccountBtn;
    private static final String PROGRESS_DIALOG = "ManageAccountActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        LinearLayout logoutBtn = findViewById(R.id.logout_btn);
        forgetMeBtn = findViewById(R.id.forget_me_btn);
        rememberMeBtn = findViewById(R.id.remember_me_btn);
        deleteAccountBtn = findViewById(R.id.delelte_acc);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetMeBtn.setVisibility(View.VISIBLE);
                rememberMeBtn.setVisibility(View.VISIBLE);
                deleteAccountBtn.setVisibility(View.VISIBLE);
            }
        });
        forgetMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog(ManageAccountActivity.this);
            }
        });
        rememberMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberMeDialog(ManageAccountActivity.this);
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccDialog(ManageAccountActivity.this);
            }
        });
    }

    public void rememberMeDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.logout_dialog);
            RelativeLayout yes_btn = dialog.findViewById(R.id.yes_btn);
            RelativeLayout no_btn = dialog.findViewById(R.id.no_btn);
            CheckBox checkBox = dialog.findViewById(R.id.checkbox);
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();
                    // Start LoginActivity and clear all previous activities
                    Intent intent = new Intent(ManageAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                if (dialog.isShowing()) {
                    return;
                }
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        removeloginPrefs();
                    }
                }
            });
        }
    }

    public void deleteAccDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.delete_acc_dialog);
            RelativeLayout yes_btn = dialog.findViewById(R.id.yes_btn);
            RelativeLayout no_btn = dialog.findViewById(R.id.no_btn);
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if accessToken exists in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    if (!prefs.contains("guest")) {
                        deleteAccountApi();
                    } else {
                        dialog.dismiss();
                        // Show Snackbar indicating that the accessToken is missing
                        WarningCustomSnackbar.showSnackbar(ManageAccountActivity.this, "Guest users cannot delete their account");
                    }
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                if (dialog.isShowing()) {
                    return;
                }
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logoutDialog(Activity activity) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.logout_dialog2);
            RelativeLayout yes_btn = dialog.findViewById(R.id.yes_btn);
            RelativeLayout no_btn = dialog.findViewById(R.id.no_btn);
            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();
                    removeloginPrefs();
                    // Start LoginActivity and clear all previous activities
                    Intent intent = new Intent(ManageAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    ProjectUtils.googleAnalyticEvent(ManageAccountActivity.this, "logout_user");
                }
            });
            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                if (dialog.isShowing()) {
                    return;
                }
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void removeloginPrefs() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.clear();
            editor.apply();
        } else {
            // Handle the case where SharedPreferences object is null
            Log.e("TAG", "removeloginPrefs is null: " + prefs);
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
    private void deleteAccountApi() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = GetApiParameters.getUrl();
        // Get the access token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", "");
        showProgress();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + AppConstants.DELETE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        // Handle response from the server
                        Log.e("TAG", "onResponse deleteAccountApi: " + response);
                        Toast.makeText(getApplicationContext(), "Delete account request submit successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManageAccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        ProjectUtils.googleAnalyticEvent(ManageAccountActivity.this, "delete_acc_req");
                        removeloginPrefs();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response from the server
                dismissProgress();
                ApiErrorSnackBar.showSnackbar(ManageAccountActivity.this, "deletion account request failed: " + error.getMessage());
                Log.e("TAG", "onErrorResponse deleteApi error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}