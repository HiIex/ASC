package com.example.asc.activity.util;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherService extends Service {

    private String TAG="Weather";
    private Timer timer=new Timer();
    private String response=null;

    public WeatherService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        //final String hash=applySha256(preferences.getString("studyNum","")+preferences.getString("password",""));
        final TimerTask task=new TimerTask() {
            @Override
            public void run() {
                /*
                Thread httpThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        response=httpGet("http://175.24.119.47:12345/broadcast_student?student_hash="+hash);
                    }
                });
                httpThread.start();

                 */
                String city=preferences.getString("city","");
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
                            String weather=nowBase.getCond_txt();
                            String temperature=nowBase.getTmp();
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("weather",weather);
                            editor.putString("temperature",temperature);
                            editor.apply();

                        } else {
                            //在此查看返回数据失败的原因
                            String status = now.getStatus();
                            Code code = Code.toEnum(status);
                            Log.i(TAG, "failed code: " + code);
                        }
                    }
                });

                /*
                Gson gson=new Gson();
                Type type=new TypeToken<BroadcastResponse>(){}.getType();
                try{
                    httpThread.join();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(response!=null){
                    BroadcastResponse broadcastResponse=gson.fromJson(response,type);
                    if(broadcastResponse.getFlag()==1){
                        //Toast.makeText(getApplicationContext(),"请求广播成功",Toast.LENGTH_SHORT).show();
                        AfterLginActivity.jobArrayList.add(0,broadcastResponse);
                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putBoolean("receive_new",true);
                        editor.apply();
                    }
                }

                 */

            }
        };
        //5分钟更新一次天气
        timer.schedule(task,3000,5*60*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    //生成哈希
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String httpGet(String requestURL){
        String result=null;
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        try{
            URL url=new URL(requestURL);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "Content-Type=application/x-www-form-urlencoded");
            //设置客户端与服务连接类型
            connection.addRequestProperty("Connection", "Keep-Alive");
            //connection.setUseCaches(false);
            //connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.connect();

            if(connection.getResponseCode()== HttpURLConnection.HTTP_OK){
                InputStream inputStream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response=new StringBuilder();
                String line=null;
                while((line=reader.readLine())!=null){
                    response.append(line);
                    //response.append("\n");
                }
                inputStream.close();
                result=response.toString();
            }else{
                result="error";
            }

        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        return result;
    }
}