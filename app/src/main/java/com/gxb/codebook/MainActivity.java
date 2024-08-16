package com.gxb.codebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import androidx.activity.EdgeToEdge;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 0000; // 延迟时间，单位毫秒
    private TextView textViewTimer; // 用来引用TextView
    private CountDownTimer countDownTimer; // 倒计时计时器
    private boolean isBusyboxDownloaded = false;
    private boolean isScriptDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        textViewTimer = findViewById(R.id.textView); // 获取TextView

        // 初始化内部文件目录
        File appFilesDir = getFilesDir();
        File appExternalFilesDir = getExternalFilesDir(null);
        if (!appFilesDir.exists()) {
            appFilesDir.mkdirs();
        }
        if (appExternalFilesDir != null && !appExternalFilesDir.exists()) {
            appExternalFilesDir.mkdirs();
        }
        startCountdown();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(SPLASH_DISPLAY_LENGTH, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText("正在初始化，请等待...");
            }

            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在Activity销毁时取消倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}