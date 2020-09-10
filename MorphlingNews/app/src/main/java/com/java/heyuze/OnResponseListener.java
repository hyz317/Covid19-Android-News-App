package com.java.heyuze;


public interface OnResponseListener
{
    void onSuccess(); //分享成功的回调

    void onCancel(); //分享取消的回调

    void onFail(String message); //分享失败的回调
}
