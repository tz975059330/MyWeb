package com.king.myweb;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 16230 on 2016/9/2.
 */

public class InformationActivity extends AppCompatActivity {

    private String strNumber;
    private String strName;
    private String strSex;
    private String strBirthday;
    private String strNation;
    private String strPolitical;
    private String strCollege;
    private String strSpecial;
    private String strClass;
    private String strSchoolSystem;
    private String strEducation;
    private String strLevel;

    private TextView tvNumber;
    private TextView tvName;
    private TextView tvSex;
    private TextView tvBirthday;
    private TextView tvNation;
    private TextView tvPolitical;
    private TextView tvCollege;
    private TextView tvSpecial;
    private TextView tvClass;
    private TextView tvSchoolSystem;
    private TextView tvEducation;
    private TextView tvLevel;
    private ImageView imgPicture;
    private byte[] bytePicture;

    private String Cookie;
    private String name;
    private String account;
    private String passwd;
    private Button btnConfirm;
    private TableLayout tableLayout;
    private Handler mHandler;

    private String url_getPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //获取上个activity传过来的内容
        Intent intent = this.getIntent();
        Data data = (Data) intent.getSerializableExtra("raw");
        account = data.getAccount();
        passwd = data.getPasswd();
        Cookie = data.getCookie();
        name = data.getName();
        //初始化界面
        initView();
        //加载信息
        getInformation();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if ((int)msg.obj == 123) {
                    loadingText();
                }
                if((int)msg.obj == 321){
                    imgPicture.setImageBitmap(BitmapFactory.decodeByteArray(bytePicture,0,bytePicture.length));
                }
            }
        };
    }

    /**
     * 加载界面
     */
    private void initView() {
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformationActivity.this.finish();
            }
        });
        //获取焦点  防止进入界面弹出键盘
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);
        tableLayout.setFocusable(true);
        tableLayout.setFocusableInTouchMode(true);
        tableLayout.requestFocus();
        tableLayout.requestFocusFromTouch();
        tvNumber = (TextView) findViewById(R.id.studentID);
        tvName = (TextView) findViewById(R.id.name);
        tvSex = (TextView) findViewById(R.id.sex);
        tvBirthday = (TextView) findViewById(R.id.birthday);
        tvNation = (TextView) findViewById(R.id.nation);
        tvPolitical = (TextView) findViewById(R.id.political);
        tvCollege = (TextView) findViewById(R.id.college);
        tvSpecial = (TextView) findViewById(R.id.subject);
        tvClass = (TextView) findViewById(R.id.classes);
        tvSchoolSystem = (TextView) findViewById(R.id.schoolSystem);
        tvEducation = (TextView) findViewById(R.id.education);
        tvLevel = (TextView) findViewById(R.id.grade);
        imgPicture = (ImageView) findViewById(R.id.photo);
        imgPicture.setPadding(0,20,0,0);
    }

    /**
     * 界面更新
     */
    private void loadingText() {
        tvNumber.setText(strNumber);
        tvName.setText(strName);
        tvSex.setText(strSex);
        tvBirthday.setText(strBirthday);
        tvNation.setText(strNation);
        tvPolitical.setText(strPolitical);
        tvCollege.setText(strCollege);
        tvSpecial.setText(strSpecial);
        tvClass.setText(strClass);
        tvSchoolSystem.setText(strSchoolSystem);
        tvEducation.setText(strEducation);
        tvLevel.setText(strLevel);
    }

    /**
     *
     * 获取信息界面
     */
    private void getInformation() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_information + account + "&xm=" + name + Constant.code_information;
                    String Referer = Constant.url_main + account;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Referer", Referer);
                    conn.setConnectTimeout(5 * 1000);
                    conn.setUseCaches(false);
                    conn.connect();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    StringBuilder sb = new StringBuilder();
                    while ((str = bf.readLine()) != null) {
                        sb.append(str.trim());
                    }
                    bf.close();
                    //解析页面   获取信息
                    resolveHTML(sb.toString());
                    conn.disconnect();
                    //当上面函数完成后  通知handler更新界面
                    Message msg = new Message();
                    msg.obj = 123;
                    mHandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    /**
     * 获取个人照片
     */
    private void getImage(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_host +url_getPhoto;
//                    System.out.println("获取图片地址"+accessURL);
                    String Referer = Constant.url_information + account + "&xm=" + name + Constant.code_information;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Host",Constant.Host);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Referer", Referer);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
//                    System.out.println(conn.toString());
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    //设置一个缓冲区
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    //判断输入流长度是否等于-1，即非空
                    while ((len = is.read(buffer)) != -1) {
                        //把缓冲区的内容写入到输出流中，从0开始读取，长度为len
                        outStream.write(buffer, 0, len);
                    }
                    bytePicture = outStream.toByteArray();
                    //关闭输入流
                    is.close();
                    conn.disconnect();
                    //当上面函数完成后  通知handler更新界面
                    Message msg = new Message();
                    msg.obj = 321;
                    mHandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 解析HTML页面
     *
     * @param page
     */
    private void resolveHTML(String page) {

        Document document = Jsoup.parse(page);
        Element element = document.getElementById("xh");
        strNumber = element.text();
        element = document.getElementById("xm");
        strName = element.text();
        element = document.getElementById("lbl_xb");
        strSex = element.text();
        element = document.getElementById("lbl_csrq");
        strBirthday = element.text();
        element = document.getElementById("lbl_mz");
        strNation = element.text();
        element = document.getElementById("lbl_zzmm");
        strPolitical = element.text();
        element = document.getElementById("lbl_xy");
        strCollege = element.text();
        element = document.getElementById("lbl_zymc");
        strSpecial = element.text();
        element = document.getElementById("lbl_xzb");
        strClass = element.text();
        element = document.getElementById("lbl_xz");
        strSchoolSystem = element.text();
        element = document.getElementById("lbl_CC");
        strEducation = element.text();
        element = document.getElementById("lbl_dqszj");
        strLevel = element.text();
        //获取照片链接
        element = document.getElementById("xszp");
        url_getPhoto = element.attr("src");
        //获取到链接之后设置图片
        if(url_getPhoto!=null && !url_getPhoto.equals("") && url_getPhoto!=""){
            getImage();
        }
    }
}
