package com.example.asc.activity.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_DB_PHONEBOOK = "create table phonebook("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "phone text)";

    public static final String CREATE_DB_MESSAGE = "create table message("
            + "id integer primary key autoincrement,"
            + "phone text,"
            + "content text)";

    public static final String CREATE_DB_SPACE="create table space("
            + "id integer primary key autoincrement,"
            + "date text,"
            + "note text,"
            + "T_BLOB blob)";

    private Context mContext;
    private int type;//1为phonebook，2为message,3为space

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,int type) {
        super(context, name, factory, version);
        mContext = context;
        this.type=type;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(type==1){
            db.execSQL(CREATE_DB_PHONEBOOK);
            //Toast.makeText(mContext, "打开联系人数据库成功！", Toast.LENGTH_SHORT).show();
        }else if(type==2){
            db.execSQL(CREATE_DB_MESSAGE);
            //Toast.makeText(mContext, "打开短信数据库成功！", Toast.LENGTH_SHORT).show();
        }else if(type==3){
            db.execSQL(CREATE_DB_SPACE);
            Toast.makeText(mContext, "打开空间数据库成功！", Toast.LENGTH_SHORT).show();
        }else if(type==0){
            db.execSQL(CREATE_DB_PHONEBOOK);
            db.execSQL(CREATE_DB_MESSAGE);
            db.execSQL(CREATE_DB_SPACE);
        }else{
            Toast.makeText(mContext, "发生未知错误！", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists phonebook");
        db.execSQL("drop table if exists message");
        db.execSQL("drop table if exists space");
        //db.execSQL("drop table if exists Category");
        type=0;
        onCreate(db);
    }
}