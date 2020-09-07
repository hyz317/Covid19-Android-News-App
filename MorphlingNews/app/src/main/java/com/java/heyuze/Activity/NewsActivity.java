package com.java.heyuze.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.R;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newscontent);
        initView();
    }

    public void initView(){
        TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
        TextView tvDate = (TextView) this.findViewById(R.id.tv_date);
        TextView tvContent = (TextView) this.findViewById(R.id.tv_content);

        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String content = intent.getStringExtra("content");

        tvTitle.setText(title);
        tvDate.setText(date);
        tvContent.setText(content);
    }
}

