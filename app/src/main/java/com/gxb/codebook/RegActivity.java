package com.gxb.codebook;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_reg);

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
                    Toast.makeText(RegActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(password2)) {
                    Toast.makeText(RegActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                } else if (!username.matches("^[A-Za-z0-9@#$%^&*.]+$") || !password.matches("^[A-Za-z0-9@#$%^&*.]+$")) {
                    Toast.makeText(RegActivity.this, "用户名和密码仅支持A-Z,a-z,0-9,@、#、$、%、^、&、*、.", Toast.LENGTH_SHORT).show();
                } else {
                    String wserpasswd = password + "+" + username;
                    String encryptedPassword = UserPasswdConfound.encrypt(wserpasswd);
                    // 以txt文件形式保存注册信息
                    saveUserInfoToFile(encryptedPassword);
                    mibao("你的家乡是："+mibao1);
                    mibao("你的宠物是："+mibao2);
                    mibao("你的学校是："+mibao3);

                    // 复制mibao.txt文件到Downloads文件夹下
                    File file = new File(getFilesDir(), "mibao.txt");
                    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "密保.txt");
                    try {
                        if (!file2.exists()) {
                            file2.createNewFile();
                        }
                        try (FileOutputStream fos = new FileOutputStream(file2, true);
                             FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegActivity.this);
                    builder.setTitle("开发者提醒：");
                    builder.setMessage("请牢记您的密保问题，以便找回密码，如记不住，密保问题密码已复制到Downloads文件夹下的“密保.txt”文件中并不会上传到云端，账号密码加密保存到本地，此软件完全不联网！");
                    // 确定按钮，点击确定后关闭对话框，并跳转到登录界面
                    builder.setPositiveButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
                    builder.show();
                }
            }
        });
    }

    private void saveUserInfoToFile(String encryptedPassword) {
        String userInfo = encryptedPassword + "\n";
        File file = new File(getFilesDir(), "user_info.txt");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write(userInfo.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mibao(String encryptedPassword) {
        String userInfo = encryptedPassword + "\n";
        File file = new File(getFilesDir(), "mibao.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write(userInfo.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}