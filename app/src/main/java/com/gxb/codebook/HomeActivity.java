package com.gxb.codebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.gxb.codebook.adapter.NotepadAdapter;
import com.gxb.codebook.bean.NotepadBean;
import com.gxb.codebook.database.SQLiteHelper;
import java.io.File;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textbeta;
    private ImageView imageView;
    private ImageView nameImageView;
    private ImageView further_image;
    private TextView nameTextView;
    private Button out_btn;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private TextView mTextView;
    private ListView listView;
    private List<NotepadBean> list;
    private SQLiteHelper mSQLiteHelper;
    private NotepadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imageView = findViewById(R.id.image_view);
        textbeta = findViewById(R.id.textbeta);
        out_btn = findViewById(R.id.out_btn);
        further_image = findViewById(R.id.further_image);
        listView = findViewById(R.id.listview);
        button_1 = findViewById(R.id.button_1);
        button_2 = findViewById(R.id.button_2);
        button_3 = findViewById(R.id.button_3);

        setupDrawerHeader(navigationView);
        setupDrawerContent(navigationView);
        loadSavedImage();
        initData();
        setTextViewStyles(textbeta);
        setTextViewStyles(nameTextView);
        String username = getIntent().getStringExtra("username");
        textbeta.setText(username);
        nameTextView.setText(username);
        imageView.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        further_image.setOnClickListener(v -> showOptionMenu());
        out_btn.setOnClickListener(v -> out_btn()); // 直接调用 out_btn 方法
        button_1.setOnClickListener(v -> button_1());
        button_2.setOnClickListener(v -> button_2());
        button_3.setOnClickListener(v -> button_3());
        applyBlurToDrawer(); // 移除参数，因为我们将在方法内部获取 navigationView
    }

    private void button_1() {
        Uri uri = Uri.parse("https://github.com/GXB-GeXiaoBai");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    private void button_2() {
        Uri uri = Uri.parse("https://gitee.com/GXB-GeXiaoBai");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    private void button_3() {
        Toast.makeText(this, "暂未开发", Toast.LENGTH_SHORT).show();
    }


    private void showOptionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, further_image);
        popupMenu.getMenuInflater().inflate(R.menu.menu_optionmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> handleMenuItemSelection(item));
        popupMenu.show();
    }

    private void setTextViewStyles(TextView textView) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, textView.getPaint().getTextSize()* textView.getText().length(), 0, Color.parseColor("#FFFF68FF"), Color.parseColor("#FFFED732"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }

    private boolean handleMenuItemSelection(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu1) {
            Intent intent = new Intent(this,
                    RecordActivity.class);
            startActivityForResult(intent, 1);
            initData();
            return true;
            //多余的可以在这里添加
        } else {
            return false;
        }
    }

    protected void initData() {
        mSQLiteHelper= new SQLiteHelper(this); //创建数据库
        showQueryData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                NotepadBean notepadBean = list.get(position);
                Intent intent = new Intent(HomeActivity.this, RecordActivity.class);
                intent.putExtra("id", notepadBean.getId());
                intent.putExtra("time", notepadBean.getNotepadTime()); //记录的时间
                intent.putExtra("content", notepadBean.getNotepadContent()); //记录的内容
                HomeActivity.this.startActivityForResult(intent, 1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                    position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder( HomeActivity.this)
                        .setMessage("是否删除此事件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean = list.get(position);
                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(HomeActivity.this,"删除成功",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog =  builder.create();
                dialog.show();
                return true;
            }
        });

    }

    private void showQueryData(){
        if (list!=null){
            list.clear();
        }
        // 从数据库中查询数据(保存的标签)
        list = mSQLiteHelper.query();
        adapter = new NotepadAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setDivider(null);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==2){
            showQueryData();
        }
    }

    private void applyBlurToDrawer() {
        // 获取 DrawerLayout 的主内容视图，这里假设它是 DrawerLayout 的子视图
        View contentToBlur = findViewById(R.id.nav_view); // 替换为你的主内容视图的ID
        if (contentToBlur != null && contentToBlur.getVisibility() == View.VISIBLE) {
            // 确保视图的宽度和高度大于 0
            if (contentToBlur.getWidth() > 0 && contentToBlur.getHeight() > 0) {
                // 创建一个与 contentToBlur 大小相同的 Bitmap
                Bitmap bitmap = Bitmap.createBitmap(contentToBlur.getWidth(), contentToBlur.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                contentToBlur.draw(canvas);

                // 创建一个 ImageView 用于承载模糊效果
                ImageView blurImageView = new ImageView(this);
                blurImageView.setImageBitmap(bitmap);

                try {
                    // 使用 Blurry 库设置模糊效果
                    Blurry.with(this)
                            .radius(25) // 设置模糊半径
                            .sampling(2) // 设置采样率
                            .from(bitmap) // 使用创建的 Bitmap
                            .into(blurImageView); // 将模糊效果应用到 blurImageView

                    // 将模糊效果的 ImageView 设置为 NavigationView 的背景
                    navigationView.setBackgroundResource(0); // 清除 NavigationView 的现有背景
                    navigationView.addView(blurImageView); // 将模糊 ImageView 添加到 NavigationView
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "模糊效果应用失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void out_btn() {
        // 退出登录, 返回到登录界面, 清除保存的用户名和密码
        SharedPreferences sharedPreferences = getSharedPreferences("SavedCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupDrawerHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        nameImageView = headerView.findViewById(R.id.name_image);
        nameTextView = headerView.findViewById(R.id.name_text);
    }

    private void loadSavedImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("SavedImage", Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("image_path", null);
        if (imagePath != null && new File(imagePath).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            imageView.setClipToOutline(true);

            if (nameImageView != null) {
                nameImageView.setImageBitmap(bitmap);
                nameImageView.setClipToOutline(true);
            }
        } else {
            // 如果没有找到图片，可能需要设置默认图片或隐藏 ImageView
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // 处理菜单项点击事件
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
        );
    }
}