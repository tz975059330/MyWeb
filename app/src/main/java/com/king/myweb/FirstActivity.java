package com.king.myweb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 16230 on 2016/8/19.
 */

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{
    private String Cookie;
    private String account;
    private String name;
    private String passwd;
    private Handler mhandler;
    private ImageView scoreSearch;
    private ImageView mCurriculum;
    private ImageView mPlan;
    private ImageView mNotice;
    private ImageView mInformation;
    private ImageView mPassword;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //获取上个activity传过来的内容
        Intent intent = this.getIntent();
        Data data = (Data) intent.getSerializableExtra("raw");
        account = data.getAccount();
        passwd =data.getPasswd();
        Cookie = data.getCookie();
        name = data.getName();
        //初始化界面6个方块
        initImageView();
        showHomePage();
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };

    }

    /**
     * 初始化界面
     */
    private void initImageView() {
        //获取屏幕宽度
        WindowManager vml = this.getWindowManager();
        int screenWidth = vml.getDefaultDisplay().getWidth();
        //设置成绩方块
        scoreSearch = (ImageView) findViewById(R.id.score_search);
        scoreSearch.setOnClickListener(this);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.score);
        int newSearchWidth = screenWidth/3*2-10;
        bitmap = Bitmap.createScaledBitmap(bitmap,newSearchWidth,newSearchWidth,false);
        scoreSearch.setImageBitmap(bitmap);
        scoreSearch.setPadding(5,5,5,5);
        //设置课表方块
        mCurriculum = (ImageView) findViewById(R.id.curriculum);
        mCurriculum.setOnClickListener(this);
        int newWidth = screenWidth/3-10;
        bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.curriculum);
        bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newWidth,false);
        mCurriculum.setImageBitmap(bitmap);
        mCurriculum.setPadding(5,5,5,5);
        //培养计划
        mPlan = (ImageView) findViewById(R.id.plan);
        mPlan.setOnClickListener(this);
        bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.plan);
        bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newWidth,false);
        mPlan.setImageBitmap(bitmap);
        mPlan.setPadding(5,5,5,5);
        //通知公告
        mNotice = (ImageView) findViewById(R.id.notice);
        mNotice.setOnClickListener(this);
        bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.notice);
        bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newWidth,false);
        mNotice.setImageBitmap(bitmap);
        mNotice.setPadding(5,5,5,5);
        //个人信息
        mInformation = (ImageView) findViewById(R.id.information);
        mInformation.setOnClickListener(this);
        bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.information);
        bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newWidth,false);
        mInformation.setImageBitmap(bitmap);
        mInformation.setPadding(5,5,5,5);
        //修改密码
        mPassword = (ImageView) findViewById(R.id.password);
        mPassword.setOnClickListener(this);
        bitmap = BitmapFactory.decodeResource(this.getResources(),R.mipmap.password);
        bitmap = Bitmap.createScaledBitmap(bitmap,newWidth,newWidth,false);
        mPassword.setImageBitmap(bitmap);
        mPassword.setPadding(5,5,5,5);
    }

    /**
     * 返回键判断
     */
    @Override
    public void onBackPressed() {

        if(mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(getApplicationContext(), "再次点击退出程序", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.score_search:
                showScore();
                break;
            case R.id.curriculum:
                showCurriculum();
                break;
            case R.id.plan:
                showPlan();
                break;
            case R.id.password:
                updatePassword();
                break;
            case R.id.notice:
                break;
            case R.id.information:
                showInformation();
                break;
            default:
                break;
        }
    }

    /**
     *
     * 修改密码窗口
     */
    private void updatePassword() {
        Intent intent = new Intent();
        intent.setClass(FirstActivity.this,ModifyPwdActivity.class);
        Data data = new Data(account,passwd,Cookie,name);
        intent.putExtra("raw",data);
        startActivity(intent);
    }

    /**
     * 显示个人信息
     */
    private void showInformation(){
        Intent intent = new Intent();
        intent.setClass(FirstActivity.this,InformationActivity.class);
        Data data = new Data(account,passwd,Cookie,name);
        intent.putExtra("raw",data);
        startActivity(intent);
    }
    private void showPlan(){
        Intent intent = new Intent();
        intent.setClass(FirstActivity.this,PlanActivity.class);
        Data data = new Data(account,passwd,Cookie,name);
        intent.putExtra("raw",data);
        startActivity(intent);
    }
    private void showCurriculum(){
        Intent intent = new Intent();
        intent.setClass(FirstActivity.this,CurriculumActivity.class);
        Data data = new Data(account,passwd,Cookie,name);
        intent.putExtra("raw",data);
        startActivity(intent);
    }
    private void showScore(){
        Intent intent = new Intent();
        intent.setClass(FirstActivity.this,ScoreActivity.class);
        Data data = new Data(account,passwd,Cookie,name);
        intent.putExtra("raw",data);
        startActivity(intent);
    }
    /**
     * 访问主页面
     */
    private void showHomePage(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String homePage = Constant.url_main+account;
                    URL url = new URL(homePage);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    StringBuffer sb = new StringBuffer();
                    while ((str = bf.readLine()) != null) {
//                        System.out.println(str);
                        sb.append(str);
                    }
                    //从验证码界面登录 没有获取到姓名
                    if(name==null){
                        Document document = Jsoup.parse(sb.toString());
                        Element element = document.getElementById("xhxm");
                        if (element != null) {
                            String[] s = element.text().split("同");
                            name = s[0];
                        }
                    }
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
