package com.example.myrssreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class ActivityShowDescription extends Activity {

        public void onCreate(Bundle icicle) {

            super.onCreate(icicle);
            setContentView(R.layout.activity_show);
            String content = new String();
            String link = null;
            Intent startingIntent = getIntent();

            if (startingIntent != null) {
                Bundle bundle = startingIntent
                        .getBundleExtra("Android.intent.extra.RSSItem");
                if (bundle == null) {
                    content = "不好意思程序出错啦";
                } else {

                	link = bundle.getString("link");
                	/*
                    content = bundle.getString("title") + "\n\n"
                            + bundle.getString("pubdate") + "\n\n"
                            + bundle.getString("description").replace('\n', ' ')
                            + "\n\n详细信息请访问以下网址:\n" + bundle.getString("link");*/
                }
            } else {
                content = "不好意思程序出错啦";
            }

            // WebView显示原网页内容
            WebView wv = (WebView) findViewById(R.id.web_holder);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setBlockNetworkImage(false);
            wv.setWebViewClient(new WebViewClient());
            wv.loadUrl(link);
        }

    }