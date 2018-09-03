package com.example.ttlock.wheel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.ttlock.R;
import com.example.ttlock.wheel.adapter.ArrayWheelAdapter;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/1.
 */

public class WheelViewDialog extends Dialog {

    public static final String TAG = WheelViewDialog.class.getSimpleName();

    private Context mContext;

    private WheelView mDataView;

    public String[] wheelData;

    public static String circleValue;

    public TextView btnSure, btnCancel;

    public interface ICustomDialogEventListener {
        void customDialogEvent(String circleModeValue, int position);
    }

    private ICustomDialogEventListener onCustomDialogEventListener;

    public WheelViewDialog(Context context, ICustomDialogEventListener onCircleValueListener) {
        super(context);
        this.mContext = context;
        this.onCustomDialogEventListener = onCircleValueListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wheel_view_dialog);
        initViews();
    }

    private void initViews() {
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mDataView = (WheelView) findViewById(R.id.circle_mode_wheelwheelview);
        btnSure = (TextView) findViewById(R.id.btn_sure);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        wheelData = mContext.getResources().getStringArray(R.array.wheel_data);
        mDataView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, wheelData));
        Date date = new Date();
//        mDataView.setCurrentItem(getWeekOfDate(date)-1);
        mDataView.setCurrentItem(0);
        circleValue = wheelData[mDataView.getCurrentItem()];
        mDataView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                circleValue = wheelData[mDataView.getCurrentItem()];
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleValue = wheelData[mDataView.getCurrentItem()];
                onCustomDialogEventListener.customDialogEvent(circleValue, mDataView.getCurrentItem());
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

//    public static int getWeekOfDate(Date dt) {
//        int [] weekDays = {0, 1,2,3,4,5,6};
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(dt);
//        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
//        if (w < 0)
//            w = 0;
//        return weekDays[w];
//
//    }
}