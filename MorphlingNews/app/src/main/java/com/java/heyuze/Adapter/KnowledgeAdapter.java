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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;

public class KnowledgeAdapter extends BaseAdapter {
    private static List<KnowledgeData> data;

    private Context context;
    private LayoutInflater layoutInflater;

    public KnowledgeAdapter(Context context, List<KnowledgeData> data) {
        KnowledgeAdapter.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    private class KnowledgeViewPack {
        Button nameButton;
        ImageButton downButton;
        TextView tvLabel;
        TextView tvDescription;
        LinearLayout fireLayout;
        ImageView image;
        LinearLayout relationsLayout;
        LinearLayout propertiesLayout;
        LinearLayout hideLayout;
        boolean firstLoad;

        public KnowledgeViewPack(View view) {
            nameButton = view.findViewById(R.id.knowledge_name);
            downButton = view.findViewById(R.id.down_button);
            tvLabel = view.findViewById(R.id.knowledge_label);
            tvDescription = view.findViewById(R.id.knowledge_description);
            fireLayout = view.findViewById(R.id.fire_list);
            image = view.findViewById(R.id.knowledge_image);
            relationsLayout = view.findViewById(R.id.knowledge_relations_list);
            propertiesLayout = view.findViewById(R.id.knowledge_properties_list);
            hideLayout = view.findViewById(R.id.hide_layout);
            firstLoad = true;
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public KnowledgeData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_knowledge, null);
            view.setTag(new KnowledgeViewPack(view));
        }
        initView(getItem(i), (KnowledgeViewPack) view.getTag());
        return view;
    }

    private void initView(KnowledgeData data, final KnowledgeViewPack pack) {
        String label = "实体";
        pack.nameButton.setText(data.label);
        pack.tvLabel.setText(label);
        pack.tvDescription.setVisibility(View.GONE);
        pack.hideLayout.setVisibility(View.GONE);
        pack.tvDescription.setText(data.description);
        pack.nameButton.setTag("hide_no_image");
        pack.image.setVisibility(View.GONE);
        if (data.imgUrl != null && !data.imgUrl.equals("")) {
            pack.nameButton.setTag("hide");
            Glide.with(context).load(data.imgUrl).into(pack.image);
        }

        if (pack.firstLoad) {
            int hotNum = (int) (data.hot / 0.32);
            for (int i = 0; i < hotNum; i++) {
                ImageView fire = new ImageView(context);
                fire.setImageDrawable(context.getResources().getDrawable(R.drawable.fire));
                fire.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fire.setLayoutParams(new LinearLayout.LayoutParams(dip2px(context, 26), dip2px(context, 40)));
                pack.fireLayout.addView(fire);
            }
            int cnt = 0;
            for (Map.Entry<String, String> entry : data.forwardRelations.entrySet()) {
                LinearLayout relation = new LinearLayout(context);
                relation.setOrientation(LinearLayout.HORIZONTAL);
                relation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                relation.setBackgroundResource(R.drawable.shape_corner);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(dip2px(context, 10), dip2px(context, 10), dip2px(context, 10), dip2px(context, 10));
                params.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(dip2px(context, 30), dip2px(context, 30));
                params2.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(dip2px(context, 30), dip2px(context, 30));
                params3.setMargins(0, 0, dip2px(context, 15), 0);
                params3.gravity = Gravity.CENTER_VERTICAL;

                TextView tv1 = new TextView(context);
                tv1.setText(entry.getValue());
                tv1.setLayoutParams(params);
                tv1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv1.setTextSize(15);
                // tv1.setTextColor(0x444444);

                ImageView img = new ImageView(context);
                img.setLayoutParams(params2);
                img.setImageResource(R.drawable.father);

                TextView tv2 = new TextView(context);
                tv2.setText(entry.getKey());
                tv2.setLayoutParams(params);
                tv2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv2.setTextSize(15);
                // tv2.setTextColor(0x444444);

                ImageButton searchImg = new ImageButton(context);
                searchImg.setLayoutParams(params3);
                searchImg.setImageResource(R.drawable.search);
                searchImg.setTag(entry.getKey());
                searchImg.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                searchImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, KnowledgeActivity.class);
                        intent.putExtra("word", (String) view.getTag());
                        context.startActivity(intent);
                    }
                });

                relation.addView(tv1);
                relation.addView(img);
                relation.addView(tv2);
                relation.addView(searchImg);
                if (cnt >= 2) relation.setVisibility(View.GONE);
                pack.relationsLayout.addView(relation);

                cnt += 1;
            }

            cnt = 0;

            for (Map.Entry<String, String> entry : data.backwardRelations.entrySet()) {
                LinearLayout relation = new LinearLayout(context);
                relation.setOrientation(LinearLayout.HORIZONTAL);
                relation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                relation.setBackgroundResource(R.drawable.shape_corner);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(dip2px(context, 10), dip2px(context, 10), dip2px(context, 10), dip2px(context, 10));
                params.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(dip2px(context, 30), dip2px(context, 30));
                params2.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(dip2px(context, 30), dip2px(context, 30));
                params3.setMargins(0, 0, dip2px(context, 15), 0);
                params3.gravity = Gravity.CENTER_VERTICAL;

                TextView tv1 = new TextView(context);
                tv1.setText(entry.getValue());
                tv1.setLayoutParams(params);
                tv1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv1.setTextSize(15);
                tv1.setTextColor(context.getResources().getColor(R.color.moredarkgrey));

                ImageView img = new ImageView(context);
                img.setLayoutParams(params2);
                img.setImageResource(R.drawable.son);

                TextView tv2 = new TextView(context);
                tv2.setText(entry.getKey());
                tv2.setLayoutParams(params);
                tv2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv2.setTextSize(15);
                tv1.setTextColor(context.getResources().getColor(R.color.moredarkgrey));

                ImageButton searchImg = new ImageButton(context);
                searchImg.setLayoutParams(params3);
                searchImg.setImageResource(R.drawable.search);
                searchImg.setTag(entry.getKey());
                searchImg.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                searchImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, KnowledgeActivity.class);
                        intent.putExtra("word", (String) view.getTag());
                        context.startActivity(intent);
                    }
                });

                relation.addView(tv1);
                relation.addView(img);
                relation.addView(tv2);
                relation.addView(searchImg);
                if (cnt >= 2) relation.setVisibility(View.GONE);
                pack.relationsLayout.addView(relation);

                cnt += 1;
            }

            if (data.backwardRelations.size() > 3 || data.forwardRelations.size() > 3) {
                ImageButton down = pack.downButton;
                down.setVisibility(View.VISIBLE);
                down.setImageResource(R.drawable.down);
                down.setTag("down");
                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag().equals("down")) {
                            for (int i = 5; i < pack.relationsLayout.getChildCount(); i++) {
                                pack.relationsLayout.getChildAt(i).setVisibility(View.VISIBLE);
                            }
                            view.setTag("up");
                            ((ImageButton) view).setImageResource(R.drawable.up);
                        } else {
                            for (int i = 5; i < pack.relationsLayout.getChildCount(); i++) {
                                pack.relationsLayout.getChildAt(i).setVisibility(View.GONE);
                            }
                            view.setTag("down");
                            ((ImageButton) view).setImageResource(R.drawable.down);
                        }
                    }
                });

            }

            for (Map.Entry<String, String> entry : data.properties.entrySet()) {
                LinearLayout properties = new LinearLayout(context);
                properties.setOrientation(LinearLayout.HORIZONTAL);
                properties.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                properties.setBackgroundResource(R.drawable.shape_corner);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dip2px(context, 6), dip2px(context, 6), dip2px(context, 6), dip2px(context, 6));
                params.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setMargins(dip2px(context, 8), dip2px(context, 8), dip2px(context, 8), dip2px(context, 8));
                params2.gravity = Gravity.CENTER_VERTICAL;

                TextView tv1 = new TextView(context);
                tv1.setText(entry.getKey());
                tv1.setLayoutParams(params);
                tv1.setPadding(dip2px(context, 4), dip2px(context, 4), dip2px(context, 4), dip2px(context, 4));
                tv1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv1.setTextSize(12);
                tv1.setTextColor(context.getResources().getColor(R.color.moredarkgrey));
                tv1.setBackgroundResource(R.drawable.shape_corner2);

                final TextView tv2 = new TextView(context);
                tv2.setText(entry.getValue());
                tv2.setLayoutParams(params2);
                tv2.setTextSize(12);
                tv2.setTextColor(context.getResources().getColor(R.color.moredarkgrey));
                tv2.setEllipsize(TextUtils.TruncateAt.END);
                tv2.setMaxLines(1);

                properties.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tv2.getMaxLines() == 1) {
                            tv2.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            tv2.setMaxLines(1);
                        }
                    }
                });

                properties.addView(tv1);
                properties.addView(tv2);
                pack.propertiesLayout.addView(properties);
            }

            pack.nameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag().equals("hide")) {
                        view.setTag("show");
                        pack.tvDescription.setVisibility(View.VISIBLE);
                        pack.image.setVisibility(View.VISIBLE);
                        pack.hideLayout.setVisibility(View.VISIBLE);
                    } else if (view.getTag().equals("hide_no_image")) {
                        view.setTag("show_no_image");
                        pack.tvDescription.setVisibility(View.VISIBLE);
                        pack.hideLayout.setVisibility(View.VISIBLE);
                    } else if (view.getTag().equals("show_no_image")) {
                        view.setTag("hide_no_image");
                        pack.tvDescription.setVisibility(View.GONE);
                        pack.hideLayout.setVisibility(View.GONE);
                    } else {
                        view.setTag("hide");
                        pack.tvDescription.setVisibility(View.GONE);
                        pack.image.setVisibility(View.GONE);
                        pack.hideLayout.setVisibility(View.GONE);
                    }
                }
            });
        }

        pack.firstLoad = false;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
