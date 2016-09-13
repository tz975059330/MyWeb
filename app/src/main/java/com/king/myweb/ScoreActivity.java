package com.king.myweb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by 16230 on 2016/9/3.
 */

public class ScoreActivity extends AppCompatActivity {

    private String Cookie;
    private String name;
    private String account;
    private String passwd;
    private ExpandableListView mainlistView;
    private List<String> parent;
    private Map<String, List<String>> map;
    private Context context;
    //必须从页面中获取
    private String __VIEWSTATE;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        //获取上个activity传过来的内容
        Intent intent = this.getIntent();
        Data data = (Data) intent.getSerializableExtra("raw");
        account = data.getAccount();
        passwd = data.getPasswd();
        Cookie = data.getCookie();
        name = data.getName();
        mainlistView = (ExpandableListView) findViewById(R.id.expend_list);
        Util.showAnimation(ScoreActivity.this, "加载数据中...");
        getLongString();
        //最外层的容器
        parent = new ArrayList<String>();
        //中间容器
        map = new HashMap<String, List<String>>();
        context = ScoreActivity.this;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if ((int) msg.obj == 233) {
                    Util.missAnimation();
                    MyAdapter myadapter = new MyAdapter(context, parent, map);
                    mainlistView.setAdapter(myadapter);
                }
                //得到长字符串之后再开始获取信息
                if ((int) msg.obj == 222) {
                    getHTML();
                }
            }
        };
    }

    private void getLongString() {
        final Thread threadLong = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_Socre + account + "&xm=" + name + Constant.code_Score;
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
                    //解析页面   获取长字符串
                    Document document = Jsoup.parse(sb.toString());
                    Element element = document.getElementById("Form1");
                    Elements elements = element.select("input[name=__VIEWSTATE]");
                    if (elements != null) {
                        __VIEWSTATE = java.net.URLEncoder.encode(elements.first().val(), "utf-8");
                    }
//                    System.out.println(__VIEWSTATE);
                    conn.disconnect();
                    Message msg = new Message();
                    msg.obj = 222;
                    mHandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadLong.start();
    }

    /**
     * 开启线程，连接网页
     */
    private void getHTML() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String accessURL = Constant.url_Socre + account + "&xm=" + name + Constant.code_Score;
                    String Referer = accessURL;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Referer", Referer);
                    conn.setRequestProperty("Host", "222.24.62.120");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
                    StringBuffer use = new StringBuffer();
                    //提交表单内容
                    use.append("__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=");
                    use.append(__VIEWSTATE);
                    String st = "&hidLanguage=&ddlXN=&ddlXQ=&ddl_kcxz=&btn_zcj=%C0%FA%C4%EA%B3%C9%BC%A8";
                    use.append(st);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "gb2312");
                    out.write(use.toString());
                    out.flush();
                    out.close();
                    //读取网页内容
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    StringBuffer sb = new StringBuffer();
                    while (null != (str = bf.readLine())) {
//                        System.out.println(str);
                        sb.append(str);
                    }
                    bf.close();
                    conn.disconnect();
                    Util.missAnimation();
                    resolveHTML(sb.toString());
                    Message msg = new Message();
                    msg.obj = 233;
                    mHandler.sendMessage(msg);
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
     * 解析HTML
     */
    private void resolveHTML(String page) {
        Document document = Jsoup.parse(page);
        //获取table 元素
        Element element = document.getElementById("Datagrid1");
        //获取table下的唯一孩子结点元素 tbody
        Element tbody = element.child(0);
        //遍历tbody，获取需要的信息(第一个为表头信息，不需要，所以从1开始遍历)table->tbody->tr->td
        if (tbody != null) {
            ArrayList<String> listString = new ArrayList<String>();
            HashSet set = new HashSet();
            for (int i = 1, n = tbody.children().size(); i < n; i++) {
                Element tr = tbody.child(i);
                String title = tr.child(0).text() + "学年第" + tr.child(1).text() + "学期";
                String lessons = tr.child(3).text() + "    " + tr.child(4).text().substring(0, 2) + "    " + tr.child
                        (8).text();
                if (!set.contains(title)) {
                    set.add(title);
                    parent.add(title);
                    listString = new ArrayList<String>();
                    map.put(title, listString);
                } else {
                    listString.add(lessons);
                }

            }
        } else {
            return;
        }

    }

}
