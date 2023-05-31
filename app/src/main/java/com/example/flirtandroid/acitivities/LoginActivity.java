package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.flirtandroid.SnackBar.WarningCustomSnackbar;
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.Utils.User;
import com.example.flirtandroid.fragments.ProgressDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private RequestQueue requestQueue;
    private static final String PROGRESS_DIALOG = "LoginActivity.java";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(getApplicationContext());
        ImageView eyeIcon = findViewById(R.id.eyeIcon);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String webClientId = "704888684444-cmrj95be4u7thrk5rdta06jfb15tmjej.apps.googleusercontent.com";
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Initialize FirebaseAnalytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        LinearLayout socialLogin = findViewById(R.id.google_btn);
        socialLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                signIn();
                ProjectUtils.googleAnalyticEvent(LoginActivity.this, "social_login");
            }
        });

        emailEditText = findViewById(R.id.usernameEditText);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText.setText(sharedPreferences.getString("email", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));
        TextView signupBtn = findViewById(R.id.my_textview3);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignedUpActivity.class);
                startActivity(intent);
            }
        });
        TextView loginAsGuest = findViewById(R.id.login_as_guest);
        loginAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                guestLoginApi();
            }
        });
        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setAllCaps(false);
        // Create a RequestQueue object to handle your network requests
        requestQueue = Volley.newRequestQueue(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Save the email and password in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();
                Integer device = 0;
                if (isValidUser(email, password)) {
                    showProgress();
                    loginApi(email, password, device);
                } else {
                    dismissProgress();
                    WarningCustomSnackbar.showSnackbar(LoginActivity.this, "Email and password are required");
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                dismissProgress();
                // Google Sign-In failed, handle error
                if (e.getStatusCode() == CommonStatusCodes.CANCELED) {
                    // User canceled the sign-in process
                    Log.w("TAG", "Google sign in canceled");
                } else {
                    // Other error occurred
                    Log.w("TAG", "Google sign in failed", e);
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // Send the user's data to your API
                            setSocialLoginApi(user);
                        } else {
                            // Sign-in failed, handle error
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void setSocialLoginApi(FirebaseUser user) {
        // Use the user's data to make a POST request to your API
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = GetApiParameters.getUrl();
        int device = 0;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("provider_id", user.getUid());
            requestBody.put("provider", "google");
            requestBody.put("device", device);
            requestBody.put("email", user.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + AppConstants.SOCIAL_LOGIN, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgress();
                        // Handle the response from API
                        Log.e("TAG", "onResponse socialLoginApi: " + response);
                        // Parse the JSON response
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONObject userData = response.getJSONObject("user");
                                Log.e("TAG", "onResponse userData: " + userData);
                                User user = new User();
                                user.setId(userData.getInt("id"));
                                user.setFirstName(userData.getString("first_name"));
                                user.setLastName(userData.getString("last_name"));
                                user.setEmail(userData.getString("email"));
                                user.setDob(userData.getString("dob"));
                                user.setAccessToken(userData.getString("access_token"));
                                // Save the user data to SharedPreferences
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
                                // Start the HomeActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(LoginActivity.this, message);
                            }
                        } catch (JSONException e) {
                            ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // API call failed, handle error
                        Log.e("TAG", "API call failed: " + error.toString());
                        ApiErrorSnackBar.showSnackbar(LoginActivity.this, "API call failed: " + error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void guestLoginApi() {
        // Set up the API URL
        String url = GetApiParameters.getUrl();
        // Create a JsonObjectRequest object to make your API call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + AppConstants.GUEST_LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful response
                        Log.e("TAG", "onResponse guestLoginApi: " + response);
                        dismissProgress();
                        // parse the JSON response
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONObject userData = response.getJSONObject("user");
                                Log.e("TAG", "onResponse userData: " + userData);
                                User user = new User();
                                user.setId(userData.getInt("id"));
                                user.setFirstName(userData.getString("first_name"));
                                user.setLastName(userData.getString("last_name"));
                                user.setEmail(userData.getString("email"));
                                user.setDob(userData.getString("dob"));
                                user.setGuest(userData.getInt("guest"));
                                user.setAccessToken(userData.getString("access_token"));
                                // save the user data to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("userId", user.getId());
                                editor.putString("firstName", user.getFirstName());
                                editor.putString("lastName", user.getLastName());
                                editor.putString("email", user.getEmail());
                                editor.putString("dob", user.getDob());
                                editor.putInt("guest", user.getGuest());
                                editor.putString("accessToken", user.getAccessToken());
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                                // start the HomeActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                ProjectUtils.googleAnalyticEvent(LoginActivity.this, "guest_login");
                            } else {
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(LoginActivity.this, message);
                            }
                        } catch (JSONException e) {
                            ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle error response
                        ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Guest login failed: " + error.getMessage());
                    }
                });
        // Add the JsonObjectRequest to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private void loginApi(String email, String password, Integer device) {
        // Set up the API URL and request parameters
        String url = GetApiParameters.getUrl();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("device", device);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create a JsonObjectRequest object to make your API call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + AppConstants.LOGIN, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Log.e("TAG", "onResponse loginApi: " + response);
                                JSONObject userData = response.getJSONObject("user");
                                User user = new User();
                                user.setId(userData.getInt("id"));
                                user.setFirstName(userData.getString("first_name"));
                                user.setLastName(userData.getString("last_name"));
                                user.setEmail(userData.getString("email"));
                                user.setDob(userData.getString("dob"));
                                user.setAccessToken(userData.getString("access_token"));
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
                                // Log the login event using FirebaseAnalytics
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.METHOD, email);  // Specify the login method used
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                ProjectUtils.googleAnalyticEvent(LoginActivity.this, "user_login");
                            } else {
                                dismissProgress();
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Login failed: " + message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismissProgress();
                            ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Login failed: " + e.getMessage());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle error response
                        ApiErrorSnackBar.showSnackbar(LoginActivity.this, "Login failed: " + error.getMessage());
                    }
                });
        // Add the JsonObjectRequest to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isValidUser(String email, String password) {
        // Perform basic null validation
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            dismissProgress();
            return false;
        }
        return true;
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