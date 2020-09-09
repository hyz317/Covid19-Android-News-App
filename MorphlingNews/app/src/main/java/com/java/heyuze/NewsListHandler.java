package com.java.heyuze;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.java.heyuze.Activity.NewsActivity;
import com.java.heyuze.Adapter.NewsAdapter;

import java.util.Vector;

public class NewsListHandler implements Runnable {
    private ListView listView;
    private Fragment fragment;
    private NewsAdapter adapter;

    public NewsListHandler(ListView listView, Fragment fragment) {
        this.listView = listView;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!InfoManager.getInstance().hasNewsData()) {
                    try {
                        wait(200);
                        System.out.println("loading...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
        synchronized (InfoManager.getInstance()) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Vector<NewsData> data = InfoManager.getInstance().getNewsData(NewsData.NewsType.NEWS);
                    adapter = new NewsAdapter(fragment.getActivity(), data);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                long arg3) {
                            Intent intent = new Intent(fragment.getActivity(), NewsActivity.class);
                            try {
                                NewsContent ctn = InfoManager.getInstance().getNewsContent(data.get(position).id);
                                intent.putExtra("title", data.get(position).title);
                                intent.putExtra("date", data.get(position).time);
                                intent.putExtra("content", ctn.content);
                            } catch (Exception e) {
                                System.out.println("EXCEPTION GET NEWS CONTENT?");
                            }
                            fragment.getActivity().startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
