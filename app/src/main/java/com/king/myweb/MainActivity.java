package com.king.myweb;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
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
    //保存Cookie的字符串
    private String Cookie;
    //保存登录用户姓名的字符串
    private String name;
    //保存__VIEWSTATE字符串
    private String __VIEWSTATE = "dDwyODE2NTM0OTg7Oz5LUCaVfG1Oi%2BQaOSKH9UZrpjfn1w%3D%3D";
    //Android系统下用于数据储存的API
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主布局文件
        setContentView(R.layout.activity_main);
        //获取实例对象
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        btnLogin = (Button) findViewById(R.id.btn_login);
        editAccount = (EditText) findViewById(R.id.tx_login);
        editPassword = (EditText) findViewById(R.id.tx_passwd);
        //初始化sharedPreferences
        sharedPreferences = this.getSharedPreferences("config", MODE_PRIVATE);
        //获取sharedPreferences中存储的数据
        final String account = sharedPreferences.getString("account", "");
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
        //网络请求时的加载动画
        //用handler更新
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Util.missAnimation();
                if ((int) msg.obj == 200) {
                    final Intent intent = new Intent();
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("登录成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    intent.setClass(MainActivity.this, FirstActivity.class);
                                    Data data = new Data(account,passwd,Cookie,name);
                                    intent.putExtra("raw",data);
                                    startActivity(intent);
                                    MainActivity.this.finish();
                                }
                            }).show();
                } else if ((int) msg.obj == 3344) {
                    final Intent intent = new Intent();
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("登录失败，本次登录需要验证码")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    intent.setClass(MainActivity.this, VerMainActivity.class);
                                    startActivity(intent);
                                    MainActivity.this.finish();
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("登录失败")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //editAccount.setText("");
                                    editPassword.setText("");
                                    //设置账号输入框为默认焦点
                                    editAccount.setFocusable(true);
                                    editAccount.setFocusableInTouchMode(true);
                                    editAccount.requestFocus();
                                    editAccount.requestFocusFromTouch();
                                }
                            }).show();
                }
            }
        };
        //登录按钮之后的判断
        btnLogin.setOnClickListener(new buttonClickListener());
    }

    /**
     * 获取登录Cookie
     */
    private void getCookie(final String mAccount, final String mPassword) {

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Constant.URL_GETCOOKIE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(10 * 1000);
                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                    conn.connect();
                    String viewstate = "__VIEWSTATE="+__VIEWSTATE;
                    String account = "&TextBox1=" + mAccount;
                    String password = "&TextBox2=" + mPassword;
                    String radiobutton = "&RadioButtonList1=%25D1%25A7%25C9%25FA";
                    String button1 = "&Button1=%2B%25B5%25C7%2B%25C2%25BC%2B";
                    StringBuffer st = new StringBuffer();
                    st.append(viewstate).append(account).append(password).append(radiobutton).append(button1);
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

                    if (conn.getHeaderField("Set-Cookie") != null) {
                        String[] temp = conn.getHeaderField("Set-Cookie").split(";");
                        Cookie = temp[0];
                    } else {
                        Cookie = null;
                    }
                    conn.disconnect();
                    //用cookie访问
                    login(mAccount, mPassword);
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
     * 用cookie直接获取登录
     */
    private void login(final String account, final String mPassword) {
        if (Cookie == null) {
            Message msg = new Message();
            msg.obj = 3344;
            mHandler.sendMessage(msg);
            return;
        }
        //如果为选中状态，则保存当前账号信息
        if (checkBox.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account", account);
            editor.putString("passwd", mPassword);
            editor.commit();
        } else {      //否则只保存账号信息
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("account", account);
            editor.commit();
        }
        Thread threadLogin = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String theURL = Constant.url_login + account;
                    URL url = new URL(theURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(10 * 1000);
                    conn.connect();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    StringBuffer sb = new StringBuffer();
                    while ((str = bf.readLine()) != null) {
                        //System.out.println(str);
                        sb.append(str);
                    }
                    Document document = Jsoup.parse(sb.toString());
                    Element element = document.getElementById("xhxm");
                    if (element != null) {
                        String[] s = element.text().split("同");
                        name = s[0];
                    }
                    bf.close();
                    Message msg = new Message();
                    msg.obj = conn.getResponseCode();
                    //msg.obj = 200;
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

    class buttonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (!Util.isNetworkAvailable(MainActivity.this.getApplicationContext())) {
                Toast.makeText(MainActivity.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
                return;
            }
            String account = editAccount.getText().toString().replace(" ", "");
            String password = editPassword.getText().toString().replace(" ", "");
            if (account != null && !account.equals("") && password != null && !password.equals("")) {
                Util.showAnimation(MainActivity.this,"正在登录...");
                getCookie(account, password);
                return;
            } else if (account == null || account.equals("")) {
                new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("账号为空")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editAccount.setText("");
                                editPassword.setText("");
                                //设置光标焦点
                                editAccount.setFocusable(true);
                                editAccount.setFocusableInTouchMode(true);
                                editAccount.requestFocus();
                                editAccount.requestFocusFromTouch();
                            }
                        }).show();
            } else if (password == null || password.equals("")) {
                new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("密码为空")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //editAccount.setText("");
                                editPassword.setText("");
                                //设置光标焦点
                                editPassword.setFocusable(true);
                                editPassword.setFocusableInTouchMode(true);
                                editPassword.requestFocus();
                                editPassword.requestFocusFromTouch();
                            }
                        }).show();
            }
        }
    }
}
