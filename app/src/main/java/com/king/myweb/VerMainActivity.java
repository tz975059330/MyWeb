package com.king.myweb;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by 16230 on 2016/9/3.
 */

public class VerMainActivity extends AppCompatActivity {
    //通知窗口更新的handler
    private Handler mHandler;
    //登录按钮
    private Button btnLogin;
    //保存密码选择框
    private CheckBox checkBox;
    //账号输入框
    private EditText editAccount;
    //密码输入框
    private EditText editPassword;
    //验证码输入框
    private EditText editVerification;
    //显示验证码的view
    private ImageView imageVerification;
    //保存验证码的bitmap
    private byte[] byteVerification;
    //保存Cookie的字符串
    private String Cookie;
    //保存__VIEWSTATE字符串
    private String __VIEWSTATE = "dDwyODE2NTM0OTg7Oz5LUCaVfG1Oi%2BQaOSKH9UZrpjfn1w%3D%3D";
    //Android系统下用于数据储存的API
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主布局文件
        setContentView(R.layout.activity_ver);
        //获取实例对象
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        btnLogin = (Button) findViewById(R.id.btn_login);
        editAccount = (EditText) findViewById(R.id.tx_login);
        editPassword = (EditText) findViewById(R.id.tx_passwd);
        editVerification = (EditText) findViewById(R.id.tx_ver);
        imageVerification = (ImageView) findViewById(R.id.img_ver);
        //初始化sharedPreferences
        sharedPreferences = this.getSharedPreferences("config", MODE_PRIVATE);
        //获取sharedPreferences中存储的数据
        String account = sharedPreferences.getString("account", "");
        final String passwd = sharedPreferences.getString("passwd", "");
        //如果能获取到密码，则证明为保存密码状态
        if (passwd != null && !passwd.equals("")) {
            checkBox.setChecked(true);
            editPassword.setText(passwd);
        }
        //设置默认的账号
        editAccount.setText(account);
        //设置账号输入框为默认焦点
        editAccount.setFocusable(true);
        editAccount.setFocusableInTouchMode(true);
        editAccount.requestFocus();
        editAccount.requestFocusFromTouch();
        //获取Cookie和隐藏字符串
        getCookieFromImage();
        //用handler更新
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Util.missAnimation();
                //handler更新UI
                if ((int) msg.obj == 302) {
                    final Intent intent = new Intent();
                    new AlertDialog.Builder(VerMainActivity.this).setTitle("提示").setMessage("登录成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    intent.setClass(VerMainActivity.this, FirstActivity.class);
                                    Data data = new Data(editAccount.getText().toString(),editPassword.getText().toString(),Cookie,null);
                                    intent.putExtra("raw",data);
//                                    System.out.println(""+passwd);
                                    startActivity(intent);
                                    VerMainActivity.this.finish();
                                }
                            }).show();
                } else if ((int) msg.obj == 200) {
                    new AlertDialog.Builder(VerMainActivity.this).setTitle("提示").setMessage("登录失败")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //editAccount.setText("");
                                    editPassword.setText("");
                                    editVerification.setText("");
                                    setImage();
                                    //设置密码输入框为默认焦点
                                    editPassword.setFocusable(true);
                                    editPassword.setFocusableInTouchMode(true);
                                    editPassword.requestFocus();
                                    editPassword.requestFocusFromTouch();
                                }
                            }).show();
                } else if ((int) msg.obj == 1314) {         //更新主界面验证码
                    //给imageView设置图片
                    imageVerification.setImageBitmap(BitmapFactory.decodeByteArray(byteVerification, 0,
                            byteVerification.length));
                    //获取到登录Cookie之后
                }
            }
        };
        //添加点击监听事件
        btnLogin.setOnClickListener(new actionListener());
        imageVerification.setOnClickListener(new actionListener());
    }
    /**
     * 从图片地址获取Cookie
     */
    private void getCookieFromImage(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Constant.url_bitmap);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    conn.setRequestProperty("Accept-Encoding","gzip, deflate");
                    conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
                    conn.setDoInput(true);
                    //conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(10 * 1000);
                    conn.connect();
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
                    byteVerification = outStream.toByteArray();
                    //关闭输入流
                    is.close();
                    if (conn.getHeaderField("Set-Cookie") != null) {
                        String[] temp = conn.getHeaderField("Set-Cookie").split(";");
                        Cookie = temp[0];
                    } else {
                        Cookie = null;
                    }
                    Message msg = new Message();
                    msg.obj = 1314;
                    mHandler.sendMessage(msg);
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 获取验证码
     * 通过Cookie访问验证码页面获取验证码
     */
    private void setImage() {
        final Thread GetBitmapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Constant.url_bitmap);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    conn.setRequestProperty("Accept-Encoding","gzip, deflate");
                    conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
                    conn.setRequestProperty("Host", "222.24.62.120");
                    conn.setRequestProperty("Referer", "http://222.24.62.120/default2.aspx");
                    conn.setRequestMethod("GET");
//                    conn.setDoInput(true);
//                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
//                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(10 * 1000);
                    conn.connect();
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
                    byteVerification = outStream.toByteArray();
                    //关闭输入流
                    is.close();
                    conn.disconnect();
                    //通知handler更新页面
                    Message msg = new Message();
                    msg.obj = 1314;
                    mHandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        GetBitmapThread.start();
    }
    /**
     * 提交表单内容登录
     *
     * @param mAccount
     * @param mPassword
     * @param verification
     */
    private void login(final String mAccount, final String mPassword, final String verification) {
        if (Cookie == null) {
            Message msg = new Message();
            msg.obj = 500;
            mHandler.sendMessage(msg);
            return;
        }
        //如果为选中状态，则保存当前账号信息
        if (checkBox.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account", mAccount);
            editor.putString("passwd", mPassword);
            editor.commit();
        } else {      //否则只保存账号信息
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("account", mAccount);
            editor.commit();
        }
        Thread threadLogin = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Constant.url_login2);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Referer", Constant.url_login2);
                    conn.setRequestProperty("Host", "222.24.62.120");
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
                    StringBuffer sb = new StringBuffer();
                    String viewstate = "__VIEWSTATE=" + __VIEWSTATE;
                    String account = "&txtUserName=" + mAccount;
                    String password = "&TextBox2=" + mPassword;
                    String ver = "&txtSecretCode=" + verification;
                    String radiobutton = "&RadioButtonList1=学生&Button1=&lbLanguage=&hidPdrs=&hidsc=";
                    StringBuffer st = new StringBuffer();
                    st.append(viewstate).append(account).append(password).append(ver).append(radiobutton);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "gb2312");
                    out.write(st.toString());
                    out.flush();
                    out.close();
//                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
//                    String str = "";
//                    while ((str = bf.readLine()) != null) {
//                        System.out.println(str);
//                    }
//                    bf.close();
                    Message msg = new Message();
                    msg.obj = conn.getResponseCode();
                    mHandler.sendMessage(msg);
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadLogin.start();
    }


    /**
     * 点击监听类
     */
    class actionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!Util.isNetworkAvailable(VerMainActivity.this.getApplicationContext())) {
                Toast.makeText(VerMainActivity.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
                return;
            }
            //点击登录按钮的判断
            if (view.getId() == R.id.btn_login) {
                String account = editAccount.getText().toString().replace(" ", "");
                String password = editPassword.getText().toString().replace(" ", "");
                String verification = editVerification.getText().toString().replace(" ", "");
                //全都不为空时进行登录
                if (account != null && !account.equals("") && password != null && !password.equals("") &&
                        verification != null && !verification.equals("")) {
                    Util.showAnimation(VerMainActivity.this,"正在登录...");
                    login(account, password, verification);
                    return;
                } else if (account == null || account.equals("")) {
                    new AlertDialog.Builder(VerMainActivity.this).setTitle("提示").setMessage("账号为空")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editAccount.setText("");
                                    editPassword.setText("");
                                    editVerification.setText("");
                                    setImage();
                                    //设置光标焦点
                                    editAccount.setFocusable(true);
                                    editAccount.setFocusableInTouchMode(true);
                                    editAccount.requestFocus();
                                    editAccount.requestFocusFromTouch();
                                }
                            }).show();
                } else if (password == null || password.equals("")) {
                    new AlertDialog.Builder(VerMainActivity.this).setTitle("提示").setMessage("密码为空")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // editAccount.setText("");
                                    editPassword.setText("");
                                    editVerification.setText("");
                                    setImage();
                                    //设置光标焦点
                                    editPassword.setFocusable(true);
                                    editPassword.setFocusableInTouchMode(true);
                                    editPassword.requestFocus();
                                    editPassword.requestFocusFromTouch();
                                }
                            }).show();
                } else if (verification == null || verification.equals("")) {
                    new AlertDialog.Builder(VerMainActivity.this).setTitle("提示").setMessage("验证码为空")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editPassword.setText("");
                                    editVerification.setText("");
                                    setImage();
                                    //设置光标焦点
                                    editPassword.setFocusable(true);
                                    editPassword.setFocusableInTouchMode(true);
                                    editPassword.requestFocus();
                                    editPassword.requestFocusFromTouch();
                                }
                            }).show();
                }
                //点击验证码图片进行的判断
            } else if (view.getId() == R.id.img_ver) {
                if(Cookie==null){
                    getCookieFromImage();
                    return;

                }
                setImage();
            }
        }
    }
}
