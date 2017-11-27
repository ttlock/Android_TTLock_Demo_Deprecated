package com.example.ttlock.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ttlock.R;
import com.example.ttlock.net.ResponseService;
import com.example.ttlock.sp.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    EditText user;
    EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        user = getView(R.id.auth_user);
        pwd = getView(R.id.auth_pwd);
        TextView auth = getView(R.id.auth);
        auth.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String access_token = MyPreference.getStr(AuthActivity.this, MyPreference.ACCESS_TOKEN);
        String openid = MyPreference.getStr(AuthActivity.this, MyPreference.OPEN_ID);
        ((TextView)getView(R.id.auth_access_token)).setText(access_token);
        ((TextView)getView(R.id.auth_openid)).setText(openid);
    }

    @Override
    public void onClick(View v) {
        final String username = user.getText().toString();
        final String password = pwd.getText().toString();
        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return ResponseService.auth(username, password);
            }

            @Override
            protected void onPostExecute(String json) {
                String msg = getString(R.string.words_authorize_successed);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("errcode")) {
                        msg = jsonObject.getString("description");
                    } else {
                        String access_token = jsonObject.getString("access_token");
                        String openid = jsonObject.getString("openid");
                        MyPreference.putStr(AuthActivity.this, MyPreference.ACCESS_TOKEN, access_token);
                        MyPreference.putStr(AuthActivity.this, MyPreference.OPEN_ID, openid);
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        onResume();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toast(msg);
            }
        }.execute();
    }
}
