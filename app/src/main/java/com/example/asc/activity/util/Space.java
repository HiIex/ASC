package com.example.asc.activity.util;

import android.graphics.Bitmap;

public class Space {
    public String date;
    public Bitmap bitmap;
    public String note;

    public Space(String date, Bitmap bitmap, String note){
        this.date=date;
        this.bitmap=bitmap;
        this.note=note;
    }

    public String getDate(){
        return this.date;
    }

    public String getNote(){
        return this.note;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }


}
