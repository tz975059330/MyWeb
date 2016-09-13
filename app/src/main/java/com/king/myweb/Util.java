package com.king.myweb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mingle.widget.ShapeLoadingDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by 16230 on 2016/9/5.
 */

public class Util {
    //显示的加载小动画
    private static ShapeLoadingDialog shapeLoadingDialog;

    /**
     * 判断当前是否有可用网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 显示小动画
     *
     * @param context
     * @param showText
     */
    public static void showAnimation(Context context, String showText) {
        shapeLoadingDialog = new ShapeLoadingDialog(context);
        shapeLoadingDialog.setLoadingText(showText);
        shapeLoadingDialog.show();
    }

    /**
     * 隐藏小动画
     */
    public static void missAnimation() {
        if (shapeLoadingDialog != null) {
            shapeLoadingDialog.dismiss();
        }
    }

    /**
     * MD5加密
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
    public static int getRandom(){
        Random random = new Random();
        int num = random.nextInt(5);
        return num;
    }

}
