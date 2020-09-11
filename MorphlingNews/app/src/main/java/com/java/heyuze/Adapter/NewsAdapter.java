package com.java.heyuze.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.java.heyuze.ui.news.NewsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class NewsAdapter extends BaseAdapter {
    private List<NewsData> data = new ArrayList<NewsData>();//新闻列表集合

    private Context context;
    private LayoutInflater layoutInflater;

    public NewsAdapter(Context context, List<NewsData> data) {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    private class NewsViewPack {
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvCategory;

        public NewsViewPack(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvCategory = (TextView) view.findViewById(R.id.tv_category);
        }

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public NewsData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_news, null);
            view.setTag(new NewsViewPack(view));
        }
        initView(getItem(i), (NewsViewPack) view.getTag());
        return view;
    }

    private void initView(NewsData data, NewsViewPack pack) {
        pack.tvCategory.setText(data.type.toString());
        pack.tvDate.setText(data.time);
        pack.tvTitle.setText(data.title);
        if (NewsFragment.hasRead(data.title + data.time)) {
            pack.tvTitle.setTextColor(context.getResources().getColor(R.color.lightgrey));
        } else {
            pack.tvTitle.setTextColor(context.getResources().getColor(R.color.moredarkgrey));
        }
    }

    public void addData(Vector<NewsData> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

}
