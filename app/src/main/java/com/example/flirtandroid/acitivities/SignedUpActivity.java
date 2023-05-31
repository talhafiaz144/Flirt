package com.example.flirtandroid.acitivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class SignedUpActivity extends AppCompatActivity {
    private EditText emailEditText;
    private RequestQueue requestQueue;
    private static final String PROGRESS_DIALOG = "SignedUpActivity.java";
    SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_up);
        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(this);
        String webClientId = "704888684444-cmrj95be4u7thrk5rdta06jfb15tmjej.apps.googleusercontent.com";
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Initialize FirebaseAnalytics
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        emailEditText = findViewById(R.id.email_EditText);

        TextView loginAs_Guest = findViewById(R.id.login_as_guest);
        loginAs_Guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                guestLoginApi();
            }
        });

        LinearLayout socialLogin = findViewById(R.id.google_btn);
        socialLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                signIn();
            }
        });

        Button sendCode = findViewById(R.id.button_first);
        sendCode.setAllCaps(false);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                if (isValidUser(email)) {
                    showProgress();
                    generateCodeApi(email);
                } else {
                    dismissProgress();
                    WarningCustomSnackbar.showSnackbar(SignedUpActivity.this, "Email is required");
                }
            }
        });

        TextView alreadyHavAccount = findViewById(R.id.already_acc);
        alreadyHavAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignedUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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
                                editor.apply();
                                // start the HomeActivity
                                Intent intent = new Intent(SignedUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                                ProjectUtils.googleAnalyticEvent(SignedUpActivity.this, "guest_login");
                            } else {
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, message);
                            }
                        } catch (JSONException e) {
                            ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // Handle error response
                        ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "Guest login failed: " + error.getMessage());
                    }
                });
        // Add the JsonObjectRequest to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                            socialLoginApi(user);
                        } else {
                            // Sign-in failed, handle error
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void socialLoginApi(FirebaseUser user) {
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
                        // Handle the response from your API
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
                                Intent intent = new Intent(SignedUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                String message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, message);
                            }
                        } catch (JSONException e) {
                            ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "Error parsing response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        // API call failed, handle error
                        Log.e("TAG", "API call failed: " + error.toString());
                        ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "API call failed: " + error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void generateCodeApi(String email) {
        // Set up the API URL and request parameters
        String url = GetApiParameters.getUrl();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create a JsonObjectRequest object to make your API call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + AppConstants.GENERATE_OTP, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful response
                        Log.e("TAG", "onResponse generateCodeApi: " + response);
                        dismissProgress();
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            if (status) {
                                // Save the message in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("responseMessage", message);
                                editor.apply();
                                Intent intent = new Intent(SignedUpActivity.this, VerifyCodeActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                ProjectUtils.googleAnalyticEvent(SignedUpActivity.this, "signup_new_user");
                            } else {
                                dismissProgress();
                                message = response.getString("message");
                                ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "Login failed: " + message);
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
                        ApiErrorSnackBar.showSnackbar(SignedUpActivity.this, "Login failed: " + error.getMessage());
                    }
                });
        // Add the JsonObjectRequest to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private boolean isValidUser(String email) {
        // Perform basic null validation
        if (email == null || email.isEmpty()) {
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