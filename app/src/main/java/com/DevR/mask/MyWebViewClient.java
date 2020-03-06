package com.DevR.mask;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class MyWebViewClient extends WebViewClient {
    public static final String INTENT_URI_START = "intent:";
    public static final String INTENT_FALLBACK_URL = "browser_fallback_url";
    public static final String URI_SCHEME_MARKET = "market://details?id=";
    public static String currentpage="";
    public static View myView;
    public static String mviewURL="";

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon){
        MainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String uri) {
        if (uri.toLowerCase().startsWith(INTENT_URI_START)) {
            Intent parsedIntent = null;
            try {
                parsedIntent = Intent.parseUri(uri, 0);
                MainActivity.context.startActivity(parsedIntent);
            } catch(ActivityNotFoundException | URISyntaxException e) {
                return doFallback(view, parsedIntent);
            }
        } else {
            view.loadUrl(uri);
        }

        return true;
    }


    public boolean doFallback(WebView view, Intent parsedIntent) {

        if (parsedIntent == null) {
            return false;
        }

        String fallbackUrl = parsedIntent.getStringExtra(INTENT_FALLBACK_URL);
        if (fallbackUrl != null) {
            view.loadUrl(fallbackUrl);
            return true;
        }

        String packageName = parsedIntent.getPackage();
        if (packageName != null) {
            MainActivity.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + packageName)));
            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mviewURL = view.getUrl();
        String blank="";
        if(MainActivity.firstURL==null){
            MainActivity.firstURL=view.getUrl();
        }
        currentpage=view.getUrl();

        MainActivity.progressBar.setVisibility(View.GONE);

       /* String mainlink=MainActivity.firstURL;
        System.out.println("view.getUrl()="+view.getUrl()+"\n");
        System.out.println("currentpage="+currentpage+"\n");
        System.out.println("MainActivity.currentPageUrl="+MainActivity.currentPageUrl+"\n");
        System.out.println("mainlink="+mainlink+"\n");
        System.out.println("MainActivity.firstURL="+MainActivity.firstURL+"\n\n\n");
        System.out.println("\n  \n");*/

        //if(!view.getUrl().equals(mainlink)&&view.getUrl().equals(currentpage)){
         //   MainActivity.oneMoreBack();
       // }


        // WebView의 페이지 로드가 완료되면 콜백의 형태로 이 메쏘드가 호출됩니다..
        // 좀 더 정확하게는 WebView가 이벤트 발생하는 경우 WebViewClient의 선언된 메쏘드들을 호출하고,
        // 요 형태는 전형적인 옵저버 패턴의 모습입니다.


        //분기햇을경우 이곳에서 처리해주면 될듯

    }





}