package com.example.asc.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.asc.R;
import com.example.asc.activity.ui.DragFloatActionButton;
import com.example.asc.activity.ui.ImageTextButton;
import com.example.asc.activity.ui.RotateDownTransformer;
import com.example.asc.activity.ui.SlideRecyclerView;
import com.example.asc.activity.util.BgmService;
import com.example.asc.activity.util.MsmUtils;
import com.example.asc.activity.util.MyDatabaseHelper;
import com.example.asc.activity.util.NetworkChangeReciever;
import com.example.asc.activity.util.Person;
import com.example.asc.activity.util.PhonebookAdapter;
import com.example.asc.activity.util.SlideMessageAdapter;
import com.example.asc.activity.util.Space;
import com.example.asc.activity.util.SpaceAdapter;
import com.example.asc.activity.util.SpacesItemDecoration;
import com.example.asc.activity.util.WeatherService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class AfterLoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<View>pageView;
    public View view1;
    public View view2;
    public View view3;
    public View view4;

    public ImageTextButton imageTextButton_left;
    public ImageTextButton imageTextButton_middle;
    public ImageTextButton imageTextButton_middle2;
    public ImageTextButton imageTextButton_right;

    private RecyclerView recyclerView_phonebook;
    private PhonebookAdapter phonebookAdapter;
    private RecyclerView.LayoutManager layoutManager_phonebook;
    private ArrayList<Person> personArrayList;

    private RecyclerView recyclerView_space;
    private SpaceAdapter spaceAdapter;
    private RecyclerView.LayoutManager layoutManager_space;
    private ArrayList<Space> spaceArrayList;

    private SlideRecyclerView slideRecyclerView_phonebook;
    private SlideMessageAdapter slideMessageAdapter;

    private MyDatabaseHelper myDatabaseHelper;

    private NetworkChangeReciever networkChangeReciever=null;

    public final static String heUsername="HE2009141654011898";
    public final static String heKey="4abb86ee73cf47879590b9151e442788";

    private LocationClient mLocationClient;
    public String city;
    public String disttrict;
    private boolean firstEnter=false;
    final private String TAG="AfterLoginActivity";

    private MediaPlayer mediaPlayer=new MediaPlayer();
    public Intent intent_bgm=null;

    private Thread contactThread;

    public final static int TAKE_PHOTO=1;
    private Uri imageUri;
    private Bitmap bitmap_photo=null;

    public static final int VIDEO_PAUSE=1;
    public static final int VIDEO_PLAY=2;
    private int videoState=VIDEO_PAUSE;

    public NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new AfterLoginActivity.MyLocationListener());
        setContentView(R.layout.activity_after_login);

        contactThread=new Thread(new Runnable() {
            @Override
            public void run() {
                initPhonebook();
                initContacts();
                initSpace();
            }
        });

        HeConfig.init(heUsername,heKey);
        HeConfig.switchToFreeServerNode();
        firstEnter=true;

        //申请权限
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECEIVE_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_SMS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if(ContextCompat.checkSelfPermission(AfterLoginActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_CONTACTS);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(AfterLoginActivity.this,permissions,2);
            //ActivityCompat.requestPermissions(AfterLoginActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS}, 1);
        }else{
            //请求位置和天气
            requestLocation();
            //获得联系人
            contactThread.start();
        }

        //开启天气后台服务
        Intent intent_weather=new Intent(this,WeatherService.class);
        startService(intent_weather);

        //动态注册广播,监测网络变化
        networkChangeReciever=new NetworkChangeReciever();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReciever,intentFilter);

        //启动bgm
        startService(new Intent(AfterLoginActivity.this,BgmService.class));


        viewPager=(ViewPager)findViewById(R.id.viewPage);
        viewPager.setOffscreenPageLimit(4);
        final LayoutInflater layoutInflater=getLayoutInflater();

        view1=layoutInflater.inflate(R.layout.tab1,null);
        initView1();

        view2=layoutInflater.inflate(R.layout.tab2,null);
        initView2();

        view3=layoutInflater.inflate(R.layout.tab3,null);
        initView3();

        view4=layoutInflater.inflate(R.layout.tab4,null);
        initView4();

        pageView=new ArrayList<>();
        pageView.add(view1);
        pageView.add(view2);
        pageView.add(view3);
        pageView.add(view4);

        final PagerAdapter mPagerAdapter = new PagerAdapter() {

            private boolean firstEnter = true;

            public void setFirstEnter(boolean firstEnter) {
                this.firstEnter = firstEnter;
            }

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageView.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }
            //使从ViewGroup中移出当前View


            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                //super.destroyItem(container, position, object);
            }


            //可以在这里初始化view
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(pageView.get(arg1));
                return pageView.get(arg1);
            }

        };

        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPageTransformer(true,new RotateDownTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageTextButton_left=(ImageTextButton)findViewById(R.id.left_button);
                imageTextButton_middle=(ImageTextButton)findViewById(R.id.middle_button);
                imageTextButton_middle2=(ImageTextButton)findViewById(R.id.middle2_button);
                imageTextButton_right=(ImageTextButton)findViewById(R.id.right_button);
                switch (position){
                    case 0:
                        //数据库
                        imageTextButton_left.setImageResource(R.drawable.database_blue);
                        imageTextButton_left.setTextColor(R.color.blue);
                        imageTextButton_middle.setImageResource(R.drawable.phonebook_black);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.photo_black);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.vedio_black);
                        imageTextButton_right.setTextColor(R.color.black);

                        break;

                    //联系人&短信
                    case 1:

                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.database_black);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.phonebook_blue);
                        imageTextButton_middle.setTextColor(R.color.blue);
                        imageTextButton_middle2.setImageResource(R.drawable.photo_black);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.vedio_black);
                        imageTextButton_right.setTextColor(R.color.black);


                        break;

                    //拍照
                    case 2:
                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.database_black);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.phonebook_black);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.photo_blue);
                        imageTextButton_middle2.setTextColor(R.color.blue);
                        imageTextButton_right.setImageResource(R.drawable.vedio_black);
                        imageTextButton_right.setTextColor(R.color.black);
                        break;

                    //历史
                    case 3:

                        //切换导航栏颜色
                        imageTextButton_left.setImageResource(R.drawable.database_black);
                        imageTextButton_left.setTextColor(R.color.black);
                        imageTextButton_middle.setImageResource(R.drawable.phonebook_black);
                        imageTextButton_middle.setTextColor(R.color.black);
                        imageTextButton_middle2.setImageResource(R.drawable.photo_black);
                        imageTextButton_middle2.setTextColor(R.color.black);
                        imageTextButton_right.setImageResource(R.drawable.vedio_blue);
                        imageTextButton_right.setTextColor(R.color.blue);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initView();


    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //String location=bdLocation.getAddrStr();
            //province=bdLocation.getProvince();
            city=bdLocation.getCity();
            disttrict=bdLocation.getDistrict();
            final SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("city",city);
            editor.putString("city",disttrict);
            editor.apply();
            if(firstEnter){
                //请求天气
                HeWeather.getWeatherNow(getApplicationContext(), city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        Log.i(TAG, "Weather Now onError: ", throwable);
                    }

                    @Override
                    public void onSuccess(Now now) {
                        Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(now));
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                        if ( Code.OK.getCode().equalsIgnoreCase(now.getStatus()) ){
                            //此时返回数据
                            NowBase nowBase = now.getNow();
                            final String weather=nowBase.getCond_txt();
                            final String temperature=nowBase.getTmp();
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("weather",weather);
                            editor.putString("temperature",temperature);
                            editor.apply();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView_location = (TextView) view3.findViewById(R.id.textView_location);
                                    textView_location.setText(city+disttrict);
                                    TextView textView_weather = (TextView) view3.findViewById(R.id.textView_weather);
                                    textView_weather.setText(weather+"  "+temperature+"℃");
                                    ImageView imageView_weather=(ImageView)view3.findViewById(R.id.imageView_weather);
                                    switch (weather){
                                        case "晴":
                                            imageView_weather.setImageResource(R.drawable.sunny);
                                            break;
                                        case "多云":
                                            imageView_weather.setImageResource(R.drawable.cloudy);
                                            break;
                                        case "阴":
                                            imageView_weather.setImageResource(R.drawable.shady);
                                            break;
                                        case "雨":
                                            imageView_weather.setImageResource(R.drawable.rainy);
                                            break;

                                    }
                                }
                            });



                        } else {
                            //在此查看返回数据失败的原因
                            String status = now.getStatus();
                            Code code = Code.toEnum(status);
                            Log.i(TAG, "failed code: " + code);
                        }
                    }
                });
                firstEnter=false;
            }
        }
    }

    public void initView(){
        //设置导航栏
        imageTextButton_left = (ImageTextButton) findViewById(R.id.left_button);
        imageTextButton_left.setText("数据库");
        imageTextButton_left.setImageResource(R.drawable.database_blue);
        imageTextButton_left.setTextSize(10);
        imageTextButton_left.setTextBold();
        imageTextButton_left.setTextColor(R.color.blue);
        imageTextButton_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(0);
            }
        });

        imageTextButton_middle = (ImageTextButton) findViewById(R.id.middle_button);
        imageTextButton_middle.setText("联系人");
        imageTextButton_middle.setImageResource(R.drawable.phonebook_black);
        imageTextButton_middle.setTextSize(10);
        imageTextButton_middle.setTextColor(R.color.black);
        imageTextButton_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(1);
            }
        });

        imageTextButton_middle2=(ImageTextButton)findViewById(R.id.middle2_button);
        imageTextButton_middle2.setText("动态");
        imageTextButton_middle2.setImageResource(R.drawable.photo_black);
        imageTextButton_middle2.setTextSize(10);
        imageTextButton_middle2.setTextColor(R.color.black);
        imageTextButton_middle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(2);
            }
        });

        imageTextButton_right = (ImageTextButton) findViewById(R.id.right_button);
        imageTextButton_right.setText("视频");
        imageTextButton_right.setImageResource(R.drawable.vedio_black);
        imageTextButton_right.setTextSize(10);
        imageTextButton_right.setTextColor(R.color.black);
        imageTextButton_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAnimation(v);
                viewPager.setCurrentItem(3);
            }
        });

        ImageButton imageButton_title_right=(ImageButton)findViewById(R.id.title_right);
        imageButton_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AfterLoginActivity.this,MessageActivity.class);
                startActivity(intent);
            }
        });

        final DragFloatActionButton dragFloatActionButton=(DragFloatActionButton)findViewById(R.id.music_button);
        dragFloatActionButton.setImageResource(R.drawable.play);
        dragFloatActionButton.setSize(FloatingActionButton.SIZE_AUTO);
        dragFloatActionButton.setColorFilter(R.color.colorAccent);
        dragFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor=preferences.edit();
                int mediaState=preferences.getInt("mediaState",BgmService.MUSIC_STOP);
                int musicID=preferences.getInt("musicID",R.raw.heavenly_blessing);
                switch(mediaState){
                    case BgmService.MUSIC_PLAY:
                        if(intent_bgm!=null){
                            stopService(intent_bgm);
                        }
                        dragFloatActionButton.setImageResource(R.drawable.play);
                        sendBroadcastData(BgmService.MUSIC_PAUSE,musicID);
                        editor.putInt("mediaState",BgmService.MUSIC_PAUSE);
                        break;
                    case BgmService.MUSIC_PAUSE:
                    case BgmService.MUSIC_STOP:
                        if(intent_bgm!=null){
                            stopService(intent_bgm);
                        }
                        dragFloatActionButton.setImageResource(R.drawable.pause);
                        sendBroadcastData(BgmService.MUSIC_PLAY,musicID);
                        editor.putInt("mediaState",BgmService.MUSIC_PLAY);

                        //状态栏通知
                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = null;
                        Intent intent1=new Intent(AfterLoginActivity.this, MessageActivity.class);
                        PendingIntent pendingIntent=PendingIntent.getActivity(AfterLoginActivity.this,0,intent1,0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel1 = new NotificationChannel("bgm","BGM消息",NotificationManager.IMPORTANCE_DEFAULT);

                            //通过NotificationManager.createNotificationChannel(channel)方法建立通知渠道
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            //Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
                            Log.i(TAG, channel1.toString());
                            notificationManager.createNotificationChannel(channel1);
                            notification = new NotificationCompat.Builder(AfterLoginActivity.this,"bgm")
                                    .setContentTitle("Bgm").setContentText("wish you heavenly blessings")     //标题 内容
                                    .setWhen(System.currentTimeMillis())         //发送时间
                                    .setContentIntent(pendingIntent)       //点击打开活动
                                    .setSmallIcon(R.drawable.hongzaomantou)   //小图标
                                    .setAutoCancel(true)               //自动消失
                                    .setLights(Color.BLUE,1000,1000)   //LED，颜色，亮起时间，暗去时间
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.hongzaomantou)).build();
                            notificationManager.notify(1110, notification);
                        } else {
                            Toast.makeText(AfterLoginActivity.this,"SDK版本过低，无法显示通知",Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                editor.apply();




            }
        });

        /*
        dragFloatActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int musicID=preferences.getInt("musicID",R.raw.heavenly_blessing);
                SharedPreferences.Editor editor=preferences.edit();
                switch(musicID){
                    case R.raw.heavenly_blessing:
                        dragFloatActionButton.setColorFilter(R.color.lightBlue);
                        musicID=R.raw.lunak;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                    case R.raw.lunak:
                        dragFloatActionButton.setColorFilter(R.color.lightGreen);
                        musicID=R.raw.pagoda;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                    case R.raw.pagoda:
                        dragFloatActionButton.setColorFilter(R.color.colorAccent);
                        musicID=R.raw.heavenly_blessing;
                        editor.putInt("musicID",musicID);
                        editor.apply();
                        break;
                }
                return true;
            }
        });

         */
    }

    public void initView1(){
        /*
        slideRecyclerView_phonebook=(SlideRecyclerView)view1.findViewById(R.id.recycleView_phonebook);
        slideRecyclerView_phonebook.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        slideRecyclerView_phonebook.addItemDecoration(new DividerItemDecoration(AfterLoginActivity.this,DividerItemDecoration.VERTICAL));
        slidePhonebookAdapter=new SlidePhonebookAdapter(this,personArrayList);
        slideRecyclerView_phonebook.setAdapter(slidePhonebookAdapter);
        slidePhonebookAdapter.setOnDeleteClickListener(new SlidePhonebookAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, final int position) {
                //数据库删除
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myDatabaseHelper=new MyDatabaseHelper(getApplicationContext(),"Phonebook.db",null,1);
                        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                        db.delete("Phonebook","name=?",new String[]{personArrayList.get(position).getName()});
                    }
                }).start();
                personArrayList.remove(position);
                slidePhonebookAdapter.notifyDataSetChanged();
                slideRecyclerView_phonebook.closeMenu();
            }
        });

         */

        if(contactThread.isAlive()){
            try{
                contactThread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
            recyclerView_phonebook=(RecyclerView)view1.findViewById(R.id.recycleView_phonebook);
            phonebookAdapter =new PhonebookAdapter(personArrayList,AfterLoginActivity.this);
            layoutManager_phonebook=new LinearLayoutManager(AfterLoginActivity.this);
            recyclerView_phonebook.addItemDecoration(new DividerItemDecoration(AfterLoginActivity.this,DividerItemDecoration.VERTICAL));
            recyclerView_phonebook.setLayoutManager(layoutManager_phonebook);
            recyclerView_phonebook.setAdapter(phonebookAdapter);

            //下拉刷新
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view1.findViewById(R.id.layout_refresh);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    final Thread initDBThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initPhonebook();
                            initContacts();
                        }
                    });
                    initDBThread.start();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            try{
                                initDBThread.join();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    phonebookAdapter =new PhonebookAdapter(personArrayList, AfterLoginActivity.this);
                                    recyclerView_phonebook.setAdapter(phonebookAdapter);
                                }
                            });
                        }
                    };
                    timer.schedule(task, 3000);

                }
            });
        }else{
            recyclerView_phonebook=(RecyclerView)view1.findViewById(R.id.recycleView_phonebook);
            phonebookAdapter =new PhonebookAdapter(personArrayList,AfterLoginActivity.this);
            layoutManager_phonebook=new LinearLayoutManager(AfterLoginActivity.this);
            recyclerView_phonebook.addItemDecoration(new DividerItemDecoration(AfterLoginActivity.this,DividerItemDecoration.VERTICAL));
            recyclerView_phonebook.setLayoutManager(layoutManager_phonebook);
            recyclerView_phonebook.setAdapter(phonebookAdapter);

            //下拉刷新

            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view1.findViewById(R.id.layout_refresh);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    final Thread initDBThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            initPhonebook();
                            initContacts();
                        }
                    });
                    initDBThread.start();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            try{
                                initDBThread.join();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    phonebookAdapter =new PhonebookAdapter(personArrayList, AfterLoginActivity.this);
                                    recyclerView_phonebook.setAdapter(phonebookAdapter);
                                }
                            });
                        }
                    };
                    timer.schedule(task, 4000);

                }
            });
        }





    }

    public void initView2(){
        Button button_sendText=view2.findViewById(R.id.button_sendText);
        button_sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText_targetNumber=(EditText)view2.findViewById(R.id.editText_targetNumber);
                EditText editText_content=(EditText)view2.findViewById(R.id.editText_content);
                String targetNumber=editText_targetNumber.getText().toString();
                String content=editText_content.getText().toString();
                if(targetNumber.equals("")){
                    Toast.makeText(AfterLoginActivity.this,"收件人号码不能为空！",Toast.LENGTH_SHORT).show();
                }else if(content.equals("")){
                    Toast.makeText(AfterLoginActivity.this,"内容不能为空！",Toast.LENGTH_SHORT).show();
                }else{
                    MsmUtils.sendMsm(AfterLoginActivity.this,targetNumber,content);
                    editText_content.setText("");
                    editText_targetNumber.setText("");
                    Toast.makeText(AfterLoginActivity.this,"短信已发送！",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void initView3(){
        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setImageResource(R.drawable.takephoto);
        imageTextButton_space.setText("动态");
        imageTextButton_space.setTextSize(13);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setImageResource(R.drawable.record);
        imageTextButton_record.setTextSize(13);
        imageTextButton_record.setText("日志");
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setImageResource(R.drawable.notebook);
        imageTextButton_notebook.setTextSize(13);
        imageTextButton_notebook.setText("备忘录");
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageView imageView_date=(ImageView)view3.findViewById(R.id.imageView_date);
        final ImageView imageView_picture=(ImageView)view3.findViewById(R.id.imageView_picture);
        final ImageView imageView_note=(ImageView)view3.findViewById(R.id.imageView_note);
        final TextView textView_date=(TextView)view3.findViewById(R.id.textView_date);
        final RelativeLayout relativeLayout_camera=(RelativeLayout)view3.findViewById(R.id.layout_camera);
        final EditText editText_note=(EditText)view3.findViewById(R.id.editText_note);
        final RelativeLayout relativeLayout_submit=(RelativeLayout)view3.findViewById(R.id.layout_submit);

        imageView_date.setVisibility(View.GONE);
        imageView_picture.setVisibility(View.GONE);
        imageView_note.setVisibility(View.GONE);
        textView_date.setVisibility(View.GONE);
        relativeLayout_camera.setVisibility(View.GONE);
        editText_note.setVisibility(View.GONE);
        relativeLayout_submit.setVisibility(View.GONE);

        initSpace();
        recyclerView_space=(RecyclerView)view3.findViewById(R.id.recycleView_space);
        spaceAdapter=new SpaceAdapter(spaceArrayList);
        layoutManager_space=new LinearLayoutManager(AfterLoginActivity.this);
        recyclerView_space.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView_space.setLayoutManager(layoutManager_space);
        recyclerView_space.setAdapter(spaceAdapter);

        //下拉刷新
        final SwipeRefreshLayout swipeRefreshLayout_history = (SwipeRefreshLayout) view3.findViewById(R.id.layout_refresh);
        swipeRefreshLayout_history.setColorSchemeColors(Color.BLUE, Color.RED);
        swipeRefreshLayout_history.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        swipeRefreshLayout_history.setRefreshing(false);
                    }
                };
                timer.schedule(task, 3000);

            }
        });

        Button button_submit=(Button)view3.findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
                final String date=textView_date.getText().toString();
                final String note=editText_note.getText().toString();
                Space space=new Space(date,bitmap_photo,note);
                spaceArrayList.add(0,space);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myDatabaseHelper=new MyDatabaseHelper(AfterLoginActivity.this,"Space.db",null,1,3);
                                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                bitmap_photo.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                                ContentValues contentValues=new ContentValues();
                                contentValues.put("date",date);
                                contentValues.put("note",note);
                                contentValues.put("T_BLOB",byteArrayOutputStream.toByteArray());
                                db.insert("space",null,contentValues);
                            }
                        }).start();

                         */
                        imageTextButton_notebook.setVisibility(View.GONE);
                        imageTextButton_record.setVisibility(View.GONE);
                        imageTextButton_space.setVisibility(View.GONE);
                        imageView_date.setVisibility(View.GONE);
                        imageView_picture.setVisibility(View.GONE);
                        imageView_note.setVisibility(View.GONE);
                        textView_date.setVisibility(View.GONE);
                        relativeLayout_camera.setVisibility(View.GONE);
                        editText_note.setVisibility(View.GONE);
                        relativeLayout_submit.setVisibility(View.GONE);
                        spaceAdapter=new SpaceAdapter(spaceArrayList);
                        recyclerView_space.setAdapter(spaceAdapter);
                    }
                });


            }
        });


        ImageButton imageButton_add=(ImageButton)view3.findViewById(R.id.button_add);
        imageButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAnimation(view);
                if(imageTextButton_notebook.getVisibility()==View.GONE&&imageTextButton_record.getVisibility()==View.GONE&&imageTextButton_space.getVisibility()==View.GONE&&imageView_date.getVisibility()==View.GONE){
                    imageTextButton_notebook.setVisibility(View.VISIBLE);
                    imageTextButton_record.setVisibility(View.VISIBLE);
                    imageTextButton_space.setVisibility(View.VISIBLE);
                }else if(imageView_date.getVisibility()==View.VISIBLE){
                    imageTextButton_notebook.setVisibility(View.GONE);
                    imageTextButton_record.setVisibility(View.GONE);
                    imageTextButton_space.setVisibility(View.GONE);
                    imageView_date.setVisibility(View.GONE);
                    imageView_picture.setVisibility(View.GONE);
                    imageView_note.setVisibility(View.GONE);
                    textView_date.setVisibility(View.GONE);
                    relativeLayout_camera.setVisibility(View.GONE);
                    editText_note.setVisibility(View.GONE);
                    relativeLayout_submit.setVisibility(View.GONE);
                }else if(imageTextButton_record.getVisibility()==View.VISIBLE){
                    imageTextButton_notebook.setVisibility(View.GONE);
                    imageTextButton_record.setVisibility(View.GONE);
                    imageTextButton_space.setVisibility(View.GONE);
                }

            }
        });

        ImageButton imageButton_camera=(ImageButton)view3.findViewById(R.id.button_camera);
        imageButton_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建File，用于存储照片
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(AfterLoginActivity.this,"com.example.asc.fileprovider",outputImage);
                }else {
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

    }

    public void initView4(){
        String url="https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
        final VideoView videoView=(VideoView)view4.findViewById(R.id.videoView);
        //videoView,setMediaController(new MediaController(AfterLoginActivity.this));
        //播放完成回调
        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
        videoView.setVideoURI(Uri.parse(url));
        //videoView.requestFocus();
        final ImageButton button_control=(ImageButton) view4.findViewById(R.id.button_control);
        final DragFloatActionButton dragFloatActionButton=(DragFloatActionButton)findViewById(R.id.music_button);
        button_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(AfterLoginActivity.this);
                SharedPreferences.Editor editor=preferences.edit();
                int musicState=preferences.getInt("mediaState",BgmService.MUSIC_STOP);
                int musicID=preferences.getInt("musicID",R.raw.heavenly_blessing);
                if(!videoView.isPlaying()){
                    if(musicState==BgmService.MUSIC_PLAY){
                        sendBroadcastData(BgmService.MUSIC_PAUSE,musicID);
                        dragFloatActionButton.setImageResource(R.drawable.pause);
                        editor.putInt("mediaState",BgmService.MUSIC_PAUSE);
                        editor.apply();
                    }
                    videoView.start();
                    button_control.setBackgroundResource(R.drawable.pause);
                }else if(videoView.isPlaying()){
                    videoView.pause();
                    button_control.setBackgroundResource(R.drawable.play);
                }else{
                    videoView.resume();
                    button_control.setBackgroundResource(R.drawable.pause);
                }
            }
        });
    }

    /**
     * 播放回调类
     */
    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(AfterLoginActivity.this,"播放结束！",Toast.LENGTH_SHORT).show();
        }
    }

    public void initPhonebook(){
        /*
        personArrayList=new ArrayList<>();
        for(int i=0;i<5;i++){
            Person person=new Person(i+1,"Hilex","----");
            personArrayList.add(person);
        }

         */
        int i=0;
        personArrayList=new ArrayList<>();
        myDatabaseHelper=new MyDatabaseHelper(getApplicationContext(),"Phonebook.db",null,1,1);
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        Cursor cursor=db.query("Phonebook",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            try{
                do{
                    String name=cursor.getString(cursor.getColumnIndex("name"));
                    String phone=cursor.getString(cursor.getColumnIndex("phone"));
                    Person person=new Person(i++,name,phone,Person.CONTACT_PRIVATE);
                    personArrayList.add(0,person);
                }while(cursor.moveToNext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();

    }


    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    //插入数据库
    public void onClickSave(View view){
        //打开数据库
        myDatabaseHelper=new MyDatabaseHelper(view.getContext(),"Phonebook.db",null,1,1);
        EditText editText_name=(EditText)view1.findViewById(R.id.editText_name);
        EditText editText_phone=(EditText)view1.findViewById(R.id.editText_phone);
        String name=editText_name.getText().toString();
        String phone=editText_phone.getText().toString();
        editText_name.setText("");
        editText_phone.setText("");
        //获得当前数据库序号
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int DBid=preferences.getInt("DBid",0);
        Person person=new Person(++DBid,name,phone,Person.CONTACT_PRIVATE);
        //插入数据库
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("phone",phone);
        db.insert("phonebook",null,contentValues);
        Toast.makeText(AfterLoginActivity.this,"成功将联系人保存到数据库!",Toast.LENGTH_SHORT).show();
    }

    public void onClickCancel(View view){
        EditText editText_name=(EditText)view1.findViewById(R.id.editText_name);
        EditText editText_phone=(EditText)view1.findViewById(R.id.editText_phone);
        editText_name.setText("");
        editText_phone.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 2:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(AfterLoginActivity.this,"必须同意全部权限才能完整使用功能！",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(AfterLoginActivity.this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        //每隔10秒更新位置
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        //高精度模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭位置
        mLocationClient.stop();
        //关闭天气服务
        Intent intent_weather=new Intent(this, WeatherService.class);
        stopService(intent_weather);

        //关闭bgm
        if(intent_bgm!=null){
            stopService(intent_bgm);
        }
    }

    private void sendBroadcastData(int type,int musicID) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.putExtra("musicID",musicID);
        intent.setAction("flag");
        sendBroadcast(intent);
    }

    public void initContacts(){
        ContentResolver contentResolver=AfterLoginActivity.this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        Log.d("SmallLetters", ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
        //personArrayList=new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Person person=new Person(personArrayList.size(),name,number,Person.CONTACT_PUBLIC);
                personArrayList.add(0,person);
            }
            //更新数据
            cursor.close();

        }

    }

    public void onClickSpace(View view){
        final String[] date = {""};

        Thread dateThraed=new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH)+1;
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int minute=calendar.get(Calendar.MINUTE);
                date[0] =year+"-"+month+"-"+day+"  "+hour+":"+minute;
            }
        });

        dateThraed.start();

        final ImageTextButton imageTextButton_space=(ImageTextButton)view3.findViewById(R.id.button_space);
        imageTextButton_space.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_record=(ImageTextButton)view3.findViewById(R.id.button_record);
        imageTextButton_record.setVisibility(View.GONE);

        final ImageTextButton imageTextButton_notebook=(ImageTextButton)view3.findViewById(R.id.button_notebook);
        imageTextButton_notebook.setVisibility(View.GONE);

        final ImageView imageView_date=(ImageView)view3.findViewById(R.id.imageView_date);
        final ImageView imageView_picture=(ImageView)view3.findViewById(R.id.imageView_picture);
        final ImageView imageView_note=(ImageView)view3.findViewById(R.id.imageView_note);
        final TextView textView_date=(TextView)view3.findViewById(R.id.textView_date);
        final RelativeLayout relativeLayout_camera=(RelativeLayout)view3.findViewById(R.id.layout_camera);
        final EditText editText_note=(EditText)view3.findViewById(R.id.editText_note);
        RelativeLayout relativeLayout_submit=(RelativeLayout)view3.findViewById(R.id.layout_submit);
        ImageView imageView_photo=(ImageView)view3.findViewById(R.id.imageView_photo);
        ImageButton imageButton_camera=(ImageButton)view3.findViewById(R.id.button_camera);
        imageView_date.setVisibility(View.VISIBLE);
        imageView_picture.setVisibility(View.VISIBLE);
        imageView_note.setVisibility(View.VISIBLE);
        textView_date.setVisibility(View.VISIBLE);
        relativeLayout_camera.setVisibility(View.VISIBLE);
        editText_note.setVisibility(View.VISIBLE);
        relativeLayout_submit.setVisibility(View.VISIBLE);
        imageButton_camera.setVisibility(View.VISIBLE);
        imageView_photo.setVisibility(View.GONE);

        try{
            dateThraed.join();
        }catch (Exception e){
            e.printStackTrace();
        }

        textView_date.setText(date[0]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将照片显示出来
                        bitmap_photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        ImageView imageView=(ImageView)view3.findViewById(R.id.imageView_photo);
                        ImageButton imageButton=(ImageButton)view3.findViewById(R.id.button_camera);
                        imageButton.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bitmap_photo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void initSpace(){
        spaceArrayList=new ArrayList<>();
        /*
        myDatabaseHelper=new MyDatabaseHelper(getApplicationContext(),"Space.db",null,1,3);
        SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
        Cursor cursor=db.query("Space",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            try{
                do{
                    String date=cursor.getString(cursor.getColumnIndex("date"));
                    String note=cursor.getString(cursor.getColumnIndex("note"));
                    byte[] bytes=cursor.getBlob(cursor.getColumnIndex("T_BLOB"));
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Space space=new Space(date,bitmap,note);
                    spaceArrayList.add(0,space);
                }while(cursor.moveToNext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();

         */
    }

    public void onClickVedio(View view){
        if(videoState==VIDEO_PAUSE){

        }
    }

}