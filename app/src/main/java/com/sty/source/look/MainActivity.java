package com.sty.source.look;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sty.source.look.utils.StreamUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etUrl;
    private Button btnLookSource;
    private TextView tvSource;
    private ImageView ivImage;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //一、找到相应控件
        initViews();
        //二、设置点击事件
        setListeners();
    }

    private void initViews() {
        etUrl = (EditText) findViewById(R.id.et_url);
        btnLookSource = (Button) findViewById(R.id.btn_looksource);
        tvSource = (TextView) findViewById(R.id.tv_source);
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    private void setListeners() {
        btnLookSource.setOnClickListener(this);
        Log.i("Tag", "onCreate 方法线程：" + Thread.currentThread().getName());
    }

    //①在主线程中创建一个Handler对象
    private Handler handler = new Handler(){
        //②重写handler的handlerMessage方法，用来接收子线程中发来的消息
        @Override
        public void handleMessage(Message msg) {
            //⑤接收子线程发送的数据，处理数据
            ////String result = (String) msg.obj;
            Bitmap bitmap = (Bitmap) msg.obj;
            //⑥当前方法属于主线程，可以作UI的更新
        //五、获取服务器返回的内容，显示到textView上
            ////tvSource.setText(result);
            ivImage.setImageBitmap(bitmap); //设置ImageView的图片内容
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_looksource:
                lookResource();
                break;
            default:
                break;
        }
    }

    private void lookResource(){
        //三、onClick方法中获取用户输入的url地址
        final String url_str = etUrl.getText().toString().trim();
        if(TextUtils.isEmpty(url_str)){
            Toast.makeText(mContext, "url不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Tag", "onClick 方法线程：" + Thread.currentThread().getName());
        //创建一个子线程做网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //四、请求URL地址
                try {
                    Log.i("Tag", "onClick 方法Runnable线程：" + Thread.currentThread().getName());
                    //1.创建一个URL对象
                    URL url = new URL(url_str);
                    //2.获取一个UrlConnection对象
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3.为UrlConnection对象设置一些必要的请求参数：请求方式、连接超时时间
                    connection.setRequestMethod("GET"); //设置请求方式
                    connection.setConnectTimeout(10 * 1000); //设置超时时间
                    //4.在获取URL请求的数据之前需要判断响应码：200成功，206访问部分数据成功，300跳转或重定向，400错误，500服务器异常
                    int code = connection.getResponseCode();
                    if(code == 200){
                        //5.获取有效数据，并将获取的流数据解析为String
                        InputStream inputStream = connection.getInputStream();
                        ////String result = StreamUtils.streamToString(inputStream);

                        //将一个读取流转换成一个图片Drawable，Bitmap位图
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        //③子线程创建一个Message对象，为了携带子线程中获取的数据给子线程
                        //Message msg = new Message();
                        Message msg = Message.obtain();  //获取一个Message对象，内部实现：如果之前的Message存在，直接返回，否则重新创建一个
                        ////msg.obj = result;  //将获取的数据封装到msg中
                        msg.obj = bitmap; //将获取的数据封装到msg中
                        //④使用handler对象将message发送到主线程
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
