package com.sty.source.look.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Shi Tianyi on 2017/10/16/0016.
 */

public class StreamUtils {

    public static String streamToString(InputStream in){
        String result = "";

        try{
            //创建一个字节数组写入流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1){
                out.write(buffer, 0, length);
                out.flush();
            }
            result = out.toString();

            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
