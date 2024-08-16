package com.gxb.codebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.graphics.BitmapFactory;
import androidx.activity.EdgeToEdge;

public class LoginActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private Button login_btn;
    private CheckBox rememberPass;
    private ImageView reg_image;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        rememberPass = findViewById(R.id.save_password);
        reg_image = findViewById(R.id.reg_image);
        loadSavedImage();
        loadCredentials();

        // 点击图片时，跳出选择框，选择图片然后显示在ImageView上
        reg_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开相册选择图片
                openImagePicker();
            }
        });
        reg_image.setClipToOutline(true);

        // 点击登录按钮时，打开user_info.txt文件，读取文件中的密文
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                } else if (!username.matches("^[A-Za-z0-9@#$%^&*.]+$") || !password.matches("^[A-Za-z0-9@#$%^&*.]+$")) {
                    Toast.makeText(LoginActivity.this, "用户名和密码仅支持A-Z,a-z,0-9,@、#、$、%、^、&、*、.", Toast.LENGTH_SHORT).show();
                    // 不存在user_info.txt时，点击登录按钮，弹出你还没有注册的提示
                } else if (!new File(getFilesDir(), "user_info.txt").exists()) {
                    Toast.makeText(LoginActivity.this, "您还没有注册！", Toast.LENGTH_SHORT).show();
                } else {
                    String wserpasswd = password + "+" + username;
                    String encryptedPassword = UserPasswdConfound.encrypt(wserpasswd);
                    // 获取rememberPass的状态，如果为true则保存密码
                    if (rememberPass.isChecked()) {
                        saveCredentials();
                    } else {
                        // 如果用户取消勾选，可以选择删除保存的用户名和密码
                        SharedPreferences sharedPreferences = getSharedPreferences("SavedCredentials", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("username");
                        editor.remove("password");
                        editor.remove("remember");
                        editor.apply();
                    }
                    new VerifyTask(LoginActivity.this).execute(encryptedPassword);
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 处理图片选择结果的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String filePath = saveImageToInternalStorage(selectedImageUri);
            if (filePath != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    reg_image.setImageBitmap(bitmap);
                    // 保存图片路径到 SharedPreferences，以便在重启后能够找到图片
                    SharedPreferences sharedPreferences = getSharedPreferences("SavedImage", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("image_path", filePath);
                    editor.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        // 这里是将图片保存到应用的内部存储的示例代码
        // 实际上，您可能需要保存到外部存储，并处理运行时权限
        String path = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            File file = new File(getFilesDir(), "selected_image.png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    private void saveCredentials() {
        // 获取用户名和密码
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        // 将用户名和密码保存到SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SavedCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("remember", rememberPass.isChecked());
        editor.apply();
    }

    private void loadSavedImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("SavedImage", Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("image_path", null);
        if (imagePath != null && new File(imagePath).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            reg_image.setImageBitmap(bitmap);
        } else {
            // 如果没有找到图片，可能需要设置默认图片或隐藏 ImageView
        }
    }

    private void loadCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("SavedCredentials", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean remember = sharedPreferences.getBoolean("remember", false);

        if (remember) {
            Username.setText(savedUsername);
            Password.setText(savedPassword);
            rememberPass.setChecked(true);
        }
    }

    public void doClick(View view) {
        // 当存在user_info.txt时，点击注册按钮，弹出你已经注册过了的提示
        File file = new File(getFilesDir(), "user_info.txt");
        if (file.exists()) {
            Toast.makeText(this, "您已经注册过了！", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, RegActivity.class));
        }
    }

    public void toForget(View view) {
        startActivity(new Intent(this, ForgetActivity.class));
    }

    private static class VerifyTask extends AsyncTask<String, Void, Boolean> {
        private WeakReference<LoginActivity> weakActivity;

        VerifyTask(LoginActivity activity) {
            this.weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String encryptedPassword = params[0];
            LoginActivity activity = weakActivity.get();
            if (activity == null) {
                return false;
            }
            return activity.verifyUser(encryptedPassword);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            LoginActivity activity = weakActivity.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (result) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.putExtra("username", activity.Username.getText().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast.makeText(activity, "登录失败，账户或密码错误！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean verifyUser(String encryptedPassword) {
        File file = new File(getFilesDir(), "user_info.txt");
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(encryptedPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}