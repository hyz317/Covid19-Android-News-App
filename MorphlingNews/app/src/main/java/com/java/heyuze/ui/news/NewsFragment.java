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
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.NewsContent;
import com.java.heyuze.NewsData;
import com.java.heyuze.NewsListHandler;
import com.java.heyuze.R;
import com.google.android.material.tabs.TabLayout;
import com.java.heyuze.knowledgeData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    private List<String> lookingCategories;
    private List<String> unlookingCategories;
    private ListView listView;
    private NewsData.NewsType nowNewsType = NewsData.NewsType.NEWS;
    private RefreshLayout mRefreshLayout;
    private Vector<NewsData> newsData;
    private NewsAdapter adapter;
    private int newsCount = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // refresh
                    mRefreshLayout.finishRefresh(true);
                    break;
                case 2: // load more
                    List<String> mLoadMoreDatas = (List<String>) msg.obj;
                    mRefreshLayout.finishLoadMore(true);
                    break;
            }
            return false;
        }
    });

    public void updateNews(NewsData.NewsType type) {
        newsData = InfoManager.getInstance().getNewsData(type);
        final Vector<NewsData> data = new Vector<>();
        for (int i = 0; i < 6; i++) {
            data.add(newsData.get(i));
        }
        newsCount = 6;
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
                } catch (Exception e) {
                    System.out.println("EXCEPTION GET NEWS CONTENT?");
                }
                getActivity().startActivity(intent);
            }
        });
    }

    private void loadMoreNews() {
        Vector<NewsData> data = new Vector<>();
        for (int i = newsCount; i < newsCount + 6; i++) {
            data.add(newsData.get(i));
        }
        newsCount += 6;
        adapter.addData(data);
    }

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

        lookingCategories = new ArrayList<String>() {{ add("News"); add("Papers");
                                                       add("Event"); }};
        unlookingCategories = new ArrayList<String>()  {{ /*add("+xllend3"); add("+royxroc");
                                                          add("+pchxiao"); add("+hyz317");*/ }};
        final TabLayout tabLayout = root.findViewById(R.id.tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!InfoManager.getInstance().hasNewsData()) return;
                String category = (String) tab.getTag();
                switch (category) {
                    case "News":
                        nowNewsType = NewsData.NewsType.NEWS;
                        updateNews(NewsData.NewsType.NEWS);
                        break;
                    case "Papers":
                        nowNewsType = NewsData.NewsType.PAPER;
                        updateNews(NewsData.NewsType.PAPER);
                        break;
                    case "Event":
                        nowNewsType = NewsData.NewsType.EVENT;
                        updateNews(NewsData.NewsType.EVENT);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        updateShowLooking(tabLayout);

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

        listView = root.findViewById(R.id.listView);
        NewsListHandler newsListHandler = new NewsListHandler(listView, this);
        Thread newsListThread = new Thread(newsListHandler);
        newsListThread.start();

        mRefreshLayout = root.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Message message = new Message();
                message.what = 1;
                try {
                    InfoManager.getInstance().updateCovidData(InfoManager.InfoType.NEWSDATA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateNews(nowNewsType);
                mHandler.sendMessage(message);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Message message = new Message();
                message.what = 2;
                loadMoreNews();
                System.out.println("load more News finished. " + adapter.getCount());
                mHandler.sendMessageDelayed(message,1000);
            }
        });

        return root;
    }
}