package com.example.flirtandroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.example.flirtandroid.acitivities.HomeActivity;
import com.example.flirtandroid.adapters.FavAdapter;
import com.example.flirtandroid.databinding.FragmentDashboardBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment implements FavAdapter.ItemClickListener {
    private FragmentDashboardBinding binding;
    private FavAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private LinearLayout progressBar;
    private ImageView backBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.my_recycler_view);
        emptyTextView = root.findViewById(R.id.empty_text_view);
        progressBar = root.findViewById(R.id.progressBar);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Create an empty ArrayList for the data initially
        List<PickupLineResponse.PickupLineData> pickupLineDataList = new ArrayList<>();
        adapter = new FavAdapter(pickupLineDataList, getContext());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        pickuplinesListApi();
        return root;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeItem(position);
                updateEmptyMessage();
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
                        Gson gson = new Gson();
                        PickupLineResponse pickupLineResponse = gson.fromJson(response, PickupLineResponse.class);
                        List<PickupLineResponse.PickupLineData> pickupLineDataList = pickupLineResponse.getData();
                        // Reverse the order of the list
                        Collections.reverse(pickupLineDataList);
                        // Filter the pickupLineDataList based on the favorite value
                        List<PickupLineResponse.PickupLineData> filteredList = new ArrayList<>();
                        for (PickupLineResponse.PickupLineData data : pickupLineDataList) {
                            if (data.getFavorite().equals("1")) {
                                filteredList.add(data);
                            }
                        }
                        adapter = new FavAdapter(filteredList, getContext());
                        adapter.setClickListener(DashboardFragment.this);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        if (adapter.getItemCount() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
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
}
