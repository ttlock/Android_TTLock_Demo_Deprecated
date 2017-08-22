package com.example.ttlock.activity;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ttlock.R;
import com.example.ttlock.adapter.KeyListAdapter;
import com.example.ttlock.dao.DbService;
import com.example.ttlock.model.Key;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * V3不存在待下载的钥匙列表
 */
@Deprecated
public class KeyListActivity extends BaseActivity {

    @BindView(R.id.list)
    ListView listView;
    JSONArray jsonArray = new JSONArray();
    private KeyListAdapter keyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_list);
        ButterKnife.bind(this);
        new AsyncTask<Void, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {
                String json = ResponseService.keyList();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    jsonArray = jsonObject.getJSONArray("list");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                super.onPostExecute(jsonArray);
                keyListAdapter = new KeyListAdapter(KeyListActivity.this, jsonArray);
                listView.setAdapter(keyListAdapter);
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnItemClick(R.id.list)
    void onItemClick(final int position) {
        showProgressDialog();
        new AsyncTask<Void, Integer, String >() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) jsonArray.get(position);
                    int lockId = jsonObject.getInt("lockId");
                    int keyId = jsonObject.getInt("keyId");
                    String json = ResponseService.downloadKey(lockId, keyId);
                    jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        msg = jsonObject.getString("description");
                    } else {
                        String lockName = jsonObject.getString("lockName");
                        String lockAlias = jsonObject.getString("lockAlias");
                        String lockKey = jsonObject.getString("lockKey");
                        String lockMac = jsonObject.getString("lockMac");
                        String aesKeyStr = jsonObject.getString("aesKeyStr");
                        String lockVersion = jsonObject.getString("lockVersion");
                        int lockFlagPos = jsonObject.getInt("lockFlagPos");
                        int electricQuantity = jsonObject.getInt("electricQuantity");
                        long startDate = jsonObject.getLong("startDate");
                        long endDate = jsonObject.getLong("endDate");
                        Key key = new Key();
                        key.setAdmin(false);
                        key.setAccessToken(MyPreference.getStr(KeyListActivity.this, MyPreference.ACCESS_TOKEN));
                        key.setLockFlagPos(lockFlagPos);
                        key.setLockId(lockId);
                        key.setKeyId(keyId);
                        key.setLockMac(lockMac);
                        key.setLockName(lockName);
                        key.setUnlockKey(lockKey);
                        key.setLockVersion(lockVersion);
                        key.setBattery(electricQuantity);
                        key.setStartDate(startDate);
                        key.setEndDate(endDate);
                        key.setAesKeystr(aesKeyStr);
                        DbService.saveKey(key);
                        msg = "下载钥匙成功";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            @TargetApi(19)
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);
                toast(msg);
                jsonArray.remove(position);
                keyListAdapter.notifyDataSetChanged();
                cancelProgressDialog();
            }
        }.execute();
    }
}
