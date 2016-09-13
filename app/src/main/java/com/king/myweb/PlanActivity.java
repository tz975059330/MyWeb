package com.king.myweb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.ElementListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
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
 * Created by 16230 on 2016/9/2.
 */

public class PlanActivity extends AppCompatActivity {

    private String Cookie;
    private String name;
    private String account;
    private String passwd;
    private ExpandableListView mainlistView;
    private List<String> parent;
    private Map<String, List<String>> map;
    private Context context;
    private String __VIEWSTATE;

    private Map<Integer, List<String>> listLession;
    private Handler mHandler;
    //表示现在学生所在学期，并将这项默认展开
    private int Hotel;

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
        //最外层显示学期的容器
        parent = new ArrayList<String>();
        //绑定最外层显示学期 和 最内层显示课程的容器
        map = new HashMap<String, List<String>>();
        //最内层显示课程的容器，Integer表示第几学期
        listLession = new HashMap<Integer, List<String>>();
        context = PlanActivity.this;
        Util.showAnimation(PlanActivity.this, "正在加载信息...");
        getLongString();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //获取到长字符串之后再获取数据
                if((int)msg.obj == 222){
                    findLession();
                }
                //所有信息获取完毕，开始设置默认打开项
                if((int)msg.obj == 333){
                    getSemester();
                }
                //数据和设置完毕，关闭动画，加载监听器
                if ((int) msg.obj == 666) {
                    Util.missAnimation();
                    MyAdapter myadapter = new MyAdapter(context, parent, map);
                    mainlistView.setAdapter(myadapter);
                    mainlistView.expandGroup(Hotel);
                }
            }
        };
    }

    private void getLongString() {
        final Thread threadLong = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_Plan + account + "&xm=" + name + Constant.code_plan;
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
     * 添加课程,获取学期
     * list.add("毛泽东思想和中国特色社会主义理论体系概论"+"    "+"2.0学分"+"    "+"必修"+"    "+"院考");
     *
     * @return
     */
    private void findLession() {

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_Plan + account + "&xm=" + name + Constant.code_plan;
                    String Referer = accessURL;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Referer", Referer);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
//                    System.out.println(conn.toString());
                    //提交表单内容
                    StringBuffer sb = new StringBuffer();
                    String st = "__EVENTTARGET=xq&__EVENTARGUMENT=&__VIEWSTATE=" + __VIEWSTATE + "&xq=全部" +
                            "&kcxz=全部&dpDBGrid:txtChoosePage=1&dpDBGrid:txtPageSize=200";
                    sb.append(st);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "gb2312");
                    out.write(sb.toString());
                    out.flush();
                    out.close();
                    //读取网页内容
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    sb = new StringBuffer();
                    while ((str = bf.readLine()) != null) {
                        sb.append(str);
                    }
                    resolveHTML(sb.toString());
                    bf.close();
                    conn.disconnect();
                    Message msg = new Message();
                    msg.obj = 333;
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
    private void getSemester(){
        //获取当前学期
        final Thread threadNum = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_Plan + account + "&xm=" + name + Constant.code_plan;
                    String Referer = Constant.url_main + account;
                    URL url = new URL(accessURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Cookie", Cookie);
                    conn.setRequestProperty("Referer", Referer);
                    conn.setUseCaches(false);
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();
                    //读取网页内容
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
                    String str = "";
                    StringBuffer sb = new StringBuffer();
                    while ((str = bf.readLine()) != null) {
                        sb.append(str);
                    }
                    bf.close();
                    conn.disconnect();
                    //获取当前所在学期
                    Document document = Jsoup.parse(sb.toString());
                    Element element = document.getElementById("xq");
                    Elements elements = element.select("option[selected]");
                    if (elements != null) {
                        String num = elements.first().text();
                        Hotel = Integer.parseInt(num);
                        Hotel -= 1;
                    }
                    Message msg = new Message();
                    msg.obj = 666;
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
        threadNum.start();
    }
    /**
     * 解析HTML
     */
    private void resolveHTML(String page) {

        Document document = Jsoup.parse(page);
        //获取table 元素
        Element element = document.getElementById("DBGrid");
        //获取table下的唯一孩子结点元素 tbody
        Element tbody = element.child(0);
        //遍历tbody，获取需要的信息(第一个为表头信息，不需要，所以从1开始遍历)table->tbody->tr->td
        if (tbody != null) {
            ArrayList<String> listString = new ArrayList<String>();
            HashSet set = new HashSet();
            for (int i = 1, n = tbody.children().size(); i < n; i++) {
                Element tr = tbody.child(i);
                String title = "第" + tr.child(7).text() + "学期";
                String lessons = tr.child(1).text() + "    " + tr.child(2).text() + "学分" + "    " + tr.child(5).text
                        ().substring(0, 2) + "    " + tr.child(4).text();
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
