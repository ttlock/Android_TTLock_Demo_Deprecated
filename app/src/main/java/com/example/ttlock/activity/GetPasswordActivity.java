package com.example.ttlock.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.example.ttlock.constant.ConstantKey;
import com.example.ttlock.enumtype.Operation;
import com.example.ttlock.model.Key;
import com.example.ttlock.model.KeyboardPasswdType;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.view.MultiButtonDialog;
import com.example.ttlock.wheel.WheelViewDialog;
import com.ttlock.bl.sdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.ttlock.utils.DateUitl.getTime;

public class GetPasswordActivity extends BaseActivity {

    @BindView(R.id.permanent)
    RadioButton permanentView;

    @BindView(R.id.period)
    RadioButton periodView;

    @BindView(R.id.loop)
    RadioButton loopView;

    @BindView(R.id.once)
    RadioButton onceView;

    @BindView(R.id.customized)
    RadioButton customizedView;

    @BindView(R.id.loop_value)
    TextView loopValueView;

    @BindView(R.id.start_time)
    TextView startTimeView;

    @BindView(R.id.end_time)
    TextView endTimeView;

    @BindView(R.id.password)
    TextView passwordView;

    @BindView(R.id.loop_layout)
    LinearLayout loopLayout;

    @BindView(R.id.start_time_layout)
    LinearLayout startTimeLayout;

    @BindView(R.id.end_time_layout)
    LinearLayout endTimeLayout;

    String[] pwdTypeTiles;
    @BindView(R.id.showPwd)
    TextView showPwd;

    private TimePickerView timePickerView;

    private WheelViewDialog wheelViewDialog;

    private boolean hourOnly;

    /**
     * keyboard password type
     */
    private int keyboardPwdType;

    long startDate;

    long endDate;

    private Key key;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        ButterKnife.bind(this);
        key = MainActivity.curKey;
        pwdTypeTiles = getResources().getStringArray(R.array.pwd_type);
        permanentView.setText(pwdTypeTiles[0]);
        periodView.setText(pwdTypeTiles[1]);
        loopView.setText(pwdTypeTiles[2]);
        onceView.setText(pwdTypeTiles[3]);

        //TODO:
        startDate = new Date().getTime();
        endDate = new Date().getTime();
        startTimeView.setText(getTime(new Date(), "yyyy-MM-dd HH:mm"));
        endTimeView.setText(getTime(new Date(), "yyyy-MM-dd HH:mm"));

        keyboardPwdType = KeyboardPasswdType.PERMENANT;
        loopLayout.setVisibility(View.GONE);
        endTimeLayout.setVisibility(View.GONE);

    }

    @OnClick({R.id.permanent, R.id.period, R.id.loop, R.id.once, R.id.start_time_layout, R.id.end_time_layout, R.id.password, R.id.loop_value, R.id.customized})
    void onClick(View view) {

        switch (view.getId()) {
            case R.id.permanent:
                passwordView.setText(R.string.words_get_passcode);
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.PERMENANT;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.GONE);
                break;
            case R.id.period:
                passwordView.setText(R.string.words_get_passcode);
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.PERIOD;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.loop:
                passwordView.setText(R.string.words_get_passcode);
                hourOnly = true;
                keyboardPwdType = KeyboardPasswdType.WEEKENDREPETUAL;
                loopValueView.setText("weekend");
                loopLayout.setVisibility(View.VISIBLE);
                endTimeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.once:
                passwordView.setText(R.string.words_get_passcode);
                hourOnly = false;
                keyboardPwdType = KeyboardPasswdType.ONCE;
                loopLayout.setVisibility(View.GONE);
                endTimeLayout.setVisibility(View.GONE);
                break;
            case R.id.start_time_layout:
                if (hourOnly) {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.HOURS_MINS);
                    timePickerView.setCyclic(true);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            int h = date.getHours();
                            int m = date.getMinutes();
                            startTimeView.setText(String.format("%02d:%02d", h, m));
                            date = new Date();
                            date.setHours(h);
                            date.setMinutes(m);
                            startDate = date.getTime();
//                            startDate = date.getTime();
//                            startTimeView.setText(getTime(date, "HH:mm"));
                        }
                    });
                } else {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.ALL);
                    timePickerView.setCyclic(false);
                    timePickerView.setRange(2018, 2021);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            startDate = date.getTime();
                            startTimeView.setText(getTime(date, "yyyy-MM-dd HH:mm"));
                        }
                    });
                }
                timePickerView.setTime(new Date());
                timePickerView.setCancelable(true);
                timePickerView.show();
                break;
            case R.id.end_time_layout:
                if (hourOnly) {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.HOURS_MINS);
                    timePickerView.setCyclic(true);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            int h = date.getHours();
                            int m = date.getMinutes();
                            endTimeView.setText(String.format("%02d:%02d", h, m));
                            date = new Date();
                            date.setHours(h);
                            date.setMinutes(m);
                            endDate = date.getTime();
//                            endTimeView.setText(getTime(date, "HH:mm"));
//                            endDate = date.getTime();
                        }
                    });
                } else {
                    timePickerView = new TimePickerView(GetPasswordActivity.this, TimePickerView.Type.ALL);
                    timePickerView.setCyclic(false);
                    timePickerView.setRange(2017, 2018);
                    timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) throws ParseException {
                            endDate = date.getTime();
                            endTimeView.setText(getTime(date, "yyyy-MM-dd HH:mm"));
                        }
                    });
                }
                timePickerView.setTime(new Date());
                timePickerView.setCancelable(true);
                timePickerView.show();
                break;
            case R.id.password:
                if (customizedView.isChecked()) {
                    showSetPasscodeDialog();
                    break;
                }
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String json = ResponseService.getKeyboardPwd(key.getLockId(), 4, keyboardPwdType, startDate, endDate);
                        String pwd = "";
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.has("keyboardPwd"))
                                pwd = jsonObject.getString("keyboardPwd");
                            else pwd = jsonObject.getString("description");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return pwd;
                    }

                    @Override
                    protected void onPostExecute(String pwd) {
                        super.onPostExecute(pwd);
                        showPwd.setText(pwd);
                    }
                }.execute();
                break;
            case R.id.loop_value:
                showWheelView();
                break;
            case R.id.customized:
                endTimeLayout.setVisibility(View.VISIBLE);
                loopLayout.setVisibility(View.GONE);
                passwordView.setText(R.string.words_set_passcode);
                break;
        }
    }

    private void showSetPasscodeDialog() {
        final MultiButtonDialog dialog = new MultiButtonDialog(this, true);
        dialog.show();
        dialog.setInputTypeNumber();
        dialog.setInputMaxLength(9);
        dialog.setInputCharacteristic("0123456789");
        dialog.setEditInputHint(R.string.input_keyboard_passcode_notify);
        dialog.setPositiveClickListener(new MultiButtonDialog.PositiveClickListener() {
            @Override
            public void onPositiveClick(String inputContent) {
                dialog.cancel();
                password = inputContent;
                if (!TextUtils.isEmpty(password)) {
                    if (password.length() < 4 || password.length() > 9)
                        toast(R.string.valid_length);
                    else
                        addByBle();
                } else toast(R.string.words_not_null);
            }
        });
    }

    private void addByBle() {
        if (MyApplication.mTTLockAPI.isBLEEnabled(this)) {
            showProgressDialog(getString(R.string.words_wait));
            MyApplication.bleSession.setPassword(password);

            MyApplication.bleSession.setStartDate(startDate);
            MyApplication.bleSession.setEndDate(endDate);
            MyApplication.bleSession.setOperation(Operation.ADD_PASSCODE);
            MyApplication.mTTLockAPI.connect(key.getLockMac());
        } else {
            MyApplication.mTTLockAPI.requestBleEnable(this);
        }
    }

    private void showWheelView() {
        wheelViewDialog = new WheelViewDialog(GetPasswordActivity.this, new WheelViewDialog.ICustomDialogEventListener() {
            @Override
            public void customDialogEvent(String circleModeValue, int position) {
                loopValueView.setText(circleModeValue);
                keyboardPwdType = position + 5;
                LogUtil.d("keyboardPwdType:" + keyboardPwdType, DBG);
            }
        });
        wheelViewDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action) {
            Intent intent = new Intent(GetPasswordActivity.this, KeyboardPwdListActivity.class);
            intent.putExtra(ConstantKey.KEY, key);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
