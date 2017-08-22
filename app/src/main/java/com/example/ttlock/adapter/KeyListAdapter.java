package com.example.ttlock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ttlock.R;
import com.example.ttlock.utils.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TTLock on 2016/9/21 0021.
 */
public class KeyListAdapter extends BaseAdapter {

    private Context mContext;

    private JSONArray jsonArray;

    public KeyListAdapter(Context context, JSONArray jsonArray) {
        mContext = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_ekey);
        JSONObject jsonObject = (JSONObject) getItem(position);
        try {
            String lockId = jsonObject.getString("lockId");
            String keyId = jsonObject.getString("keyId");
            ((TextView)viewHolder.getView(R.id.lockname)).setText(lockId + " - " + keyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewHolder.getConvertView();
    }
}
