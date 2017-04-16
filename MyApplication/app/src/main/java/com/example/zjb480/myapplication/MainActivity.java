package com.example.zjb480.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zjb480.service.UserService;
import com.example.zjb480.service.WebService;

public class MainActivity extends Activity implements View.OnClickListener{

    // 登陆按钮
    private Button logbtn;
    // 登陆按钮
    private Button register;
    // 调试文本，注册文本
    private TextView infotv, regtv;
    // 显示用户名和密码
    EditText username, password;
    // 创建等待框
    private ProgressDialog dialog;
    // 返回的数据
    private String info;
    // 返回主线程更新数据
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取控件
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        logbtn = (Button) findViewById(R.id.login);
        regtv = (TextView) findViewById(R.id.register);
        infotv = (TextView) findViewById(R.id.info);
        // 设置按钮监听器
        logbtn.setOnClickListener(this);
        regtv.setOnClickListener(this);

        //findViews();
    }

    private void findViews() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        logbtn = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        logbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                Log.i("TAG", name + "_" + pass);
                UserService uService = new UserService(MainActivity.this);
                boolean flag = uService.login(name, pass);
                if (flag) {
                    Log.i("TAG", "登录成功");
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("TAG", "登录失败了");
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                // 检测网络，无法检测wifi
                if (!checkNetwork()) {
                    Toast toast = Toast.makeText(MainActivity.this,"网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
                // 提示框
                dialog = new ProgressDialog(this);
                dialog.setTitle("提示");
                dialog.setMessage("正在登陆，请稍后...");
                dialog.setCancelable(false);
                dialog.show();
                // 创建子线程，分别进行Get和Post传输
                new Thread(new MyThread()).start();
                break;
            case R.id.register:
                Intent regItn = new Intent(MainActivity.this, RegisterActivity.class);
                // overridePendingTransition(anim_enter);
                startActivity(regItn);
                break;
        }
        ;
    }

    // 子线程接收数据，主线程修改数据
    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet(username.getText().toString(), password.getText().toString());
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    infotv.setText(info);
                    dialog.dismiss();
                }
            });
        }
    }

    // 检测网络
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
