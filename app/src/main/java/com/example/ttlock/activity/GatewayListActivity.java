package com.example.ttlock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.example.ttlock.R;
import com.example.ttlock.adapter.GatewayListAdapter;
import com.example.ttlock.constant.ConstantKey;
import com.example.ttlock.databinding.ActivityGatewayListBinding;
import com.example.ttlock.model.Gateway;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.wheel.OnRecyclerItemClickListener;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GatewayListActivity extends BaseActivity {

    ActivityGatewayListBinding binding;
    private ArrayList<Gateway> gateways;
    private GatewayListAdapter gatewayListAdapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gateway_list);
        showProgressDialog();
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return ResponseService.gatewayList(1, 100);
            }

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                cancelProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        String errmsg = jsonObject.getString("errmsg");
                        toast(errmsg);
                    } else {
                        json = jsonObject.getJSONArray("list").toString();
                        gateways = GsonUtil.toObject(json, new TypeToken<ArrayList<Gateway>>(){});

                        gatewayListAdapter = new GatewayListAdapter(GatewayListActivity.this, gateways);
                        binding.recycler.setLayoutManager(new LinearLayoutManager(GatewayListActivity.this));
                        binding.recycler.setAdapter(gatewayListAdapter);

                        binding.recycler.setOnCreateContextMenuListener(GatewayListActivity.this);

                        binding.recycler.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.recycler) {
                            @Override
                            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                                Gateway gateway = ((GatewayListAdapter.GatewayHolder)viewHolder).binding.getGateway();
                                Intent intent = new Intent(GatewayListActivity.this, UnderGatewayLockList.class);
                                intent.putExtra(ConstantKey.GATEWAY, gateway);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {
//                                LogUtil.d("长按", true);
                                position = viewHolder.getPosition();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.d("json:" + json, true);
            }
        }.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, getString(R.string.words_delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        final int position = (int) info.position;
//        View view = info.targetView;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return ResponseService.deleteGateway(gateways.get(position).getGatewayId());
            }

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int errcode = jsonObject.getInt("errcode");
                    if (errcode != 0) {
                        String errmsg = jsonObject.getString("errmsg");
                        toast(errmsg);
                    } else {
                        gateways.remove(position);
                        toast(getString(R.string.words_gateway_delete_successed));
                        gatewayListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
        return super.onContextItemSelected(item);
    }
}
