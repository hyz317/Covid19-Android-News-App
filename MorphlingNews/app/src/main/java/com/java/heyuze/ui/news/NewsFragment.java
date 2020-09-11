package com.java.heyuze.ui.news;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import com.java.heyuze.Activity.SearchActivity;
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.NewsContent;
import com.java.heyuze.NewsData;
import com.java.heyuze.NewsListHandler;
import com.java.heyuze.R;
import com.google.android.material.tabs.TabLayout;
import com.java.heyuze.KnowledgeData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    private List<String> lookingCategories;
    private List<String> unlookingCategories;
    private ListView listView;
    private TextView clusterResult;
    private NewsData.NewsType nowNewsType = NewsData.NewsType.NEWS;
    private int nowEventsType = -1;
    private RefreshLayout mRefreshLayout;
    private Vector<NewsData> newsData;
    private Vector<NewsData> eventNewsData;
    private NewsAdapter adapter;
    private int newsCount = 0;
    private static HashSet<String> readTitles = new HashSet<>();
    private boolean firstLoad = true;

    public static boolean hasRead(String str) {
        System.out.println("SETSIZE???????? " + readTitles.size());
        return readTitles.contains(str);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // refresh
                    mRefreshLayout.finishRefresh(true);
                    break;
                case 2: // load more
                    mRefreshLayout.finishLoadMore(true);
                    break;
            }
            return false;
        }
    });

    public void updateNews(NewsData.NewsType type) {
        newsData = InfoManager.getInstance().getNewsData(type);
        final Vector<NewsData> data = new Vector<>();
        for (int i = 0; i < Math.min(6, newsData.size()); i++) {
            data.add(newsData.get(i));
        }
        newsCount = Math.min(6, newsData.size());
        adapter = new NewsAdapter(getActivity(), data);
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
                    intent.putExtra("source", ctn.source);
                    readTitles.add(data.get(position).title + data.get(position).time);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    System.out.println("EXCEPTION GET NEWS CONTENT?");
                }
                getActivity().startActivity(intent);
            }
        });
    }

    private void loadMoreNews() {
        Vector<NewsData> data = new Vector<>();
        if (newsCount == newsData.size()) return;
        for (int i = newsCount; i < Math.min(newsCount + 6, newsData.size()); i++) {
            data.add(newsData.get(i));
        }
        newsCount += 6;
        newsCount = Math.min(newsCount, newsData.size());
        adapter.addData(data);
    }

    private void updateEventNews(int num) {
        newsData = InfoManager.getInstance().getNewsData(NewsData.NewsType.EVENT);
        eventNewsData = new Vector<>();
        for (NewsData data : newsData) {
            if (data.eventLabel == num)
                eventNewsData.add(data);
        }
        String result = "聚类第 " + (num + 1) + " 组下共有 " + eventNewsData.size() + " 组新闻\n" + "关键词为" + Arrays.toString(InfoManager.getInstance().getEventKeyword(num + 1));
        clusterResult.setText(result);
        Vector<NewsData> data = new Vector<>();
        for (int i = 0; i < Math.min(6, eventNewsData.size()); i++) {
            data.add(eventNewsData.get(i));
        }
        newsCount = Math.min(6, eventNewsData.size());
        adapter = new NewsAdapter(getActivity(), data);
        listView.setAdapter(adapter);
    }

    private void loadMoreEventNews(int num) {
        Vector<NewsData> data = new Vector<>();
        if (newsCount == eventNewsData.size()) return;
        for (int i = newsCount; i < Math.min(newsCount + 6, eventNewsData.size()); i++) {
            data.add(eventNewsData.get(i));
        }
        newsCount += 6;
        newsCount = Math.min(newsCount, eventNewsData.size());
        adapter.addData(data);
    }

    private void updateLooking(final Button b, final GridLayout looking, final GridLayout unlooking) {
        // System.out.println("looking: " + lookingCategories);
        // System.out.println("unlooking: " + unlookingCategories);
        if (lookingCategories.contains((String) b.getText())) {
            if (lookingCategories.size() == 1) return;
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
            TabLayout.Tab tab = tabLayout.newTab().setText(category);
            tab.setTag(category);
            tabLayout.addTab(tab);
        }
    }

    public void setAdapter(NewsAdapter adapter) {
        this.adapter = adapter;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_news, container, false);
        listView = root.findViewById(R.id.listView);

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        newsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        lookingCategories = new ArrayList<String>() {{ add("News"); add("Papers");
                                                       add("Event"); add("Points"); }};
        unlookingCategories = new ArrayList<String>()  {{ /*add("+xllend3"); add("+royxroc");
                                                          add("+pchxiao"); add("+hyz317");*/ }};
        final TabLayout tabLayout = root.findViewById(R.id.tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!InfoManager.getInstance().hasNewsData()) return;
                String category = (String) tab.getTag();
                root.findViewById(R.id.tab_event).setVisibility(View.GONE);
                clusterResult.setVisibility(View.GONE);
                switch (category) {
                    case "News":
                        nowEventsType = -1;
                        nowNewsType = NewsData.NewsType.NEWS;
                        updateNews(NewsData.NewsType.NEWS);
                        break;
                    case "Papers":
                        nowEventsType = -1;
                        nowNewsType = NewsData.NewsType.PAPER;
                        updateNews(NewsData.NewsType.PAPER);
                        break;
                    case "Event":
                        nowNewsType = NewsData.NewsType.EVENT;
                        root.findViewById(R.id.tab_event).setVisibility(View.VISIBLE);
                        updateNews(NewsData.NewsType.EVENT);
                        break;
                    case "Points":
                        nowEventsType = -1;
                        nowNewsType = NewsData.NewsType.POINTS;
                        updateNews(NewsData.NewsType.POINTS);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        updateShowLooking(tabLayout);

        final TabLayout eventTabLayout = root.findViewById(R.id.tab_event);
        clusterResult = root.findViewById(R.id.event_keywords);
        eventTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!InfoManager.getInstance().hasNewsData()) return;
                Integer category = (Integer) tab.getTag();
                if (category != -1) {
                    clusterResult.setVisibility(View.VISIBLE);
                    updateEventNews(category);
                }
                else {
                    clusterResult.setVisibility(View.GONE);
                    updateNews(NewsData.NewsType.EVENT);
                }
                nowEventsType = category;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        TabLayout.Tab tab = eventTabLayout.newTab().setText("全部");
        tab.setTag(-1);
        eventTabLayout.addTab(tab);
        for (int i = 1; i <= 6; i++) {
            TabLayout.Tab tab1 = eventTabLayout.newTab().setText("聚类" + i);
            tab1.setTag(i - 1);
            eventTabLayout.addTab(tab1);
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

        NewsListHandler newsListHandler = new NewsListHandler(listView, this);
        Thread newsListThread = new Thread(newsListHandler);
        newsListThread.start();

        mRefreshLayout = root.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!InfoManager.getInstance().hasNewsData()) return;
                Message message = new Message();
                message.what = 1;
                try {
                    InfoManager.getInstance().updateCovidData(InfoManager.InfoType.NEWSDATA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nowEventsType == -1) updateNews(nowNewsType);
                else updateEventNews(nowEventsType);
                mHandler.sendMessage(message);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!InfoManager.getInstance().hasNewsData()) return;
                Message message = new Message();
                message.what = 2;
                if (nowEventsType == -1) loadMoreNews();
                else loadMoreEventNews(nowEventsType);
                System.out.println("load more News finished. " + adapter.getCount());
                mHandler.sendMessageDelayed(message,1000);
            }
        });

        Button searchBar = root.findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("type", nowNewsType.toString());
                getActivity().startActivity(intent);
            }
        });

        firstLoad = false;

        return root;
    }
}