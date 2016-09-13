package com.king.myweb;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by 16230 on 2016/9/2.
 */

public class CurriculumActivity extends AppCompatActivity {
    private String Cookie;
    private String name;
    private String account;
    private String passwd;
    /**
     * 第一个无内容的格子
     */
    protected TextView empty;
    /**
     * 星期一的格子
     */
    protected TextView monColum;
    /**
     * 星期二的格子
     */
    protected TextView tueColum;
    /**
     * 星期三的格子
     */
    protected TextView wedColum;
    /**
     * 星期四的格子
     */
    protected TextView thrusColum;
    /**
     * 星期五的格子
     */
    protected TextView friColum;
    /**
     * 星期六的格子
     */
    protected TextView satColum;
    /**
     * 星期日的格子
     */
    protected TextView sunColum;
    /**
     * 课程表body部分布局
     */
    protected RelativeLayout course_table_layout;
    /**
     * 屏幕宽度
     **/
    protected int screenWidth;
    /**
     * 课程格子平均宽度
     **/
    protected int aveWidth;


    private int gridHeight;
    private Handler mHandler;
    private HashMap<String, Course> map;
    //五种颜色的背景
    int[] background = {R.drawable.course_info_blue, R.drawable.course_info_green, R.drawable.course_info_red, R
            .drawable.course_info_red, R.drawable.course_info_yellow};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum);
        //获取上个activity传过来的内容
        Intent intent = this.getIntent();
        Data data = (Data) intent.getSerializableExtra("raw");
        account = data.getAccount();
        passwd = data.getPasswd();
        Cookie = data.getCookie();
        name = data.getName();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if ((int) msg.obj == 333) {
                    getCourse();
                }
                if ((int) msg.obj == 666) {
                    setCourseView();
                    for (Map.Entry<String, Course> entry : map.entrySet()) {
                        String str = entry.getKey();
                        Course course = entry.getValue();
                        int week = Integer.parseInt(str.substring(1, 2));
                        int startSection = Integer.parseInt(str.substring(4, 5));
                        int endSection = Integer.parseInt(str.substring(8, 9));
                        String courseName = course.getCourseName();
                        String classRoom = course.getClasssroom();
//                        System.out.println(course);
                        String everyWeek = "";
                        if (course.getEveryWeek() == 0) {
                            everyWeek = "单双周";
                        } else if (course.getEveryWeek() == 1) {
                            everyWeek = "单周";
                        } else if (course.getEveryWeek() == 2) {
                            everyWeek = "双周";
                        }
                        String information = course.getCourseName() + "," + course.getClasssroom() + "," + course
                                .getTeacher() + "," + everyWeek;
                        addCourse(week, startSection, endSection, courseName, classRoom, information);
                    }
                    Util.missAnimation();
                }
            }
        };
        init();
        Util.showAnimation(CurriculumActivity.this, "正在加载数据...");
    }

    private void init() {
        //获得列头的控件
        empty = (TextView) this.findViewById(R.id.test_empty);
        monColum = (TextView) this.findViewById(R.id.test_monday_course);
        tueColum = (TextView) this.findViewById(R.id.test_tuesday_course);
        wedColum = (TextView) this.findViewById(R.id.test_wednesday_course);
        thrusColum = (TextView) this.findViewById(R.id.test_thursday_course);
        friColum = (TextView) this.findViewById(R.id.test_friday_course);
        satColum = (TextView) this.findViewById(R.id.test_saturday_course);
        sunColum = (TextView) this.findViewById(R.id.test_sunday_course);
        course_table_layout = (RelativeLayout) this.findViewById(R.id.test_course_rl);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        int aveWidth = width / 8;
        //第一个空白格子设置为25宽
        empty.setWidth(aveWidth * 3 / 4);
        monColum.setWidth(aveWidth * 33 / 32 + 1);
        tueColum.setWidth(aveWidth * 33 / 32 + 1);
        wedColum.setWidth(aveWidth * 33 / 32 + 1);
        thrusColum.setWidth(aveWidth * 33 / 32 + 1);
        friColum.setWidth(aveWidth * 33 / 32 + 1);
        satColum.setWidth(aveWidth * 33 / 32 + 1);
        sunColum.setWidth(aveWidth * 33 / 32 + 1);
        this.screenWidth = width;
        this.aveWidth = aveWidth;
        int height = dm.heightPixels;
        gridHeight = height / 12;
        //通知开始添加课表信息
        Message msg = new Message();
        msg.obj = 333;
        mHandler.sendMessage(msg);
    }

    /**
     * 设置课表界面
     */
    private void setCourseView() {
        //设置课表界面
        //动态生成12 * maxCourseNum个textview
        for (int i = 1; i <= 12; i++) {

            for (int j = 1; j <= 8; j++) {

                TextView tx = new TextView(CurriculumActivity.this);
                tx.setId((i - 1) * 8 + j);
                //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if (j < 8)
                    tx.setBackground(ContextCompat.getDrawable(CurriculumActivity.this.getBaseContext(), R.drawable
                            .course_text_view_bg));
                else
                    tx.setBackground(ContextCompat.getDrawable(CurriculumActivity.this.getBaseContext(), R.drawable
                            .course_table_last_colum));
                //相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(aveWidth * 33 / 32 + 1, gridHeight);
                //文字对齐方式
                tx.setGravity(Gravity.CENTER);
                //如果是第一列，需要设置课的序号（1 到 12）
                if (j == 1) {
                    tx.setText(String.valueOf(i));
                    rp.width = aveWidth * 3 / 4;
                    //设置他们的相对位置
                    if (i == 1) rp.addRule(RelativeLayout.BELOW, empty.getId());
                    else rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                } else {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
                    tx.setText("");
                }

                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
            }
        }
    }

    /**
     * 添加课程信息
     */
    private void addCourse(int week, int startSection, int endSection, String course, String classroom, final String
            allInformation) {
        // 添加课程信息
        TextView courseInfo = new TextView(this);
        courseInfo.setText(course + "\n@" + classroom);
        courseInfo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(CurriculumActivity.this).setTitle("详细").setMessage(allInformation)
                        .setPositiveButton("确定", null).show();
            }
        });
        //该textview的高度根据其节数的跨度来设置
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(aveWidth * 31 / 32, (gridHeight - 3) *
                (endSection - startSection + 1));
        //textview的位置由课程开始节数和上课的时间（day of week）确定
        rlp.topMargin = week + (startSection - 1) * gridHeight;
        rlp.leftMargin = 3;
        // 偏移由这节课是星期几决定
        rlp.addRule(RelativeLayout.RIGHT_OF, week);
        //字体剧中
        courseInfo.setGravity(Gravity.CENTER);
        // 设置一种背景
        courseInfo.setBackgroundResource(background[Util.getRandom()]);
        courseInfo.setTextSize(12);
        courseInfo.setLayoutParams(rlp);
        courseInfo.setTextColor(Color.WHITE);
        //设置不透明度
        courseInfo.getBackground().setAlpha(222);
        course_table_layout.addView(courseInfo);
    }

    private void getCourse() {
        //获取课表
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessURL = Constant.url_Course + account + "&xm=" + name + Constant.code_Course;
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
                    resolveHTML(sb.toString());
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
        CourseService getCourse = new CourseService();
        getCourse.parseCourse(page);
        map = getCourse.getReMap();
        Message msg = new Message();
        msg.obj = 666;
        mHandler.sendMessage(msg);
    }

}
