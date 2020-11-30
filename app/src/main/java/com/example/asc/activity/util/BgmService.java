package com.example.asc.activity.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.asc.R;

public class BgmService extends Service {
    public MediaPlayer mediaPlayer;
    private int musicID=R.raw.lunak;
    public final static int MUSIC_PLAY=1;
    public final static int MUSIC_PAUSE=2;
    public final static int MUSIC_STOP=3;
    public final static int MUSIC_CHANGE=4;

    public class MediaBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",-1);
            musicID=intent.getIntExtra("musicID",R.raw.lunak);
            switch(type){
                case MUSIC_PLAY:
                    play();
                    break;
                case MUSIC_PAUSE:
                    pause();
                    break;
                case MUSIC_STOP:
                    stop();
                    break;
                case MUSIC_CHANGE:
                    change();
                    break;

            }
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RegisterBroadcast();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void play(){
        if(mediaPlayer==null){
            try{
                mediaPlayer=MediaPlayer.create(this,musicID);
                mediaPlayer.setLooping(true);
                setMediaPrepareAsync();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void pause(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public void change(){
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(this,musicID);
            mediaPlayer.setLooping(true);
            setMediaPrepareAsync();
        }catch(Exception e){
            e.printStackTrace();
        }

    }




    public void setMediaPrepareAsync() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    public void RegisterBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("flag");
        registerReceiver(new MediaBroadcastReceiver(), filter);
    }




}
