package com.java.heyuze.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.Adapter.KnowledgeAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.KnowledgeData;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;

import java.util.Vector;

public class KnowledgeActivity extends AppCompatActivity {
    private KnowledgeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledgeinfo);

        Intent intent = this.getIntent();
        String word = intent.getStringExtra("word");

        try {
            final Vector<KnowledgeData> data = InfoManager.getInstance().getKnowledge(word);
            TextView result = findViewById(R.id.search_result_knowledge);
            String resultText = "\"" + word + "\" 查找得到 " + data.size() + " 条结果";
            result.setText(resultText);

            ListView listView = findViewById(R.id.knowledge_listView);
            adapter = new KnowledgeAdapter(this, data);
            listView.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
