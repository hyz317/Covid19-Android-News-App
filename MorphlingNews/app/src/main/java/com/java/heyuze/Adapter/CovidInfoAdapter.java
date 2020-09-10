package com.java.heyuze.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.java.heyuze.Activity.CovidInfoActivity;
import com.java.heyuze.InfectData;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.java.heyuze.SimpleInfectData;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class CovidInfoAdapter extends BaseAdapter {
    private List<InfectData> data = new ArrayList<InfectData>();
    private Context context;
    private LayoutInflater layoutInflater;

    public CovidInfoAdapter(Context context, List<InfectData> data) {
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
        initView((InfectData) getItem(i), (CovidInfoAdapter.CovidInfoViewPack) view.getTag());
        return view;
    }

    private void initView(final InfectData rawData, CovidInfoAdapter.CovidInfoViewPack pack) {
        final SimpleInfectData data = new SimpleInfectData(rawData);
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
                Intent intent = new Intent(context, CovidInfoActivity.class);
                intent.putExtra("Name", data.name);
                intent.putExtra("Confirmed", Vector2String(rawData.confirmed));
                intent.putExtra("Cured", Vector2String(rawData.cured));
                intent.putExtra("Dead", Vector2String(rawData.dead));
                intent.putExtra("BeginDate", rawData.beginDate);
                context.startActivity(intent);
            }
        });
    }

    private String Vector2String(Vector<Integer> v) {
        StringBuilder str = new StringBuilder();
        for (Integer num : v) {
            str.append(String.valueOf(num));
            str.append(",");
        }
        str.delete(str.length() - 1, str.length());
        System.out.println(str.toString());
        return str.toString();
    }
}
