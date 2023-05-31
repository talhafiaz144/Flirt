package com.example.flirtandroid.acitivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.Utils.User;
import com.example.flirtandroid.fragments.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Calendar myCalendar;
    private EditText firstName, lastName, dob, email, password, confirmPassword;
    private static final String PROGRESS_DIALOG = "RegisterActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstName = findViewById(R.id.f_name);
        lastName = findViewById(R.id.l_name);
        dob = findViewById(R.id.dob);
        myCalendar = Calendar.getInstance();

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // Update the dob EditText with the selected date
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };

                // Create a new DatePickerDialog with the current date as the default selection
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                // Show the dialog
                datePickerDialog.show();
            }
        });

        email = findViewById(R.id.email_Edit_Text);
        password = findViewById(R.id.password_Edit_Text);
        confirmPassword = findViewById(R.id.c_password);
        Button doneBtn = findViewById(R.id.done_btn);
        doneBtn.setAllCaps(false);
        Intent intent = getIntent();
        String emailText = intent.getStringExtra("text");
        Log.e("TAG", "onCreate emailText: "+emailText);
        if (emailText != null) {
            email.setText(emailText);
            email.setEnabled(false);
        }
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldsValid()) {
                    showProgress();
                    String firstNameStr = firstName.getText().toString();
                    String lastNameStr = lastName.getText().toString();
                    String dobStr = dob.getText().toString();
                    String emailStr = email.getText().toString();
                    String passwordStr = password.getText().toString();
                    String confirmPasswordStr = confirmPassword.getText().toString();
                    Integer deviceStr = 0; // set the device to Android
                    registerUserApi(firstNameStr, lastNameStr, dobStr, emailStr, passwordStr, confirmPasswordStr, deviceStr);
                }
                dismissProgress();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Set your date format here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void registerUserApi(String firstName, String lastName, String dob, String email, String password, String confirmPassword, Integer device) {
        String url = GetApiParameters.getUrl();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url + AppConstants.REGISTER,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        // parse the JSON response
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                JSONObject userData = jsonResponse.getJSONObject("user");
                                Log.e("TAG", "onResponse userData: "+userData );
                                User user = new User();
                                user.setId(userData.getInt("id"));
                                user.setFirstName(userData.getString("first_name"));
                                user.setLastName(userData.getString("last_name"));
                                user.setEmail(userData.getString("email"));
                                user.setDob(userData.getString("dob"));
                                user.setAccessToken(userData.getString("access_token"));
                                // save the user data to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("userId", user.getId());
                                editor.putString("firstName", user.getFirstName());
                                editor.putString("lastName", user.getLastName());
                                editor.putString("email", user.getEmail());
                                editor.putString("dob", user.getDob());
                                editor.putString("accessToken", user.getAccessToken());
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                                // start the HomeActivity
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                ProjectUtils.googleAnalyticEvent(RegisterActivity.this, "register_new_user");
                            } else {
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(RegisterActivity.this, message);
                            }
                        } catch (JSONException e) {
                            ApiErrorSnackBar.showSnackbar(RegisterActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // handle error
                        ApiErrorSnackBar.showSnackbar(RegisterActivity.this, "Login failed: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("dob", dob);
                params.put("email", email);
                params.put("password", password);
                params.put("password_confirmation", confirmPassword);
                params.put("device", String.valueOf(device));
                return params;
            }
        };
        queue.add(postRequest);
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
    private boolean isFieldsValid() {
        if (TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError("Please enter your first name");
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError("Please enter your last name");
            return false;
        }
        if (TextUtils.isEmpty(dob.getText().toString())) {
            dob.setError("Please enter your date of birth");
            return false;
        }
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter your email");
            return false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Please enter your password");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
            confirmPassword.setError("Please confirm your password");
            return false;
        }
        return true;
    }
}