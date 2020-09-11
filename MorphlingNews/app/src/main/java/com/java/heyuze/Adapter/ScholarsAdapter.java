package com.java.heyuze.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.heyuze.Activity.KnowledgeActivity;
import com.java.heyuze.InfoManager;
import com.java.heyuze.KnowledgeData;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.java.heyuze.ScholarData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lecho.lib.hellocharts.model.Line;

public class ScholarsAdapter extends BaseAdapter {
    private List<ScholarData> data;

    private Context context;
    private LayoutInflater layoutInflater;

    public ScholarsAdapter(Context context, List<ScholarData> data) {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    private class ScholarsViewPack {
        ImageView avatar;
        TextView name;
        TextView position;
        TextView affiliation;
        TextView H;
        TextView P;
        TextView work;
        TextView profile;
        TextView contact;
        LinearLayout hideLayout;
        LinearLayout totalLayout;
        TextView dead;
        TextView deadIcon;

        public ScholarsViewPack(View view) {
            avatar = view.findViewById(R.id.scholar_image);
            name = view.findViewById(R.id.scholar_name);
            position = view.findViewById(R.id.scholar_position);
            affiliation = view.findViewById(R.id.scholar_affiliation);
            H = view.findViewById(R.id.scholar_achievement);
            P = view.findViewById(R.id.scholar_paper);
            work = view.findViewById(R.id.scholar_work);
            profile = view.findViewById(R.id.scholar_profile);
            contact = view.findViewById(R.id.scholar_contact);
            hideLayout = view.findViewById(R.id.hide_scholar_layout);
            totalLayout = view.findViewById(R.id.scholar_total_layout);
            dead = view.findViewById(R.id.scholar_dead);
            deadIcon = view.findViewById(R.id.scholar_dead_icon);
        }
    }

    public void changeData(Vector<ScholarData> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ScholarData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_scholars, null);
            view.setTag(new ScholarsViewPack(view));
        }
        initView(getItem(i), (ScholarsViewPack) view.getTag());
        return view;
    }

    private void initView(ScholarData data, final ScholarsViewPack pack) {
        pack.hideLayout.setVisibility(View.GONE);
        String contactStr = "Email: " + data.detail.email + "\nAddress: " + data.detail.address + "\nPhone: " + data.detail.phone;
        Glide.with(context).load(data.imgUrl).into(pack.avatar);
        pack.name.setText(data.nameCn == null || data.nameCn.equals("") ? data.nameEn : data.nameCn);
        pack.position.setText(data.detail.position);
        pack.affiliation.setText(data.detail.affiliation);
        pack.H.setText(data.hIndex);
        pack.P.setText(data.pubs);
        if (data.detail.work != null) pack.work.setText(data.detail.work.replaceAll("<br>", "\n"));
        if (data.detail.biography != null) pack.profile.setText(data.detail.biography.replaceAll("<br>", "\n"));
        pack.contact.setText(contactStr);

        if (data.type == ScholarData.ScholarType.PASSAWAY) {
            String passStr = data.pDetail.passawayYear + "年" + data.pDetail.passawayMonth + "月" + data.pDetail.passawayDay + "日";
            if (data.pDetail.passawayYear.equals("null")) passStr = "暂时未知";
            pack.dead.setText(passStr);
            pack.dead.setVisibility(View.VISIBLE);
            pack.deadIcon.setVisibility(View.VISIBLE);
        } else {
            pack.dead.setVisibility(View.GONE);
            pack.deadIcon.setVisibility(View.GONE);
        }

        pack.totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pack.hideLayout.getVisibility() == View.GONE) {
                    pack.hideLayout.setVisibility(View.VISIBLE);
                } else {
                    pack.hideLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
