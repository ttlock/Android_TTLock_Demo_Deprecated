package com.example.ttlock.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.example.ttlock.adapter.KeyAdapter;
import com.example.ttlock.dao.DbService;
import com.example.ttlock.model.Key;
import com.example.ttlock.model.KeyObj;
import com.example.ttlock.net.ResponseService;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.list)
    ListView listView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

//    private String accessToken;

    private KeyAdapter keyAdapter;

    private List<Key> keys;

    /**
     * current used key
     */
    public static Key curKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initUI();

        LogUtil.d("init", DBG);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d("", DBG);
        syncData();
    }

    private void initUI() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Initialization
     */
    private void init() {
        //turn on bluetooth
        MyApplication.mTTLockAPI.requestBleEnable(this);
        LogUtil.d("start bluetooth service", DBG);
        MyApplication.mTTLockAPI.startBleService(this);
        //It need location permission to start bluetooth scan,or it can not scan device
        if(requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            MyApplication.mTTLockAPI.startBTDeviceScan();
        }

//        accessToken = MyPreference.getStr(this, MyPreference.ACCESS_TOKEN);
        keys = new ArrayList<>();
        syncData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        LogUtil.d("", DBG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer(GravityCompat.START);
        LogUtil.d("", DBG);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void onResume() {
        super.onResume();
        LogUtil.d("", DBG);
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        curKey = keys.get(position);
        if(curKey.isAdmin()) {
            Intent intent = new Intent(this, OperateActivity.class);
            startActivity(intent);
        } else toast(getString(R.string.words_not_admin));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, getString(R.string.words_delete));
//        menu.setHeaderTitle(R.string.words_lock);
    }

    /**
     *
     * @param item 0 do delete operation
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = (int) info.id;
        final Key key = keys.get(position);
        showProgressDialog();
        if(item.getItemId() == 0) {
            new AsyncTask<Void, String, String>() {
                @Override
                protected void onPostExecute(String msg) {
                    super.onPostExecute(msg);
                    cancelProgressDialog();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    keys.remove(position);
                    keyAdapter.notifyDataSetChanged();
                }

                @Override
                protected String doInBackground(Void... params) {
                    String json = ResponseService.deleteKey(key.getKeyId());
                    String msg = "";
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject.getInt("errcode") == 0) {
                            msg = getString(R.string.words_delete_ekey_successed);
                            //delete local key
                            DbService.deleteKey(key);
                        } else msg = jsonObject.getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return msg;
                }
            }.execute();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("stop bluetooth service", DBG);
        MyApplication.mTTLockAPI.stopBleService(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, FoundDeviceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_auth:
                intent.setClass(this, AuthActivity.class);
                break;
            case R.id.nav_gateway:
                intent.setClass(this, GatewayActivity.class);
                break;
            case R.id.nav_gateway_list:
                intent.setClass(this, GatewayListActivity.class);
                break;
        }
        startActivity(intent);
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == TTLockAPI.REQUEST_ENABLE_BT) {
                //start bluetooth scan
                MyApplication.mTTLockAPI.startBTDeviceScan();
            }
        }
    }

    /**
     * synchronizes the data of key
     */
    private void syncData() {
        showProgressDialog();
        new AsyncTask<Void,String,String>() {

            @Override
            protected String doInBackground(Void... params) {
                //you can synchronizes all key datas when lastUpdateDate is 0
                String json = ResponseService.syncData(0);
                LogUtil.d("json:" + json, DBG);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        toast(jsonObject.getString("description"));
                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(intent);
                        return json;
                    }
                    //use lastUpdateDate you can get the newly added key and data after the time
                    long lastUpdateDate = jsonObject.getLong("lastUpdateDate");
                    String keyList = jsonObject.getString("keyList");
//                    JSONArray jsonArray = jsonObject.getJSONArray("keyList");
                    keys.clear();
                    ArrayList<KeyObj> list = GsonUtil.toObject(keyList, new TypeToken<ArrayList<KeyObj>>(){});
                    keys.addAll(convert2DbModel(list));
//                    for(int i = 0;i<jsonArray.length();i++) {
//                        jsonObject = (JSONObject) jsonArray.get(i);
//                        int keyId = jsonObject.getInt("keyId");
//                        int lockId = jsonObject.getInt("lockId");
//                        String userType = jsonObject.getString("userType");
//                        String keyStatus = jsonObject.getString("keyStatus");
//                        String lockName = jsonObject.getString("lockName");
//                        String lockAlias = jsonObject.getString("lockAlias");
//                        String lockKey = jsonObject.getString("lockKey");
//                        String lockMac = jsonObject.getString("lockMac");
//                        int lockFlagPos = jsonObject.getInt("lockFlagPos");
//                        String adminPwd = "";
//                        if (jsonObject.has("adminPwd"))
//                            adminPwd = jsonObject.getString("adminPwd");
//                        String noKeyPwd = "";
//                        if (jsonObject.has("noKeyPwd"))
//                            noKeyPwd = jsonObject.getString("noKeyPwd");
//                        String deletePwd = "";
//                        if (jsonObject.has("deletePwd"))
//                            deletePwd = jsonObject.getString("deletePwd");
//                        int electricQuantity = jsonObject.getInt("electricQuantity");
//                        String aesKeyStr = jsonObject.getString("aesKeyStr");
//                        String lockVersion = jsonObject.getString("lockVersion");
//                        long startDate = jsonObject.getLong("startDate");
//                        long endDate = jsonObject.getLong("endDate");
//                        String remarks = jsonObject.getString("remarks");
//                        int timezoneRawOffset = jsonObject.getInt("timezoneRawOffset");
//
//                        Key key = new Key();
//                        key.setKeyId(keyId);
//                        key.setLockId(lockId);
//                        key.setAdminPs(adminPwd);
//                        key.setAdminKeyboardPwd(noKeyPwd);
//                        key.setDeletePwd(deletePwd);
//                        key.setKeyStatus(keyStatus);
//                        key.setAdmin("110301".equals(userType));
//                        key.setAccessToken(MyPreference.getStr(MainActivity.this, MyPreference.ACCESS_TOKEN));
//                        key.setLockFlagPos(lockFlagPos);
//                        key.setLockId(lockId);
//                        key.setKeyId(keyId);
//                        key.setLockMac(lockMac);
//                        key.setLockName(lockName);
//                        key.setLockAlias(lockAlias);
//                        key.setUnlockKey(lockKey);
//                        key.setLockVersion(lockVersion);
//                        key.setBattery(electricQuantity);
//                        key.setStartDate(startDate);
//                        key.setEndDate(endDate);
//                        key.setAesKeystr(aesKeyStr);
//                        key.setTimezoneRawOffset(timezoneRawOffset);
//                        keys.add(key);
////                        DbService.saveKey(key);
//                    }

                    //clear local keys and save new keys
                    DbService.deleteAllKey();
                    DbService.saveKeyList(keys);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.cancel();
                keyAdapter = new KeyAdapter(MainActivity.this, keys);
                listView.setAdapter(keyAdapter);
                listView.setOnCreateContextMenuListener(MainActivity.this);
            }
        }.execute();
    }

    private static ArrayList<Key> convert2DbModel(ArrayList<KeyObj> list){
        ArrayList<Key> keyList = new ArrayList<>();
        if(list != null && list.size() > 0){
            for(KeyObj key : list){
                Key DbKey = new Key();
                DbKey.setUserType(key.userType);
                DbKey.setKeyStatus(key.keyStatus);
                DbKey.setLockId(key.lockId);
                DbKey.setKeyId(key.keyId);
                DbKey.setLockVersion(GsonUtil.toJson(key.lockVersion));
                DbKey.setLockName(key.lockName);
                DbKey.setLockAlias(key.lockAlias);
                DbKey.setLockMac(key.lockMac);
                DbKey.setElectricQuantity(key.electricQuantity);
                DbKey.setLockFlagPos(key.lockFlagPos);
                DbKey.setAdminPwd(key.adminPwd);
                DbKey.setLockKey(key.lockKey);
                DbKey.setNoKeyPwd(key.noKeyPwd);
                DbKey.setDeletePwd(key.deletePwd);
                DbKey.setPwdInfo(key.pwdInfo);
                DbKey.setTimestamp(key.timestamp);
                DbKey.setAesKeyStr(key.aesKeyStr);
                DbKey.setStartDate(key.startDate);
                DbKey.setEndDate(key.endDate);
                DbKey.setSpecialValue(key.specialValue);
                DbKey.setTimezoneRawOffset(key.timezoneRawOffset);
                DbKey.setKeyRight(key.keyRight);
                DbKey.setKeyboardPwdVersion(key.keyboardPwdVersion);
                DbKey.setRemoteEnable(key.remoteEnable);
                DbKey.setRemarks(key.remarks);

                keyList.add(DbKey);
            }
        }
        return keyList;
    }
}
