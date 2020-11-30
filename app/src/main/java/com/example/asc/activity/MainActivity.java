package com.example.asc.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asc.R;
import com.example.asc.activity.ui.CircleImageView;
import com.example.asc.activity.ui.NbButton;

public class MainActivity extends AppCompatActivity {

    public String studyNum;
    public String password;

    private NbButton button;
    private RelativeLayout rlContent;
    private Handler handler;
    private Animator animator;

    public static int displayWidth;  //屏幕宽度
    public static int displayHeight; //屏幕高度

    private boolean clickLogin=true;//区别于注册

    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置背景亮度
        Drawable drawable=getResources().getDrawable(R.drawable.hualian);
        drawable.setAlpha(200);

        //密码隐藏与显示
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        final EditText editText_studyNum=(EditText)findViewById(R.id.account);
        final EditText editText_password=(EditText)findViewById(R.id.password);
        final CheckBox checkBox_hide=(CheckBox)findViewById(R.id.hide_pwd);
        checkBox_hide.setChecked(true);
        editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        checkBox_hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        //登录
        final CheckBox checkBox_rememerPwd=(CheckBox)findViewById(R.id.remember_pwd);
        //editText_studyNum.setText(preferences.getString("studyNum"," "));
        if(preferences.getBoolean("rememberPassword",false)){
            editText_studyNum.setText(preferences.getString("studyNum"," "));
            editText_password.setText(preferences.getString("password"," "));
            checkBox_rememerPwd.setChecked(true);
        }

        handler=new Handler();
        rlContent=(RelativeLayout)findViewById(R.id.parent);
        rlContent.getBackground().setAlpha(0);
        button=(NbButton)findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyNum=editText_studyNum.getText().toString();
                password=editText_password.getText().toString();
                //登录成功
                if(studyNum.equals(preferences.getString("studyNum",""))&&password.equals(preferences.getString("password",""))){
                    //testList();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clickLogin=true;
                            SharedPreferences.Editor editor=preferences.edit();
                            if(checkBox_rememerPwd.isChecked()){
                                editor.putBoolean("rememberPassword",true);
                                editor.putString("studyNum",studyNum);
                                editor.putString("password",password);
                            }else{
                                editor.putBoolean("rememberPassword",false);
                                editor.putString("studyNum",studyNum);
                                editor.putString("password",password);
                            }
                            //预加载历史，初始化任务池,请求科普
                            editor.apply();

                        }
                    }).start();
                    Toast.makeText(MainActivity.this,"登录成功！", Toast.LENGTH_SHORT).show();
                    //Intent intent=new Intent(MainActivity.this,AfterLginActivity.class);
                    //startActivity(intent);
                    button.startAnim();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //转圈圈
                            gotoNew();
                        }
                    },2000);


                }else{
                    //登录失败
                    Toast.makeText(MainActivity.this,"账户密码不正确!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //登录动画

    private void gotoNew() {
        button.gotoNew();

        final Intent intent=new Intent(MainActivity.this,AfterLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setClass(MainActivity.this,AfterLoginActivity.class);

        int xc=(button.getLeft()+button.getRight())/2;
        int yc=(button.getTop()+button.getBottom())/2;
        animator= ViewAnimationUtils.createCircularReveal(rlContent,xc,yc,0,1900);
        //转完圈后的持续时间
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);

                    }
                },400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        CircleImageView circleImageView=(CircleImageView)findViewById(R.id.head_picture);
        circleImageView.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_account=(LinearLayout)findViewById(R.id.mid1);
        LinearLayout linearLayout_password=(LinearLayout)findViewById(R.id.mid2);
        linearLayout_account.setVisibility(View.INVISIBLE);
        linearLayout_password.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_checkbox=(LinearLayout)findViewById(R.id.checkbox);
        linearLayout_checkbox.setVisibility(View.INVISIBLE);
        RelativeLayout relativeLayout_login=(RelativeLayout)findViewById(R.id.buttom);
        relativeLayout_login.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_bottom1=(LinearLayout)findViewById(R.id.bottom1);
        linearLayout_bottom1.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout_bottom2=(LinearLayout)findViewById(R.id.bottom2);
        linearLayout_bottom2.setVisibility(View.INVISIBLE);
        rlContent.getBackground().setAlpha(255);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(clickLogin){
            animator.cancel();
            rlContent.getBackground().setAlpha(0);
            button.regainBackground();
        }
    }
}