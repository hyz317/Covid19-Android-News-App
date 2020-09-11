package com.java.heyuze;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.java.heyuze.Activity.NewsActivity;
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.ui.news.NewsFragment;

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
                        wait(100);
                        System.out.println("news loading...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
        synchronized (InfoManager.getInstance()) {
            System.out.println(fragment.getActivity());
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((NewsFragment) fragment).updateNews(NewsData.NewsType.NEWS);
                }
            });
        }
    }
}
