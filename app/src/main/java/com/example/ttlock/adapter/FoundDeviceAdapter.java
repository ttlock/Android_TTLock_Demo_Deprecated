package com.example.ttlock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ttlock.R;
import com.example.ttlock.utils.ViewHolder;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.List;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class FoundDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<ExtendedBluetoothDevice> mLeDevices;

    public FoundDeviceAdapter(Context context, List<ExtendedBluetoothDevice> mLeDevices) {
        mContext = context;
        this.mLeDevices = mLeDevices;
    }

    /**
     * update scan device
     * @param extendedBluetoothDevice
     */
    public void updateDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
        boolean contain = false;
        boolean update = false;
        for(ExtendedBluetoothDevice device : mLeDevices) {
            if(device.equals(extendedBluetoothDevice)) {
                contain = true;
                if(device.isSettingMode() != extendedBluetoothDevice.isSettingMode()) {
                    device.setSettingMode(extendedBluetoothDevice.isSettingMode());
                    update = true;
                }
            }
        }
        if(!contain) {
            mLeDevices.add(extendedBluetoothDevice);
            update = true;
        }
        if(update)
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mLeDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return ((ExtendedBluetoothDevice) getItem(position)).isSettingMode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_device);
        TextView deviceName  = viewHolder.getView(R.id.device_name);
        TextView macAddress = viewHolder.getView(R.id.device_address);
        TextView addIcon = viewHolder.getView(R.id.device_add_able);
        if(mLeDevices.get(position).isSettingMode())
            addIcon.setVisibility(View.VISIBLE);
        else addIcon.setVisibility(View.GONE);
        deviceName.setText(mLeDevices.get(position).getName());
        macAddress.setText(mLeDevices.get(position).getAddress());
        return viewHolder.getConvertView();
    }
}
