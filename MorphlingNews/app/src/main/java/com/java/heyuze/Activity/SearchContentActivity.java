package com.java.heyuze.Activity;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Vector;

public class SearchContentActivity extends AppCompatActivity {
    private Vector<NewsData> newsData;
    private int newsCount = 0;
    private NewsAdapter adapter;
    private RefreshLayout mRefreshLayout;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 2) { // load more
                mRefreshLayout.finishLoadMore(true);
            }
            return false;
        }
    });

    private void loadMore() {
        Vector<NewsData> data = new Vector<>();
        if (newsCount == newsData.size()) return;
        for (int i = newsCount; i < Math.min(newsCount + 6, newsData.size()); i++) {
            data.add(newsData.get(i));
        }
        newsCount += 6;
        newsCount = Math.min(newsCount, newsData.size());
        adapter.addData(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchcontent);

        Intent intent = this.getIntent();
        String strType = intent.getStringExtra("type");
        String word = intent.getStringExtra("word");

        NewsData.NewsType type = (strType.equals("NEWS") ? NewsData.NewsType.NEWS :
                                  strType.equals("PAPER") ? NewsData.NewsType.PAPER :
                                  strType.equals("POINTS") ? NewsData.NewsType.POINTS :
                                  NewsData.NewsType.EVENT);

        newsData = InfoManager.getInstance().searchNewsData(type, word);

        TextView result = findViewById(R.id.search_result);
        String resultText = "\"" + word + "\" 在分类 " + strType + " 下查找得到 " + newsData.size() + " 条结果";
        result.setText(resultText);

        ListView listView = findViewById(R.id.search_listView);
        final Vector<NewsData> data = new Vector<>();
        for (int i = 0; i < Math.min(6, newsData.size()); i++) {
            data.add(newsData.get(i));
        }
        newsCount = Math.min(6, newsData.size());
        adapter = new NewsAdapter(this, data);
        listView.setAdapter(adapter);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Message message = new Message();
                message.what = 2;
                loadMore();
                System.out.println("load more News finished. " + adapter.getCount());
                mHandler.sendMessageDelayed(message,1000);
            }
        });
    }
}
