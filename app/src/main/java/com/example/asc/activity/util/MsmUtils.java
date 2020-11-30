package com.example.asc.activity.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class MsmUtils extends BroadcastReceiver {

    private MyDatabaseHelper myDatabaseHelper;
    private int notifyID=0;
    public NotificationManager notificationManager;
    private static final String TAG="MsmUtils";
    /**
     * 发送 MSM
     * @param activity
     * @param targetNumber
     * @param content
     */
    public static void sendMsm(Activity activity, String targetNumber, String content) {
        //创建一个PendingIntent对象
        PendingIntent pi = PendingIntent.getActivity(activity, 0, new Intent(), 0);
        //获取SmsManager
        SmsManager sManager = SmsManager.getDefault();
        //发送短信
        sManager.sendTextMessage(targetNumber, null, content, pi, null);
    }

    /**
     * 接收到新MSM
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage smsMessage;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                smsMessage = SmsMessage.createFromPdu((byte[]) object);
                final String content = smsMessage.getDisplayMessageBody();
                final String phone = smsMessage.getOriginatingAddress();

                //插入短信数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myDatabaseHelper=new MyDatabaseHelper(context,"Message.db",null,1,2);
                        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                        ContentValues contentValues=new ContentValues();
                        contentValues.put("phone",phone);
                        contentValues.put("content",content);
                        db.insert("message",null,contentValues);
                    }
                }).start();



            }
        }
    }
}