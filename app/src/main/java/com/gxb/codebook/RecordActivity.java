package com.gxb.codebook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.gxb.codebook.database.SQLiteHelper;
import com.gxb.codebook.utils.DBUtils;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    SQLiteHelper mSQLiteHelper;
    TextView noteName;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record);
        note_back = (ImageView) findViewById(R.id.note_back);
        note_time = (TextView)findViewById(R.id.tv_time);
        content = (EditText) findViewById(R.id.note_content);
        delete = (ImageView) findViewById(R.id.delete);
        note_save = (ImageView) findViewById(R.id.note_save);
        noteName = (TextView) findViewById(R.id.note_name);
        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        initData();
    }
    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加");
        Intent intent = getIntent();
        if(intent!= null){
            id = intent.getStringExtra("id");
            if (id != null){
                noteName.setText("修改");
                content.setText(intent.getStringExtra("content"));
                note_time.setText(intent.getStringExtra("time"));
                note_time.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_back) {
            finish();
        } else if (v.getId() == R.id.delete) {
            content.setText("");
        } else if (v.getId() == R.id.note_save) {
            String noteContent = content.getText().toString().trim();
            if (id != null) { // 修改操作
                if (noteContent.length() > 0) {
                    if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())) {
                        showToast("修改成功");
                        setResult(2);
                        finish();
                    } else {
                        showToast("修改失败");
                    }
                } else {
                    showToast("修改内容不能为空!");
                }
            } else {
                // 向数据库中添加数据
                if (noteContent.length() > 0) {
                    if (mSQLiteHelper.insertData(noteContent, DBUtils.getTime())) {
                        showToast("保存成功");
                        setResult(2);
                        finish();
                    } else {
                        showToast("保存失败");
                    }
                } else {
                    showToast("内容不能为空!");
                }
            }
        }
        // 可以添加更多的 else if 来处理其他视图的点击事件
    }
    public void showToast(String message){
        Toast.makeText(RecordActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
