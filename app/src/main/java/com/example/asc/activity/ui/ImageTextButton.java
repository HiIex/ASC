package com.example.asc.activity.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asc.R;

public class ImageTextButton extends LinearLayout {

    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.image_text_button, this, true);
        mContext = context;
        mImgView = (ImageView)findViewById(R.id.img);
        mTextView = (TextView)findViewById(R.id.text);
    }

    /*设置图片接口*/
    public void setImageResource(int resId){
        mImgView.setImageResource(resId);
        mImgView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    /*设置文字接口*/
    public void setText(String str){
        mTextView.setText(str);
    }
    /*设置文字大小*/
    public void setTextSize(float size){
        mTextView.setTextSize(size);
    }
    //设置字体颜色
    public void setTextColor(int resID){
        mTextView.setTextColor(resID);
    }

    //设置字体加粗
    public void setTextBold(){
        mTextView.getPaint().setFakeBoldText(true);
    }

    public void setImagViewSize(int size){
        mImgView.setMinimumHeight(size);
        mImgView.setMinimumWidth(size);
    }

}
