package com.example.ttlock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ttlock.R;
import com.example.ttlock.constant.Operate;
import com.example.ttlock.model.Key;
import com.example.ttlock.utils.ViewHolder;

/**
 * Created by TTLock on 2016/9/13 0013.
 */
public class OperateAdapter extends BaseAdapter {

    private Context mContext;

    private Key mKey;

    String[] operates;

    public OperateAdapter(Context context, Key key, String[] operates) {
        mContext = context;
        mKey = key;
        this.operates = operates;
    }

    @Override
    public int getCount() {
        return operates.length;
    }

    @Override
    public Object getItem(int position) {
        return operates[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_operate);
        ((TextView)viewHolder.getView(R.id.operate)).setText(operates[position]);
        TextView valueView = viewHolder.getView(R.id.value);
        switch (position) {
            case Operate.SET_ADMIN_CODE:
                valueView.setText(mKey.getNoKeyPwd());
                break;
            case Operate.SET_DELETE_CODE:
                valueView.setText(mKey.getDeletePwd());
                break;
        }
        return viewHolder.getConvertView();
    }
}
