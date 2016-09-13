package com.king.myweb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 16230 on 2016/9/1.
 */

public class ModifyPwdActivity extends AppCompatActivity {

    private String account;
    private String passwd;
    private String Cookie;
    private String name;
    private String __VIEWSTATE = "dDwxMDIyOTMyNDk0Ozs%2bdO0fUzY5QiZk%2bcYCOy9yNUvaNBE%3d";
    private EditText oldPasswd;
    private EditText newPasswd;
    private EditText againPasswd;
    private Button btnUpdate;
    //匹配密码的正则式
    static final String MATCH = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,16}$";
    private Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypwd);
        //获取上个activity传过来的内容
        Intent intent = this.getIntent();
        Data data = (Data) intent.getSerializableExtra("raw");
        account = data.getAccount();
        passwd =data.getPasswd();
        Cookie = data.getCookie();
        name = data.getName();

        //实例化对象
        oldPasswd = (EditText) findViewById(R.id.tx_oldpasswd);
        newPasswd = (EditText) findViewById(R.id.tx_newpasswd);
        againPasswd = (EditText) findViewById(R.id.tx_againpasswd);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        //按钮添加监听
        btnUpdate.setOnClickListener(new myOnclickListener());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Util.missAnimation();
                if((int)msg.obj==200) {
                    new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("修改成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   passwd = newPasswd.getText().toString();
                                }
                            }).show();
                }else{
                    new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("修改失败")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        };
    }
    /**
     * 提交修改密码表单
     * @param mOldpasswd
     * @param mNewpasswd
     * @param againPasswd
     */
    private void updatePassword(final String mOldpasswd, final String mNewpasswd, final String againPasswd) {

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_password+account+Constant.code_password;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie",Cookie);
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
                    conn.setRequestProperty("Referer",accessURL);
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
                    String viewstate = "__VIEWSTATE="+__VIEWSTATE;
                    String TextBox2 = "&TextBox2=" + mOldpasswd;
                    String TextBox3 = "&TextBox3=" + mNewpasswd;
                    String TextBox4 = "&Textbox4=" + againPasswd;
                    String button1 = "&Button1=修 改";
                    StringBuffer sb = new StringBuffer();
                    sb.append(viewstate).append(TextBox2).append(TextBox3).append(TextBox4).append(button1);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),"gb2312");
                    out.write(sb.toString());
                    out.flush();
                    out.close();

//                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
//                    String str = "";
//                    while ((str = bf.readLine()) != null) {
//                        System.out.println(str);
//                    }
                    Message msg = new Message();
                    msg.obj = conn.getResponseCode();
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
    class myOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String mOldpasswd = oldPasswd.getText().toString();
            String mNewpasswd = newPasswd.getText().toString();
            String mAgainpasswd = againPasswd.getText().toString();
            Pattern pattern = Pattern.compile(MATCH);
            Matcher matcher = pattern.matcher(mNewpasswd);
            if(!mOldpasswd.equals(passwd)){
                new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("原密码输入错误")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //原密码输入错误，直接全部重置
                                oldPasswd.setText("");
                                newPasswd.setText("");
                                againPasswd.setText("");
                                oldPasswd.setFocusable(true);
                                oldPasswd.setFocusableInTouchMode(true);
                                oldPasswd.requestFocus();
                                oldPasswd.requestFocusFromTouch();
                            }
                        }).show();
                return;
            }else if(!matcher.matches()){
                new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("新密码格式错误")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //新密码错误
                                newPasswd.setText("");
                                againPasswd.setText("");
                                newPasswd.setFocusable(true);
                                newPasswd.setFocusableInTouchMode(true);
                                newPasswd.requestFocus();
                                newPasswd.requestFocusFromTouch();
                            }
                        }).show();
                return;
            }else if(!mNewpasswd.equals(mAgainpasswd)){
                new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("两次输入内容不同")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newPasswd.setText("");
                                againPasswd.setText("");
                                newPasswd.setFocusable(true);
                                newPasswd.setFocusableInTouchMode(true);
                                newPasswd.requestFocus();
                                newPasswd.requestFocusFromTouch();
                            }
                        }).show();
                return;
            }else if(mOldpasswd.equals(mNewpasswd)){
                new AlertDialog.Builder(ModifyPwdActivity.this).setTitle("提示").setMessage("新密码必须和原密码不同")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newPasswd.setText("");
                                againPasswd.setText("");
                                newPasswd.setFocusable(true);
                                newPasswd.setFocusableInTouchMode(true);
                                newPasswd.requestFocus();
                                newPasswd.requestFocusFromTouch();
                            }
                        }).show();
                return;
            }else{
                Util.showAnimation(ModifyPwdActivity.this,"修改中...");
                updatePassword(mOldpasswd,mNewpasswd,mAgainpasswd);
            }
        }
    }
}
