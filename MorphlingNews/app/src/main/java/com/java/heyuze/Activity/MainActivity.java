package com.java.heyuze.Activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.java.heyuze.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.java.heyuze.*;
import com.tencent.mm.opensdk.openapi.*;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        InfoManager.getInstance();
        final String APP_ID = "wxxxxxxx"; //替换为申请到的app id
        IWXAPI wx_api; //全局的微信api对象
        wx_api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_news, R.id.navigation_covid19, R.id.navigation_scholars)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DictionaryHandler dictionaryHandler = new DictionaryHandler();
        Thread dictionaryThread = new Thread(dictionaryHandler);
        dictionaryThread.start();

        String fileDir = getApplicationContext().getFilesDir().getPath() + "/";
        InitThreadHandler initThreadHandler = new InitThreadHandler(fileDir);
        Thread initThread = new Thread(initThreadHandler);
        initThread.start();



        /*
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
        */


        // FragmentManager fragmentManager = getSupportFragmentManager();
        // Fragment newsFragment = fragmentManager.findFragmentById(R.id.navigation_news);

    }

}