package com.example.flirtandroid.acitivities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.flirtandroid.Utils.User;
import com.example.flirtandroid.fragments.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditAccountActivity extends AppCompatActivity {
    private EditText firstName, lastName, dob;
    private Calendar myCalendar;
    private static final String PROGRESS_DIALOG = "EditAccountActivity.java";
    private User user; // assuming the User model class exists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        firstName = findViewById(R.id.fnameEditText);
        lastName = findViewById(R.id.lnameEditText);
        dob = findViewById(R.id.dobEditText);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditAccountActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                // Show the dialog
                datePickerDialog.show();
            }
        });

        // retrieve the user data from SharedPreferences or another storage mechanism
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (prefs.contains("accessToken")) {
            int userId = prefs.getInt("userId", -1); // -1 is the default value if the key doesn't exist
            String firstNameString = prefs.getString("firstName", "");
            String lastNameString = prefs.getString("lastName", "");
            String emailString = prefs.getString("email", "");
            String dobString = prefs.getString("dob", "");
            String accessTokenString = prefs.getString("accessToken", "");
            // create a new User object with the retrieved data
            user = new User();
            user.setId(userId);
            user.setFirstName(firstNameString);
            user.setLastName(lastNameString);
            user.setEmail(emailString);
            user.setDob(dobString);
            user.setAccessToken(accessTokenString);
            // set the retrieved data in the EditText fields
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            dob.setText(user.getDob());
        } else {
            // if the user is not logged in, show default text in the EditText fields
            firstName.setText("Guest");
            lastName.setText("User");
            dob.setText("01/01/2000");
        }

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button saveButton = findViewById(R.id.Save_Btn);
        saveButton.setAllCaps(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the user is logged in
                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                if (prefs.contains("guest")) {
                    // show a Snackbar message if the user is not logged in
                    WarningCustomSnackbar.showSnackbar(EditAccountActivity.this, "Please login to update your profile");
                    return;
                }
                // update the user object with the new data from the EditText fields
                user.setFirstName(firstName.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setDob(dob.getText().toString());
                // save the updated user data to SharedPreferences
                prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("firstName", user.getFirstName());
                editor.putString("lastName", user.getLastName());
                editor.putString("dob", user.getDob());
                editor.apply();
                showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        // update profile using API
                        String firstNameStr = firstName.getText().toString();
                        String lastNameStr = lastName.getText().toString();
                        String dobStr = dob.getText().toString();
                        updateProfileApi(firstNameStr, lastNameStr, dobStr);
                        // navigate back to the previous activity
                        EditAccountActivity.this.onBackPressed();
                        Toast.makeText(getApplicationContext(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, 1000); // 1 second delay
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Set your date format here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateProfileApi(String firstName, String lastName, String dob) {
        String url = GetApiParameters.getUrl();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url + AppConstants.UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // handle success response
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean status = jsonResponse.getBoolean("status");
                            if (status) {
                                Log.e("TAG", "onResponse updateProfileApi: " + response);
                                // profile updated successfully
                                String message = jsonResponse.getString("message");
                                Log.e("TAG", "onResponse message: " + message);
                                // update the User object with the new data
                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setDob(dob);
                                ProjectUtils.googleAnalyticEvent(EditAccountActivity.this, "profile_update");
                            } else {
                                // profile update failed
                                dismissProgress();
                                String message = jsonResponse.getString("message");
                                ApiErrorSnackBar.showSnackbar(EditAccountActivity.this, message);
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            ApiErrorSnackBar.showSnackbar(EditAccountActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // handle error
                        dismissProgress();
                        ApiErrorSnackBar.showSnackbar(EditAccountActivity.this, "Profile update failed: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("dob", dob);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String accessToken = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", "");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
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

}