package com.java.heyuze.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.OnResponseListener;
import com.java.heyuze.R;

import android.util.Log;

import com.java.heyuze.WXShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.*;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler
{
    private IWXAPI api;
    private WXShare wxShare;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newscontent);
        Intent intent = this.getIntent();
        String info = intent.getStringExtra("info");
        wxShare = new WXShare(this);
        wxShare.setListener(new OnResponseListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onFail(String message)
            {

            }
        });
        Log.e("WXEntryActivity", "WXEntryActivity");
        WXShare share = new WXShare(this);
        api = share.getApi();
        wxShare.share(info);
        try
        {
            if (!api.handleIntent(getIntent(), this))
            {
                finish();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        wxShare.register();
    }

    @Override
    protected void onDestroy()
    {
        wxShare.unregister();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Log.e("onNewIntent", "onNewIntent");
        setIntent(intent);
        if (!api.handleIntent(intent, this))
        {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq)
    {

    }

    @Override
    public void onResp(BaseResp baseResp)
    {
        Intent intent = new Intent(WXShare.ACTION_SHARE_RESPONSE);
        intent.putExtra(WXShare.EXTRA_RESULT, new WXShare.Response(baseResp));
        sendBroadcast(intent);
        finish();
    }

}