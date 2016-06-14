package com.google.gh.todolist;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class NewsContentActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
//        DialogReceiver dialogReceiver = new DialogReceiver();
//        IntentFilter filter = new IntentFilter("com.test");
//        registerReceiver(dialogReceiver,filter);
        if(getIntent().getData()!=null){
            try{
                String uri = getIntent().getData().toString();
                webView= (WebView) findViewById(R.id.webview);
//                webView.loadUrl(uri);
                webView.loadUrl("file:///android_asset/demo.html");
//                String fileNames[] =this.getAssets().list("");
//                Toast.makeText(this,fileNames[0],Toast.LENGTH_SHORT).show();
                //设置本地调用对象及其接口
                webView.addJavascriptInterface(new JavaScriptObject(this), "myObj");
//                打开页面时， 自适应屏幕：
                WebSettings webSettings =   webView .getSettings();
                webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//                便页面支持缩放：
                webSettings.setJavaScriptEnabled(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setSupportZoom(true);
//                webView.loadData(uri, "text/html; charset=UTF-8", "UTF-8");
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:funFromjs()");
                Toast.makeText(NewsContentActivity.this, "调用javascript:funFromjs()", Toast.LENGTH_LONG).show();

                 sendBroadcast(new Intent("com.test"));
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        if(webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class JavaScriptObject {
        Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface //sdk17版本以上加上注解
        public void fun1FromAndroid(String name) {
            Toast.makeText(mContxt, name, Toast.LENGTH_LONG).show();
        }
        @JavascriptInterface //sdk17版本以上加上注解
        public void fun2(String name) {
            Toast.makeText(mContxt, "调用fun2:" + name, Toast.LENGTH_SHORT).show();
        }
    }

    public class DialogReceiver extends BroadcastReceiver {
        public DialogReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("fdg").setIcon(R.mipmap.ic_launcher)
                    .setView(R.layout.activity_main)
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
    }

}
