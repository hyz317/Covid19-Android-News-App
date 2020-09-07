package com.java.heyuze.ui.news;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.heyuze.Activity.MainActivity;
import com.java.heyuze.Activity.NewsActivity;
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.NewsContent;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.google.android.material.tabs.TabLayout;
import com.java.heyuze.knowledgeData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    private List<String> lookingCategories;
    private List<String> unlookingCategories;

    private void updateLooking(final Button b, final GridLayout looking, final GridLayout unlooking) {
        System.out.println("looking: " + lookingCategories);
        System.out.println("unlooking: " + unlookingCategories);
        if (lookingCategories.contains((String) b.getText())) {
            System.out.println("!!! is in looking");
            lookingCategories.remove((String) b.getText());
            unlookingCategories.add('+' + (String) b.getText());
            looking.removeView(b);
            LayoutTransition tr = looking.getLayoutTransition();
            tr.addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {

                }

                @Override
                public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                    if (b.getParent() == null) {
                        b.setText(('+' + (String) b.getText()));
                        unlooking.addView(b);
                    }
                }
            });
        } else {
            unlookingCategories.remove((String) b.getText());
            lookingCategories.add(((String) b.getText()).substring(1));
            unlooking.removeView(b);
            LayoutTransition utr = unlooking.getLayoutTransition();
            utr.addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {

                }

                @Override
                public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                    if (b.getParent() == null) {
                        b.setText(((String) b.getText()).substring(1));
                        looking.addView(b);
                    }
                }
            });
        }
    }

    private void updateShowLooking(TabLayout tabLayout) {
        tabLayout.removeAllTabs();
        for (String category : lookingCategories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        newsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        lookingCategories = new ArrayList<String>() {{ add("类别1"); add("类别2"); add("类别3");
                                                     add("类别4"); add("类别5"); add("类别6"); }};
        unlookingCategories = new ArrayList<String>()  {{ add("+xllend3"); add("+royxroc");
                                                          add("+pchxiao"); add("+hyz317"); }};
        final TabLayout tabLayout = root.findViewById(R.id.tab);
        for (String category : lookingCategories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        final View dialogView = View.inflate(getActivity(), R.layout.dialog_category, null);
        final GridLayout looking = dialogView.findViewById(R.id.lookingCategoriesLayout);
        final GridLayout unlooking = dialogView.findViewById(R.id.unlookingCategoriesLayout);
        for (String s : lookingCategories) {
            Button btn = new Button(getActivity());
            btn.setText(s);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button b = (Button) view;
                    updateLooking(b, looking, unlooking);
                    updateShowLooking(tabLayout);
                }
            });
            looking.addView(btn);
        }
        for (String s : unlookingCategories) {
            Button btn = new Button(getActivity());
            btn.setText(s);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button b = (Button) view;
                    updateLooking(b, looking, unlooking);
                    updateShowLooking(tabLayout);
                }
            });
            unlooking.addView(btn);
        }
        final AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setMessage("新闻类别选择")
                .setNegativeButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }
                }).create();

        Button adjustCategoryBtn = root.findViewById(R.id.category);
        adjustCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog2.show();
            }
        });

        try
        {
            while (!InfoManager.getInstance().hasNewsData())
            {
                System.out.println("Please Wait!");
            }
            NewsContent test = InfoManager.getInstance().getNewsContent("5e8d92fa7ac1f2cf57f7bc75");
            System.out.println(test.content);
            System.out.println(test.date);
            System.out.println(test.labels.get(0));
            System.out.println(test.words.get(0));
            Vector<knowledgeData> test2 = InfoManager.getInstance().getKnowledge("病毒");
            System.out.println(test2.get(0).description);
            System.out.println(test2.get(0).url);
            System.out.println(test2.get(0).hot);
            System.out.println(test2.get(0).imgUrl);
            System.out.println(test2.get(0).label);
            System.out.println(test2.get(0).properties.get("鉴别诊断"));
            System.out.println(test2.get(0).forwardRelations.get("无症状感染者"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        final Vector<NewsData> data = InfoManager.getInstance().getNewsData();
        NewsAdapter adapter = new NewsAdapter(getActivity(), data);
        ListView listView = root.findViewById(R.id.listView);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);

                try {
                    NewsContent ctn = InfoManager.getInstance().getNewsContent(data.get(position).id);
                    intent.putExtra("title", data.get(position).title);
                    intent.putExtra("date", data.get(position).time);
                    intent.putExtra("content", ctn.content);
                } catch (Exception e) {
                    System.out.println("EXCEPTION GET NEWS CONTENT?");
                }

                getActivity().startActivity(intent);

            }
        });

        return root;
    }
}