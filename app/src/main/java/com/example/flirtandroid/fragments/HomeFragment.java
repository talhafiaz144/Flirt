package com.example.flirtandroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.flirtandroid.Utils.PickupLineResponse;
import com.example.flirtandroid.Utils.ProjectUtils;
import com.example.flirtandroid.acitivities.CreateMatchActivity;
import com.example.flirtandroid.adapters.PickupLineAdapter;
import com.example.flirtandroid.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements PickupLineAdapter.ItemClickListener {
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private PickupLineAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.recyclerView);
        emptyTextView = root.findViewById(R.id.empty_text_view);
        progressBar = root.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addMatch = getView().findViewById(R.id.add_match_btn);
        addMatch.setAllCaps(false);
        addMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateMatchActivity.class);
                startActivity(intent);
            }
        });
        pickuplinesListApi();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateEmptyMessage() {
        if (adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.e("TAG", "onItemClick: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removePickupLine(position);
                Log.e("TAG", "removePickupLine 1: ");
                ProjectUtils.googleAnalyticEvent(getActivity(), "profile_update");
            }
        }, 1000);
    }

    private void pickuplinesListApi() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = GetApiParameters.getUrl();
        // Get the access token from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", "");
        Log.e("TAG", "pickuplinesListApi Token: " + accessToken);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + AppConstants.PICKUP_LINE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from the server
                        recyclerView.setVisibility(View.VISIBLE);
                        Log.e("TAG", "onResponse pickuplinesListApi: " + response);
                        try {
                            Gson gson = new Gson();
                            PickupLineResponse pickupLineResponse = gson.fromJson(response, PickupLineResponse.class);
                            List<PickupLineResponse.PickupLineData> pickupLineDataList = pickupLineResponse.getData();
                            // Reverse the order of the list
                            Collections.reverse(pickupLineDataList);
                            // Format the dates
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MM/dd", Locale.ENGLISH);
                            // Update the dates to "Today" or "Yesterday" if they are today's or yesterday's date
                            for (PickupLineResponse.PickupLineData pickupLineData : pickupLineDataList) {
                                String date = pickupLineData.getCreated_at();
                                if (isToday(date)) {
                                    pickupLineData.setCreated_at("Today");
                                } else if (isYesterday(date)) {
                                    pickupLineData.setCreated_at("Yesterday");
                                } else {
                                    try {
                                        Date parsedDate = inputFormat.parse(date);
                                        String formattedDate = outputFormat.format(parsedDate);
                                        pickupLineData.setCreated_at(formattedDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // Create a new instance of the adapter and pass the data to it
                            PickupLineAdapter adapter = new PickupLineAdapter(pickupLineDataList, getContext());
                            // Set the adapter for the RecyclerView
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            if (adapter.getItemCount() == 0) {
                                emptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                emptyTextView.setVisibility(View.GONE);
                            }
                        } catch (JsonSyntaxException e) {
                            // Handle JSON parsing error
                            progressBar.setVisibility(View.GONE);
                            ApiErrorSnackBar.showSnackbar(getActivity(), "Error parsing pickuplines response");
                            Log.e("TAG", "JsonSyntaxException: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response from the server
                progressBar.setVisibility(View.GONE);
                ApiErrorSnackBar.showSnackbar(getActivity(), "pickuplines request failed: " + error.getMessage());
                Log.e("TAG", "onErrorResponse pickuplinesListApi error: " + error.getMessage());
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

    private boolean isYesterday(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayDate = dateFormat.format(calendar.getTime());
        return date.equals(yesterdayDate);
    }

    private boolean isToday(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());
        return date.equals(todayDate);
    }

    private void removePickupLine(int position) {
        adapter.removeItem(position);
        updateEmptyMessage();
    }
}