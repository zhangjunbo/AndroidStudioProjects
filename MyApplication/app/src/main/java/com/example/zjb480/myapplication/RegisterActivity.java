package com.example.zjb480.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zjb480.domain.User;
import com.example.zjb480.service.UserService;
import com.example.zjb480.service.WebService;
import com.example.zjb480.service.WebServicePost;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username;
    EditText password;
    //EditText age;
    //RadioGroup sex;
    Button register;
    // 创建等待框
    private ProgressDialog dialog;
    // 返回的数据
    private String info;
    // 返回主线程更新数据
    private static Handler handler = new Handler();
    // 调试文本，注册文本
    private TextView infotv, regtv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        username = (EditText) findViewById(R.id.usernameRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        register = (Button) findViewById(R.id.Register);
        infotv = (TextView) findViewById(R.id.info);
        // 设置按钮监听器
        register.setOnClickListener(this);
        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //findViews();
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                //String agestr = age.getText().toString().trim();
                //if(agestr.length()>3 || !isNumeric(agestr)){
                //    Toast.makeText(RegisterActivity.this, "徐总/撸森/张总不要瞎搞，输入真实的年龄！", Toast.LENGTH_LONG).show();
                //}
                //String sexstr = ((RadioButton) RegisterActivity.this.findViewById(sex.getCheckedRadioButtonId())).getText().toString();
                //Log.i("TAG", name + "_" + pass + "_" + agestr + "_" + sexstr);
                UserService uService = new UserService(RegisterActivity.this);
                User user = new User();
                user.setUsername(name);
                user.setPassword(pass);
                //user.setAge(Integer.parseInt(agestr));
                //user.setSex(sexstr);
                uService.register(user);
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
            }
        });
        */
    }

    private void findViews() {
        username = (EditText) findViewById(R.id.usernameRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        //age = (EditText) findViewById(R.id.ageRegister);
        //sex = (RadioGroup) findViewById(R.id.sexRegister);
        register = (Button) findViewById(R.id.Register);
    }
    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                Intent regItn = new Intent(RegisterActivity.this, RegisterActivity.class);
                // overridePendingTransition(anim_enter);
                startActivity(regItn);
                break;
            case R.id.Register:
            // 检测网络，无法检测wifi
            if (!checkNetwork2()) {
                Toast toast = Toast.makeText(RegisterActivity.this,"网络未连接", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            }
            // 提示框
            dialog = new ProgressDialog(this);
            dialog.setTitle("提示");
            dialog.setMessage("正在注册，请稍后...");
            dialog.setCancelable(false);
            dialog.show();
            // 创建子线程，分别进行Get和Post传输
            new Thread(new RegisterActivity.MyThread2()).start();
            break;
        }
        ;
    }


    // 子线程接收数据，主线程修改数据
    public class MyThread2 implements Runnable {
        @Override
        public void run() {
            //info = WebService.executeHttpGet(username.getText().toString(), password.getText().toString());
             info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
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
    private boolean checkNetwork2() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
