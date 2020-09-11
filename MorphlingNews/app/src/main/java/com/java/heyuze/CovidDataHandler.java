package com.java.heyuze;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.java.heyuze.ui.covid19.Covid19Fragment;

public class CovidDataHandler implements Runnable {
    Fragment fragment;
    View root;

    public CovidDataHandler(Fragment fragment, View root) {
        this.fragment = fragment;
        this.root = root;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!InfoManager.getInstance().hasInfectData()) {
                    try {
                        wait(400);
                        System.out.println("covid data loading...");
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
                    ((Covid19Fragment) fragment).firstLoad();
                    ((Covid19Fragment) fragment).showChinaData(root);
                }
            });
        }
    }
}
