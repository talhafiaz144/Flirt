package com.example.flirtandroid.adapters;

import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MyViewHolder> {
    private List<PickupLineResponse.PickupLineData> pickupLineDataList;
    private Context context;
    private ItemClickListener clickListener;

    public FavAdapter(List<PickupLineResponse.PickupLineData> data, Context context) {
        this.pickupLineDataList = data;
        this.context = context;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PickupLineResponse.PickupLineData item = pickupLineDataList.get(position);
        holder.textView.setText(item.getLine());
        holder.textView2.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return pickupLineDataList.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < pickupLineDataList.size()) { // check if position is valid
            pickupLineDataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pickupLineDataList.size());
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView, textView2;
        public ImageView favIconBtn;
        public ImageView copy_txt;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.thomas_j_pe_1);
            textView2 = itemView.findViewById(R.id.user_name);
            favIconBtn = itemView.findViewById(R.id.fav_img_1);
            copy_txt = itemView.findViewById(R.id.copy_txt_1);
            favIconBtn.setOnClickListener(this);
            copy_txt.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == favIconBtn) {
                int position = getAdapterPosition();
                changeFavStatusApi(position);
                favIconBtn.setBackgroundResource(R.drawable.saved);
                Toast.makeText(context, "Remove from saved successfully", Toast.LENGTH_SHORT).show();
                removeItem(position);
            } else if (view == copy_txt) {
                String text = textView.getText().toString();
                copyTextToClipboard(text);
                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();
            }
        }

        private void copyTextToClipboard(String text) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText(text);
            }
        }
    }

    private void changeFavStatusApi(int position) {
        PickupLineResponse.PickupLineData item = pickupLineDataList.get(position);
        int lineId = item.getId();
        String favorite = "0";
        String url = GetApiParameters.getUrl();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + AppConstants.CHANGE_FAV_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the API response here
                        Log.e("TAG", "onResponse changeFavStatusApi: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the API error here
                        Log.e("TAG", "onErrorResponse: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String accessToken = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", "");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("line_id", String.valueOf(lineId));
                params.put("favorite", String.valueOf(favorite));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}