
package com.example.flirtandroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.example.flirtandroid.Utils.User;
import com.example.flirtandroid.acitivities.EditAccountActivity;
import com.example.flirtandroid.acitivities.HomeActivity;
import com.example.flirtandroid.acitivities.ManageAccountActivity;
import com.example.flirtandroid.acitivities.PrivacyActivity;
import com.example.flirtandroid.acitivities.SubscriptionAcitivty;
import com.example.flirtandroid.databinding.FragmentNotificationsBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;

public class NotificationsFragment extends Fragment {

    private LinearLayout editAccount, subscriptionBtn, privacyBtn, manageAccBtn;
    private TextView firstName, lastName;
    private User user;
    private ImageView backBtn;
    private FragmentNotificationsBinding binding;
    private TagContainerLayout mTagContainerLayout1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        backBtn = root.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the reference to the hosting activity (HomeActivity) and call the navigateToHomeTab() method
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.navigateToHomeTab();
                }
            }
        });

        firstName = root.findViewById(R.id.title_name);
        lastName = root.findViewById(R.id.title_l_name);

        editAccount = root.findViewById(R.id.edit_account);
        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileInfoApi();
                Intent intent = new Intent(getActivity(), EditAccountActivity.class);
                startActivity(intent);
            }
        });

        privacyBtn = root.findViewById(R.id.privacy_btn);
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyActivity.class);
                startActivity(intent);
            }
        });

        manageAccBtn = root.findViewById(R.id.manage_acc_btn);
        manageAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
                startActivity(intent);
            }
        });

        subscriptionBtn = root.findViewById(R.id.subcription_btn);
        subscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubscriptionAcitivty.class);
                startActivity(intent);
            }
        });
        return root;
    }

    private void profileInfoApi() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = GetApiParameters.getUrl();

        // Get the access token from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", "");

        Log.e("TAG", "profileInfoApi Token: " + accessToken);

        // Request a JSON object response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + AppConstants.PROFILE_INFO, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse profileInfoApi: "+response );

                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response from the server
                ApiErrorSnackBar.showSnackbar(getActivity(), "Error fetching profile info: " + error.getMessage());
                Log.e("TAG", "onErrorResponse profileInfoApi error: " + error.getMessage());
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
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        // retrieve the user data from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1); // -1 is the default value if the key doesn't exist

        if (userId != -1) { // user is logged in
            String firstNameString = prefs.getString("firstName", "");
            String lastNameString = prefs.getString("lastName", "");

            // create a new User object with the retrieved data
            user = new User();
            user.setId(userId);
            user.setFirstName(firstNameString);
            user.setLastName(lastNameString);

            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
        } else { // user is not logged in
            firstName.setText("Guest");
            lastName.setText("User");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}