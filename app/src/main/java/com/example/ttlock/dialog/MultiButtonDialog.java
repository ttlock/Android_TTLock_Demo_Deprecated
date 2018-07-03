package com.example.ttlock.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ttlock.MyApplication;
import com.example.ttlock.R;
import com.example.ttlock.utils.DisplayUtil;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MultiButtonDialog extends Dialog implements View.OnClickListener {

    public static final int STYLE_WITH_EDIT = 1;
    public static final int STYLE_NO_EDIT = 2;

    private Boolean showEdith = false;

    private Context context;
    private  ViewHolder viewHolder;

    private PositiveClickListener mListener;
    private LeftButtonClickListener mLeftLister;

    public interface PositiveClickListener{
        void onPositiveClick(String inputContent);
    }

    public interface LeftButtonClickListener{
        void onLeftClick();
    }

    public MultiButtonDialog(Context context) {
        super(context, R.style.DialogLayout);
    }

    public MultiButtonDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public MultiButtonDialog(Context context, boolean withEdit){
        super(context, R.style.DialogLayout);
        showEdith = withEdit;

    }

    public void setPositiveClickListener(PositiveClickListener listener){
        this.mListener = listener;
    }

    public void setLeftClickListener(LeftButtonClickListener listener){
        this.mLeftLister = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_button_dialog);
        viewHolder = new ViewHolder();
        initDialog();
    }

    private void initDialog(){
        if(showEdith){
            viewHolder.mInput_root.setVisibility(View.VISIBLE);
            viewHolder.mTv_title.setVisibility(View.VISIBLE);
            viewHolder.tv_content.setVisibility(View.GONE);
        }
    }

    public void setContentText(String text){
        if(!TextUtils.isEmpty(text)){
            if(showEdith){
                viewHolder.mEt_input.setText(text);
            }else {
                viewHolder.tv_content.setText(text);
                viewHolder.tv_content.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = viewHolder.tv_content.getLineCount();
                        if (lineCount == 1) {
                            viewHolder.tv_content.setGravity(Gravity.CENTER);
                        }
                    }
                });
            }
        }
    }

    public void setContentText(int stringId){
        if(showEdith){
            viewHolder.mEt_input.setText(stringId);
        }else {
            viewHolder.tv_content.setText(stringId);
            viewHolder.tv_content.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = viewHolder.tv_content.getLineCount();
                    if (lineCount == 1 && !viewHolder.tv_content.getText().toString().contains("\n")) {
                        viewHolder.tv_content.setGravity(Gravity.CENTER);
                    }
                }
            });
        }
    }


    public void setDialogTitle(String title){
        viewHolder.mTv_title.setText(title);
    }

    public void setDialogTitle(int stringId){
        viewHolder.mTv_title.setVisibility(View.VISIBLE);
        viewHolder.mTv_title.setText(stringId);
    }

    public void setEditInputHint(int stringId){
        if(showEdith){
            viewHolder.mEt_input.setHint(stringId);
        }
    }

    public void setLeftBtnText(int stringId){
        viewHolder.btn_left.setText(stringId);
    }

    public void setRightBtnText(int stringId){
        viewHolder.btn_right.setText(stringId);
    }

    public void setInputTypeNumber() {
        viewHolder.mEt_input.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
    }

    public void setInputMaxLength(int len) {
        viewHolder.mEt_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(len)});
    }

    public void setInputCharacteristic(String digits) {
        viewHolder.mEt_input.setKeyListener(DigitsKeyListener.getInstance(digits));
    }

    public void showDialog(Dialog dialog){
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setContentView(R.layout.multi_button_dialog);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        //TODO:
        p.width = (int) (DisplayUtil.getScreenWidth(MyApplication.getInstance()) * 0.80);
        dialogWindow.setAttributes(p);
    }

    public void setFirstEditConent(String conent){
        viewHolder.mEt_input.setText(conent);
    }


    class ViewHolder {
        Button btn_right;
        Button btn_left;
        public TextView tv_content;
        public TextView mTv_title;
        public EditText mEt_input;
        public ImageView mIv_clearInput;
        public ViewGroup mInput_root;

        public ViewHolder(){
            tv_content = (TextView)findViewById(R.id.tv_content);
            btn_right = (Button) findViewById(R.id.btn_right);
            btn_left = (Button)findViewById(R.id.btn_left);
            mTv_title = (TextView)findViewById(R.id.tv_title);
            mEt_input = (EditText)findViewById(R.id.et_input_one);
            mIv_clearInput = (ImageView)findViewById(R.id.iv_clear_input);
            mInput_root = (LinearLayout)findViewById(R.id.ll_input_root_one);

            mIv_clearInput.setOnClickListener(MultiButtonDialog.this);
            btn_right.setOnClickListener(MultiButtonDialog.this);
            btn_left.setOnClickListener(MultiButtonDialog.this);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_right:
                if(mListener != null){
                    String editContent = "";
                    if(showEdith){
                        editContent = viewHolder.mEt_input.getText().toString();
                    }
                    mListener.onPositiveClick(editContent);
                }else {
                    dismiss();
                }
                break;
            case R.id.iv_clear_input:
                viewHolder.mEt_input.setText("");
                break;
            case R.id.btn_left:
                if(mLeftLister != null){
                    mLeftLister.onLeftClick();
                    dismiss();
                }else {
                    dismiss();
                }
                break;
            default:
                dismiss();
                break;
        }
    }
}
