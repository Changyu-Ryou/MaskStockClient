package com.DevR.mask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    static String currentPageUrl = "";
    static String firstURL = "";
    static WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    //static String from=MyFirebaseMessagingService.from;
    //static String title=MyFirebaseMessagingService.title;
    //static String body=MyFirebaseMessagingService.body;
    //static Map<String, String> data=MyFirebaseMessagingService.data;
    private FirebaseAnalytics mFirebaseAnalytics;
    Intent intent;
    String blogurl = "";
    private AdView mAdView;     //하단배너광고
    static Context context;
    static SwipeRefreshLayout swipe;       //swipe reload
    //SwipeRefreshLayout mySwipeRefreshLayout;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;
        String id = "id";
        String name = "chan";
        intent = getIntent();

        //하단 배너 init
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        // 전면광고 초기화

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId("ca-app-pub-7742126992195898/9239118392"); // * 자신의 전면광고 단위 아이디
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        // 토큰이 등록되는 시점에 호출되는 메소드 입니다.
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,

                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        String newToken = instanceIdResult.getToken();
                        Log.d("TAG", "새토큰" + newToken);

                    }
                }

        );

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(token);
                        Log.d("TAG:", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        //이렇게 ALL 추가 하면 이 디바이스는 ALL을 구독한다는 얘기가 된다.
        FirebaseMessaging.getInstance().subscribeToTopic("ALL");

        /// createNotification("현재 마스크 재고 알림이 작동중입니다.");
        //notifon();

        // 웹뷰 시작
        mWebView = (WebView) findViewById(R.id.webView);


        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스크립트 허용 여부
        // mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        //  mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        //mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        // mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        //mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        //mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        //mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부  네트워크로 받아옴
        //mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        MyWebViewClient testChromeClient = new MyWebViewClient();
        mWebView.setWebViewClient(testChromeClient);
        mWebView.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        // mWebView.setWebViewClient(new WebViewClientClass());//새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용

        mWebView.setWebChromeClient(new WebChromeClient() {         //웹뷰 내 팝업창 구현
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(context)
                        .setTitle("Pop up")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
        System.out.println("url main=" + intent.getStringExtra("url"));


        if (intent.getStringExtra("url") == null) {
            //mWebView.loadUrl("http://www.naver.net"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
            jsoupAsyncTask.execute();
            //mWebView.loadUrl(blogurl); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        } else
            mWebView.loadUrl(intent.getStringExtra("url"));


        firstURL = mWebView.getUrl();
        currentPageUrl = "firstpage";
    }


    @Override
    public void onBackPressed() {
        // 혹시 웹 뷰를 사용하는 액티비티일 경우 웹뷰 뒤로가기를 구현해준다.
        System.out.println(mWebView.getUrl());
        if (mWebView.canGoBack()) {
            currentPageUrl = mWebView.getUrl();
            mWebView.goBack();
            System.out.println("can go");
        } else {
            // 2. 다이얼로그를 생성한다.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("종료하시겠습니까?");
            builder.setMessage("'마스크 재판매 알리미'를 종료하셔도 마스크 알림은 계속 받으실 수 있습니다.");
            builder.setNegativeButton("취소", null);
            builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 3. 다이얼로그의 긍정 이벤트일 경우 종료한다.
                    finish();

                }
            });
            builder.show();
        }


        //super.onBackPressed();



    }

/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//뒤로가기 버튼 이벤트
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {//웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            mWebView.goBack();
            return true;
        }else{

        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void reflash(View view) {
        mWebView.reload();
    }

    public void home(View view) {

        if (interstitialAd.isLoaded()) {
            //  System.out.println("아니다");*/
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // 사용자가 광고를 닫으면 뒤로가기 이벤트를 발생시킨다.
                    System.out.println("전면광고 로드");
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                    while(true) {
                        if(mWebView.canGoBack()){
                            System.out.println("\ngoback=========="+blogurl);
                            mWebView.goBack();
                        }else{
                            System.out.println("\nelse진입=========="+blogurl);
                            System.out.println("blogurl="+blogurl);
                            if(blogurl.equals("")){
                                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                                jsoupAsyncTask.execute();
                            }
                            break;
                        }
                    }


                }
            });
        }else{

            while(true) {
                if (mWebView.canGoBack()) {
                    System.out.println("\ngoback==========" + blogurl);
                    mWebView.goBack();
                } else {
                    System.out.println("\nelse진입==========" + blogurl);
                    System.out.println("blogurl=" + blogurl);
                    if (blogurl.equals("")) {
                        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                        jsoupAsyncTask.execute();
                    }
                    break;
                }
            }


        }

    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String mainpageLink = "https://raw.githubusercontent.com/Changyu-Ryou/MaskStockClient/master/link.txt"; //git link.txt
            Document doc = null;
            try {
                doc = Jsoup.connect(mainpageLink).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("doc-" + doc);
            Elements titles = doc.select("pre");
            for (Element e : titles) {
                System.out.println("title: " + e.text());
                blogurl += e.text();
            }
            Element body = doc.body();
            String aa = String.valueOf(body);
            blogurl = Html.fromHtml(aa).toString();
            System.out.println("url-" + blogurl);


            return blogurl;
        }

        @Override
        protected void onPostExecute(String result) {     //텍스트를 어찌할건지 이후에 정함
            mWebView.loadUrl(result);
        }
    }

    // 서비스로 부터 인텐트를 받았을 때의 처리.
    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent 호출됨");

        // 인텐트를 받은 경우만, 값을 Activity로 전달하도록 합니다.
        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);

    }


    // 인텐트를 처리하도록 합니다.
    private void processIntent(Intent intent) {

        String from = intent.getStringExtra("from");
        if (from == null) {

            // from 값이 없는 경우, 값을 전달하지 않습니다. (푸쉬 노티 메시지가 아닌것을 판단하고 처리하지 않는듯).
            Log.d("TAG", "보낸 곳이 없습니다.");
            return;

        }


        intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.d("TAG URL", "urlurlurlurlurl==." + url);
        mWebView.loadUrl(url);

        /*
        TextView aa = findViewById(R.id.text);
        aa.setText(MyFirebaseMessagingService.from);
        System.out.println("dataaaaaaa==="+MyFirebaseMessagingService.urlString);


        Bundle bundle = new Bundle();
        bundle.putString("url", MyFirebaseMessagingService.urlString);
        intent.putExtras(bundle);
        intent.putExtra("from", from);
        intent.putExtra("title", MyFirebaseMessagingService.messageTitle);
        intent.putExtra("body", MyFirebaseMessagingService.messageBody);
        //intent.putExtra("contents", MyFirebaseMessagingService.contents);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        startActivity(intent);
        // 메시지를 받은 것우 처리를 합니다.
        Log.d( "TAG", "여기서 메시지 응답 처리를 하면 됩니다." );
*/

    }


}


/*
    // 서비스로 부터 인텐트를 받았을 때의 처리.
    @Override
    protected void onNewIntent(Intent intent) {
        println( "onNewIntent 호출됨" );

        // 인텐트를 받은 경우만, 값을 Activity로 전달하도록 합니다.
        if( intent != null )
        {
            processIntent( intent );
        }

        super.onNewIntent(intent);

    }



    // 인텐트를 처리하도록 합니다.
    private void processIntent( Intent intent ){

        String from = intent.getStringExtra("from");
        if( from == null)
        {

            // from 값이 없는 경우, 값을 전달하지 않습니다. (푸쉬 노티 메시지가 아닌것을 판단하고 처리하지 않는듯).
            Log.d( "TAG", "보낸 곳이 없습니다." );
            return;

        }


        // 메시지를 받은 것우 처리를 합니다.
        //Log.d( "TAG", "여기서 메시지 응답 처리를 하면 됩니다." );
        TextView aa = findViewById(R.id.text);
        //aa.setText(MyFirebaseMessagingService.from);
        createNotification2(MyFirebaseMessagingService.from);

    }

    private void createNotification2(String aa) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(aa);
        builder.setContentText("");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        //builder.setOngoing(true);
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(2, builder.build());
    }


    private void createNotification(String aa) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(aa);
        builder.setContentText("재고 감지중");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }

    @Override
    protected void onDestroy() {
        removeNotification();
        super.onDestroy();
    }
}
*/