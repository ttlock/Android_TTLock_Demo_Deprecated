package com.example.ttlock.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ttlock.R;
import com.example.ttlock.databinding.ItemGatewayBinding;
import com.example.ttlock.model.Gateway;

import java.util.ArrayList;

/**
 * Created by TTLock on 2017/3/24.
 */
public class GatewayListAdapter extends RecyclerView.Adapter<GatewayListAdapter.GatewayHolder> {

    private Context mContext;
    private ArrayList<Gateway> gateways;

    public static class GatewayHolder extends RecyclerView.ViewHolder {

        public ItemGatewayBinding binding;

        public GatewayHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(Gateway gateway) {
            binding.setGateway(gateway);
        }
    }

    public GatewayListAdapter(Context context, ArrayList<Gateway> gateways) {
        this.mContext = context;
        this.gateways = gateways;
    }

    @Override
    public GatewayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gateway, parent, false);
        itemView.setLongClickable(true);
        return new GatewayHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GatewayHolder holder, int position) {
        holder.bind(gateways.get(position));
    }

    @Override
    public int getItemCount() {
        return gateways.size();
    }

}
