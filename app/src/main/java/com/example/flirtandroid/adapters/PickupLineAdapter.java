package com.example.flirtandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flirtandroid.R;
import com.example.flirtandroid.Utils.AppConstants;
import com.example.flirtandroid.Utils.GetApiParameters;
import com.example.flirtandroid.Utils.PickupLineResponse;
import com.example.flirtandroid.acitivities.extras.DetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupLineAdapter extends RecyclerView.Adapter<PickupLineAdapter.PickupLineViewHolder> {
    private List<PickupLineResponse.PickupLineData> pickupLineDataList;
    private Context context;
    private RequestQueue requestQueue;

    public PickupLineAdapter(List<PickupLineResponse.PickupLineData> pickupLineDataList, Context context) {
        this.pickupLineDataList = pickupLineDataList;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    @NonNull
    @Override
    public PickupLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new PickupLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupLineViewHolder holder, int position) {
        PickupLineResponse.PickupLineData pickupLineData = pickupLineDataList.get(position);
        holder.createdAt.setText(pickupLineData.getCreated_at());
        holder.pickupLineTextView.setText(pickupLineData.getName());
        List<String> interests = pickupLineData.getInterest();
        if (interests != null && interests.size() > 0) {
            holder.firstInterest.setText(interests.get(0));
        } else {
            holder.firstInterest.setText("");
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < pickupLineDataList.size()) { // check if position is valid
            pickupLineDataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pickupLineDataList.size());
        }
    }

    @Override
    public int getItemCount() {
        return pickupLineDataList.size();
    }

    public String getLineId(int position) {
        PickupLineResponse.PickupLineData pickupLineData = pickupLineDataList.get(position);
        return pickupLineData.getLine();
    }

    public Integer getId(int position) {
        PickupLineResponse.PickupLineData pickupLineData = pickupLineDataList.get(position);
        return pickupLineData.getId();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class PickupLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView pickupLineTextView;
        private TextView firstInterest;
        private TextView createdAt;
        private ImageView deleteImage;
        private LinearLayout startnewActivity;

        public PickupLineViewHolder(@NonNull View itemView) {
            super(itemView);
            pickupLineTextView = itemView.findViewById(R.id.thomas_j_pe);
            firstInterest = itemView.findViewById(R.id.first_inter);
            createdAt = itemView.findViewById(R.id.yesterday);
            deleteImage = itemView.findViewById(R.id.trash_btn);
            deleteImage.setOnClickListener(this);
            startnewActivity = itemView.findViewById(R.id.add_action);
            startnewActivity.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int lineId = getId(position);

            switch (v.getId()) {
                case R.id.trash_btn:
                    // Delete button clicked
                    deletePickupLineApi(lineId);
                    Toast.makeText(context, "Delete match successfully", Toast.LENGTH_SHORT).show();
                    removeItem(position);
                    break;
                case R.id.add_action:
                    // startnewActivity view clicked, start the PersonInterestActivity
                    startPersonInterestActivity(position);
                    break;
            }
        }

        private void startPersonInterestActivity(int position) {
            PickupLineResponse.PickupLineData pickupLineData = pickupLineDataList.get(position);

            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", pickupLineData.getName());
            intent.putExtra("line", pickupLineData.getLine());
            intent.putExtra("id", pickupLineData.getId());
            intent.putStringArrayListExtra("interests", (ArrayList<String>) pickupLineData.getInterest());
            intent.putExtra("favourite", pickupLineData.getFavorite());
            context.startActivity(intent);
        }
    }

    private void deletePickupLineApi(int lineId) {
        String url = GetApiParameters.getUrl();
        // Get the access token from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.DELETE_PICKUP_LINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");
                            if (status) {
                                // Show success message
                                Log.e("TAG", "onResponse deletelineApi: " + message);
                                Log.e("TAG", "onResponse deletePickupLineApi: " + response);
                            } else {
                                // Show error message
                                String validationErrors = jsonObject.optString("validation_errors");
                                Log.e("TAG", "onResponse deletelineApi: Validation Errors - " + validationErrors);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the API error here
                        String errorResponse = "";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorResponse = new String(error.networkResponse.data);
                        }
                        Log.e("TAG", "onErrorResponse deletelineApi: " + errorResponse);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("line_id", String.valueOf(lineId));
                Log.e("TAG", "getParams: " + params);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
