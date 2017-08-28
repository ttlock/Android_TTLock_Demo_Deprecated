package com.example.ttlock.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.example.ttlock.adapter.OperateAdapter;
import com.example.ttlock.constant.Operate;
import com.example.ttlock.enumtype.Operation;
import com.example.ttlock.model.BleSession;
import com.example.ttlock.model.Key;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static com.example.ttlock.MyApplication.mTTLockAPI;
import static com.example.ttlock.activity.MainActivity.curKey;

public class OperateActivity extends BaseActivity {

    @BindView(R.id.list)
    ListView listView;

    private OperateAdapter operateAdapter;

    private BleSession bleSession;

    private Key mKey;

    private Dialog dialog;

    private String[] operates;
    private int openid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operate);
        ButterKnife.bind(this);
        mKey = curKey;
        operates = getResources().getStringArray(R.array.operate);
        operateAdapter = new OperateAdapter(this, mKey, operates);
        listView.setAdapter(operateAdapter);
        bleSession = MyApplication.bleSession;
        openid = MyPreference.getOpenid(this, MyPreference.OPEN_ID);
    }

    @OnItemClick(R.id.list)
    void onItemClick(int position) {
        switch (position) {
            case Operate.CLICK_TO_UNLOCK://点击开门
                if(mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if(mKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), System.currentTimeMillis(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {//未连接进行连接
                    showProgressDialog("等待连接");
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.CLICK_UNLOCK);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            //后面两个操作是车位锁独有操作
            case Operate.LOCKCAR_UP://车位锁升
                if(mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if(mKey.isAdmin())
                        mTTLockAPI.lockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr());
                    else
                        mTTLockAPI.lockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                    MyApplication.mTTLockAPI.lockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {
                    showProgressDialog("等待连接");
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.LOCKCAR_UP);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            case Operate.LOCKCAR_DOWN://车位锁降
                if(mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    if(mKey.isAdmin())
                        mTTLockAPI.unlockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), System.currentTimeMillis(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                    else
                        mTTLockAPI.unlockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                    MyApplication.mTTLockAPI.unlockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
                } else {
                    showProgressDialog("等待连接");
                    mTTLockAPI.connect(mKey.getLockMac());
                    bleSession.setOperation(Operation.LOCKCAR_DOWN);
                    bleSession.setLockmac(mKey.getLockMac());
                }
                break;
            case Operate.DEVICE_FIRMWARE_UPDATE:
                start_activity(DeviceFirmwareUpdateActivity.class);
                break;
            default:
                showMyDialog(position);
                break;
        }
    }

    private void showMyDialog(final int operate) {
        if (dialog == null)
            dialog = new Dialog(this, R.style.dialog);
        View dialogView = View.inflate(this, R.layout.dialog_input_view, null);
        TextView titleView = getView(dialogView, R.id.dialog_title);
        final EditText contentView = getView(dialogView, R.id.dialog_content);
        String title = operates[operate];
        String content = "";
        String hit = "";
        switch (operate) {
            case Operate.SET_ADMIN_CODE:
//                title = "设置管理码";
                hit = "请输入管理码";
                break;
            case Operate.SET_DELETE_CODE:
//                title = "设置清空码";
                hit = "请输入清空码";
                break;
            case Operate.SET_LOCK_TIME:
//                title = "设置锁时间";
                hit = "传入要设置时间戳(默认使用手机时间)";
                break;
            case Operate.SEND_EKey:
                hit = "请输入接收者的用户名,默认发送永久钥匙";
                break;
            default:
                contentView.setVisibility(View.GONE);
                break;
        }
        titleView.setText(title);
        contentView.setHint(hit);
        TextView oKView = getView(dialogView, R.id.dialog_ok);
        TextView cancelView = getView(dialogView, R.id.dialog_cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        oKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(operate >= Operate.SET_ADMIN_CODE && operate <= Operate.GET_LOCK_TIME)
                    showProgressDialog("等待连接");
                else showProgressDialog();
                String content = contentView.getText().toString().trim();
                switch (operate) {
                    case Operate.SET_ADMIN_CODE:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.setAdminKeyboardPassword(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), content);
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.SET_ADMIN_KEYBOARD_PASSWORD);
                            bleSession.setPassword(content);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.SET_DELETE_CODE:
                        bleSession.setOperation(Operation.SET_DELETE_PASSWORD);
                        bleSession.setPassword(content);
                        bleSession.setLockmac(mKey.getLockMac());
                        break;
                    case Operate.SET_LOCK_TIME:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.setLockTime(null, openid, curKey.getLockVersion(), curKey.getUnlockKey(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.SET_LOCK_TIME);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.RESET_KEYBOARD_PASSWORD:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.resetKeyboardPassword(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.RESET_KEYBOARD_PASSWORD);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.RESET_EKEY:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.resetEKey(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.RESET_EKEY);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.RESET_LOCK:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.resetLock(null, openid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.RESET_LOCK);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.GET_OPERATE_LOG:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.getOperateLog(null, curKey.getLockVersion(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.GET_OPERATE_LOG);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.GET_LOCK_TIME:
                        if(mTTLockAPI.isConnected(mKey.getLockMac())) {
                            mTTLockAPI.getLockTime(null, curKey.getLockVersion(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
                        } else {
                            mTTLockAPI.connect(mKey.getLockMac());
                            bleSession.setOperation(Operation.GET_LOCK_TIME);
                            bleSession.setLockmac(mKey.getLockMac());
                        }
                        break;
                    case Operate.SEND_EKey:
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                String json = ResponseService.sendEKey(mKey, params[0]);
                                String msg = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    msg = jsonObject.getString("description");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return msg;
                            }

                            @Override
                            protected void onPostExecute(String msg) {
                                super.onPostExecute(msg);
                                toast(msg);
                                cancelProgressDialog();
                            }
                        }.execute(contentView.getText().toString().trim());
                        break;
                    case Operate.GET_PASSWORD:
                        cancelProgressDialog();
                        Intent intent = new Intent(OperateActivity.this, GetPasswordActivity.class);
                        startActivity(intent);
                        break;


                }
            }
        });

        dialog.show();

        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        LogUtil.d("p.height:" + p.height, DBG);
        LogUtil.d("p.width:" + p.width, DBG);
//        p.width = WindowManager.LayoutParams.MATCH_PARENT;
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        dialog.setContentView(dialogView);
    }

}

