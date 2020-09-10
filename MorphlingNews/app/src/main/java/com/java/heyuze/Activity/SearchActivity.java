package com.java.heyuze.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.InfoManager;
import com.java.heyuze.R;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;

    private void showToast() {
        TextView textView = new TextView(this);
        textView.setText("搜索功能未加载完毕，请稍等！");
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(textView);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = this.getIntent();
        final String type = intent.getStringExtra("type");

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                if (!InfoManager.getInstance().isReadyForSearch()) {
                    showToast();
                    return;
                }
                System.out.println("received " + string);
                Intent intent = new Intent(SearchActivity.this, SearchContentActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("word", string);
                SearchActivity.this.startActivity(intent);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }
}
