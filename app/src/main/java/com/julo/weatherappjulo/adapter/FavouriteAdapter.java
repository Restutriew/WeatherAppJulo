package com.julo.weatherappjulo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julo.weatherappjulo.R;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {


    private Context mContext;
    private ArrayList<String> mcityNameList;

    private FavouriteAdapter.OnItemClickCallback onItemClickCallback;


    public FavouriteAdapter(Context mContext, ArrayList mcityNameList) {
        this.mContext = mContext;
        this.mcityNameList = mcityNameList;
    }

    public void setOnItemClickCallback(FavouriteAdapter.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_favourite, parent, false);
        return new FavouriteAdapter.FavouriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        holder.tv_favouriteCityList.setText(String.valueOf(mcityNameList.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(mcityNameList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mcityNameList.size();
    }

    public interface OnItemClickCallback {
        void onItemClicked(String data);
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favouriteCityList;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_favouriteCityList = itemView.findViewById(R.id.tv_favouriteCityList);
        }
    }
}
