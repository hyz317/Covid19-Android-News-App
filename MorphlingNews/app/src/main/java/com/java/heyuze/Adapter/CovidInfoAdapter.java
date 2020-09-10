package com.java.heyuze.Adapter;

import android.content.Context;
import android.content.SyncRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.java.heyuze.SimpleInfectData;

import java.util.ArrayList;
import java.util.List;

public class CovidInfoAdapter extends BaseAdapter {
    private List<SimpleInfectData> data = new ArrayList<SimpleInfectData>();

    private Context context;
    private LayoutInflater layoutInflater;

    public CovidInfoAdapter(Context context, List<SimpleInfectData> data) {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    private class CovidInfoViewPack {
        private TextView tvCountry;
        private TextView tvIncrease;
        private TextView tvConfirmed;
        private TextView tvCured;
        private TextView tvDead;
        private ImageButton imgButton;

        public CovidInfoViewPack(View view) {
            tvCountry = view.findViewById(R.id.tv_covid_country);
            tvIncrease = view.findViewById(R.id.tv_covid_increase);
            tvConfirmed = view.findViewById(R.id.tv_covid_confirmed);
            tvCured = view.findViewById(R.id.tv_covid_cured);
            tvDead = view.findViewById(R.id.tv_covid_dead);
            imgButton = view.findViewById(R.id.tv_covid_more);
        }

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_covid_info, null);
            view.setTag(new CovidInfoAdapter.CovidInfoViewPack(view));
        }
        initView((SimpleInfectData) getItem(i), (CovidInfoAdapter.CovidInfoViewPack) view.getTag());
        return view;
    }

    private void initView(SimpleInfectData data, CovidInfoAdapter.CovidInfoViewPack pack) {
        pack.tvCountry.setText(data.name);
        pack.tvIncrease.setText(String.valueOf(data.increase));
        pack.tvConfirmed.setText(String.valueOf(data.accumulation));
        pack.tvCured.setText(String.valueOf(data.cure));
        pack.tvDead.setText(String.valueOf(data.dead));
        pack.imgButton.setTag(data.name);
        pack.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("more on " + (String) view.getTag());
            }
        });
    }
}
