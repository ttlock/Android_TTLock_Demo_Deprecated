package com.example.ttlock.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ttlock.R;
import com.example.ttlock.activity.BaseActivity;
import com.example.ttlock.databinding.ItemUnderGatewayLockBinding;
import com.example.ttlock.model.UnderGatewayLock;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.utils.DateUitl;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TTLock on 2017/3/24.
 */
public class UnderGatewayLockAdapter extends RecyclerView.Adapter<UnderGatewayLockAdapter.GatewayHolder> {

    private Context mContext;
    private ArrayList<UnderGatewayLock> underGatewayLocks;

    public static class GatewayHolder extends RecyclerView.ViewHolder {

        public ItemUnderGatewayLockBinding binding;

        public GatewayHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(UnderGatewayLock underGatewayLock) {
            binding.setUnderGatewayLock(underGatewayLock);
        }
    }

    public UnderGatewayLockAdapter(Context context, ArrayList<UnderGatewayLock> underGatewayLocks) {
        this.mContext = context;
        this.underGatewayLocks = underGatewayLocks;
    }

    @Override
    public GatewayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_under_gateway_lock, parent, false);
        return new GatewayHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GatewayHolder holder, int position) {
        final UnderGatewayLock underGatewayLock = underGatewayLocks.get(position);
        holder.bind(underGatewayLock);
        holder.binding.setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPostExecute(String json) {
                        super.onPostExecute(json);
                        ((BaseActivity)mContext).cancelProgressDialog();
                        LogUtil.d("json:" + json, true);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String msg;
                            if(jsonObject.has("errcode")) {
                                msg = jsonObject.getString("errmsg");
                            } else {
                                long date = jsonObject.getLong("date");
                                msg = "set time:" + DateUitl.getTime(date, "yyyy-MM-dd HH:mm");
                            }
                            ((BaseActivity)mContext).toast(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        ((BaseActivity)mContext).showProgressDialog();
                        return ResponseService.updateLockDate(underGatewayLock.getLockId());
                    }
                }.execute();
            }
        });
        holder.binding.lockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPostExecute(String json) {
                        super.onPostExecute(json);
                        ((BaseActivity)mContext).cancelProgressDialog();
                        LogUtil.d("json:" + json, true);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String msg;
                            if(jsonObject.has("errcode")) {
                                msg = jsonObject.getString("errmsg");
                            } else {
                                long date = jsonObject.getLong("date");
                                msg = "lock time:" + DateUitl.getTime(date, "yyyy-MM-dd HH:mm");
                            }
                            ((BaseActivity)mContext).toast(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        ((BaseActivity)mContext).showProgressDialog();
                        return ResponseService.queryLockDate(underGatewayLock.getLockId());
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return underGatewayLocks.size();
    }

}
