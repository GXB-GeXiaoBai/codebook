package com.gxb.codebook;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ForgetActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private EditText Password2;
    private EditText MiBao1;
    private EditText MiBao2;
    private EditText MiBao3;
    private Button reg_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget);

        Username = findViewById(R.id.username);
        Password = findViewById(R.id.one_password);
        Password2 = findViewById(R.id.two_password);
        reg_btn = findViewById(R.id.reg_btn);
        MiBao1 = findViewById(R.id.mibao1);
        MiBao2 = findViewById(R.id.mibao2);
        MiBao3 = findViewById(R.id.mibao3);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                String password2 = Password2.getText().toString();
                String mibao1 = MiBao1.getText().toString();
                String mibao2 = MiBao2.getText().toString();
                String mibao3 = MiBao3.getText().toString();
                if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || mibao1.isEmpty() || mibao2.isEmpty() || mibao3.isEmpty()) {
                    Toast.makeText(ForgetActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(password2)) {
                    Toast.makeText(ForgetActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                } else if (!username.matches("^[A-Za-z0-9@#$%^&*.]+$") || !password.matches("^[A-Za-z0-9@#$%^&*.]+$")) {
                    Toast.makeText(ForgetActivity.this, "用户名和密码仅支持A-Z,a-z,0-9,@、#、$、%、^、&、*、.", Toast.LENGTH_SHORT).show();
                } else {
                    String wserpasswd = password + "+" + username;
                    if (mibao("你的家乡是："+mibao1) == 0 || mibao("你的宠物是："+mibao2) == 0 || mibao("你的学校是："+mibao3) == 0) {
                        Toast.makeText(ForgetActivity.this, "密保问题错误", Toast.LENGTH_SHORT).show();
                    }else {
                        String encryptedPassword = UserPasswdConfound.encrypt(wserpasswd);
                        saveUserInfoToFile(encryptedPassword);
                        Toast.makeText(ForgetActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    private void saveUserInfoToFile(String encryptedPassword) {
        String userInfo = encryptedPassword + "\n";
        File file = new File(getFilesDir(), "user_info.txt");
        // 清除原本的用户信息，然后再写入
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(file, false)) {
                fos.write(userInfo.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int mibao(String mibao) {
        // 读取密保问题文件,查找String mibao，如果存在则返回1，否则返回0
        File file = new File(getFilesDir(), "mibao.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileInputStream fis = new FileInputStream(file)) {
                InputStreamReader reader = new InputStreamReader(fis);
                char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    String content = new String(buffer, 0, len);
                    if (content.contains(mibao)) {
                        return 1;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}