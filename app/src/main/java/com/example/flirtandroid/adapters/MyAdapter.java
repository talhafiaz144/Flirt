package com.example.flirtandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.flirtandroid.R;

import java.util.List;
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> data;
    public MyAdapter(List<String> data) {
        this.data = data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String item = data.get(position);
        holder.textView.setText(item);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public void removeItem(int position) {
        if (position >= 0 && position < data.size()) { // check if position is valid
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public ImageView trashBtn;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.yesterday);
            trashBtn = itemView.findViewById(R.id.trash_btn);
            trashBtn.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            removeItem(position);
        }
    }
}
