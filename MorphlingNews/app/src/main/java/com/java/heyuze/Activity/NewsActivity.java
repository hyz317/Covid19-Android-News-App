package com.java.heyuze.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
        TextView tvTitle = this.findViewById(R.id.tv_title);
        TextView tvDate = this.findViewById(R.id.tv_date);
        TextView tvContent = this.findViewById(R.id.tv_content);
        ImageButton wxButton = this.findViewById(R.id.wx_button);

        Intent intent = this.getIntent();
        final String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        final String content = intent.getStringExtra("content");

        tvTitle.setText(title);
        tvDate.setText(date);
        tvContent.setText(content);

        wxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsActivity.this, WXEntryActivity.class);
                intent.putExtra("info", title + "\n" + content);
                NewsActivity.this.startActivity(intent);
            }
        });
    }
}

