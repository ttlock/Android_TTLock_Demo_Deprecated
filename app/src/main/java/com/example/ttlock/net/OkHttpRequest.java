package com.example.ttlock.net;

import android.text.TextUtils;

import com.ttlock.bl.sdk.util.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class OkHttpRequest {

    private static boolean DBG = true;
    private static final OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();

    private static final MediaType DEFAULT = MediaType.parse("application/x-www-form-urlencoded");

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpRequest() {
        throw new AssertionError();
    }

    private static String getParamUrl(final Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (String key : params.keySet())
            sb.append(key).append('=').append(params.get(key)).append('&');
//		params.forEach((key, value) -> sb.append(key).append('=').append(value).append('&'));
        return sb.substring(0, sb.length() - 1);
    }

    public static String get(final String url) {

        return get(url, null);
    }

    public static String get(final String url, final Map<String, String> params) {
        final String paramUrl = getParamUrl(params);
        final String newUrl = TextUtils.isEmpty(paramUrl) ? url : url + "?" + paramUrl;
        Request.Builder requestBuilder = new Request.Builder().url(newUrl);
//		if (headers != null) {
//			headers.forEach((key, value) -> requestBuilder.addHeader(Utils.parseString(key), Utils.parseString(value)));
//		}
        Request request = requestBuilder.build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendPost(final String url, final Map<String, String> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                if(value == null)
                    value = "";
                LogUtil.d(String.format("%s:%s", key, value), DBG);
                formBodyBuilder.add(key, value);
            }
//			params.forEach((key, value) -> formBodyBuilder.add(key, value));
        }
        RequestBody body = formBodyBuilder.build();
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
//		if (headers != null) {
//			headers.forEach((key, value) -> requestBuilder.addHeader(key, value));
//		}
        Request request = requestBuilder.build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseData = response.body().string();
            LogUtil.d("responseData:" + responseData, DBG);
            return responseData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * json format
     *
     * @param url
     * @param params
     * @return
     */
    public static String sendPost(final String url, final String params) {
        RequestBody body = RequestBody.create(DEFAULT, params);
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        Request request = requestBuilder.build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
