package com.java.heyuze.ui.news;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.heyuze.Activity.MainActivity;
import com.java.heyuze.R;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
        final TextView textView = root.findViewById(R.id.text_home);
        newsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


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

        return root;
    }
}