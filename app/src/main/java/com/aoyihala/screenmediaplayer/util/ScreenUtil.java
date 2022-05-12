package com.aoyihala.screenmediaplayer.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ScreenUtil {

    /**
     * 模拟屏幕点击事件，点击坐标（x,y）
     *
     * @param x
     * @param y
     */
    public static float[] click(float x, float y) {
        x = x*(1080f/640f);
        y = y*(1920f/640f);
        Log.e("点击的坐标",x+" "+y);
        float[] xy = {x, y};
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        List<String> commands = new ArrayList<String>();
        commands.add("input");
        commands.add("tap");
        commands.add("" + x);
        commands.add("" + y);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process prs = pb.start();
            is = prs.getInputStream();
            byte[] b = new byte[1024];
            int size = 0;
            baos = new ByteArrayOutputStream();
            while ((size = is.read(b)) != -1) {
                baos.write(b, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (baos != null) baos.close();
            } catch (Exception ex) {
            }
        }
        return xy;
    }

    /**
     * 模拟屏幕滑动事件，从（x,y）滑动到（newX，newY）
     *
     * @param x
     * @param y
     * @param newX
     * @param newY
     */
    public static void slide(int x, int y, int newX, int newY) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        List<String> commands = new ArrayList<String>();
        commands.add("input");
        commands.add("swip");
        commands.add("" + x);
        commands.add("" + y);
        commands.add("" + newX);
        commands.add("" + newY);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process prs = pb.start();
            is = prs.getInputStream();
            byte[] b = new byte[1024];
            int size = 0;
            baos = new ByteArrayOutputStream();
            while ((size = is.read(b)) != -1) {
                baos.write(b, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Sim", "slide: " + e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
                if (baos != null) baos.close();
            } catch (Exception ex) {
                Log.d("Sim", "slide: " + ex.getMessage());
            }
        }
    }

}

