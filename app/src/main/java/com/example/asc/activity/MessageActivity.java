package com.example.asc.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.asc.R;
import com.example.asc.activity.ui.SlideRecyclerView;
import com.example.asc.activity.util.Message;
import com.example.asc.activity.util.MyDatabaseHelper;
import com.example.asc.activity.util.SlideMessageAdapter;
import com.example.asc.activity.util.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends AppCompatActivity {

    private SlideRecyclerView slideRecyclerView;
    public ArrayList<Message> messageArrayList;
    private SlideMessageAdapter slideMessageAdapter;

    private int space=15;//RecycleView间距

    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initMessage();
        slideRecyclerView=(SlideRecyclerView)findViewById(R.id.slideRecycleView);
        slideRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //DividerItemDecoration itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        //itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider_inset)));
        slideRecyclerView.addItemDecoration(new SpacesItemDecoration(space));
        slideMessageAdapter=new SlideMessageAdapter(this,messageArrayList);
        slideRecyclerView.setAdapter(slideMessageAdapter);
        slideMessageAdapter.setOnDeleteClickListener(new SlideMessageAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, int position) {
                final String content=messageArrayList.get(position).getContent();
                //数据库删除
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myDatabaseHelper=new MyDatabaseHelper(MessageActivity.this,"Message.db",null,1,2);
                        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                        db.delete("message","content=?",new String[]{content});
                    }
                }).start();
                messageArrayList.remove(position);
                slideMessageAdapter.notifyDataSetChanged();
                slideRecyclerView.closeMenu();
            }
        });


        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.layout_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Thread initThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initMessage();
                    }
                });
                initThread.start();
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        try{
                            initThread.join();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slideMessageAdapter=new SlideMessageAdapter(MessageActivity.this,messageArrayList);
                                slideRecyclerView.setAdapter(slideMessageAdapter);
                            }
                        });
                    }
                };
                timer.schedule(task, 4000);

            }
        });


    }

    public void initMessage(){
        messageArrayList=new ArrayList<>();
        myDatabaseHelper=new MyDatabaseHelper(getApplicationContext(),"Message.db",null,1,2);
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        Cursor cursor=db.query("Message",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            try{
                do{
                    String phone=cursor.getString(cursor.getColumnIndex("phone" ));
                    String content=cursor.getString(cursor.getColumnIndex("content"));
                    Message message=new Message(phone,content);
                    messageArrayList.add(0,message);
                }while(cursor.moveToNext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}