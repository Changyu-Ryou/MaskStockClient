package com.DevR.mask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_VALUE_INT = -1;
    static String currentPageUrl = "";
    static String firstURL = "";
    static WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    //static String from=MyFirebaseMessagingService.from;
    //static String title=MyFirebaseMessagingService.title;
    //static String body=MyFirebaseMessagingService.body;
    //static Map<String, String> data=MyFirebaseMessagingService.data;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final IntentFilter s_intentFilter; //시간변화감지용

    RelativeLayout setlay;
    RelativeLayout homelay;

    ImageButton refreshBtn;
    ImageButton homeBtn;
    ImageButton setBtn;
    ImageButton boxBtn;

    static int subscribe = 0;        //fcm 구독값 0==all, 1==구독x
    String rewordtime="";

    Intent intent;
    String blogurl = "";
    private AdView mAdView;     //하단배너광고
    static Context context;
    static SwipeRefreshLayout swipe;       //swipe reload
    //SwipeRefreshLayout mySwipeRefreshLayout;
    private InterstitialAd interstitialAd;
    private InterstitialAd minterstitialAd;
    private int nBefore = 0;                //새로고침 버튼

    public ProgressDialog progressDialog;
    public static ProgressBar progressBar;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setlay = (RelativeLayout) findViewById(R.id.setlayout);
        homelay = (RelativeLayout) findViewById(R.id.homelayout);

        setBtn = (ImageButton) findViewById(R.id.setting);
        boxBtn = (ImageButton) findViewById(R.id.box);
        homeBtn = (ImageButton) findViewById(R.id.home);
        refreshBtn = (ImageButton) findViewById(R.id.renew);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        progressBar.bringToFront();

        homeBtn.bringToFront();
        refreshBtn.bringToFront();

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
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("EEA172A68DC419954C6AB203C2B2B3B4").build();        //갤럭시 테스트 기기 등록
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // 전면광고 초기화 홈버튼
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // * 자신의 전면광고 단위 아이디
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        // 종료 직전 광고 초기화 홈버튼
        minterstitialAd = new InterstitialAd(context);
        minterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // * 자신의 전면광고 단위 아이디
        minterstitialAd.loadAd(new AdRequest.Builder().build());

        minterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                minterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        subscribe = 0;
        getPreferences();       //저장된 구독 값 알아오기


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
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        // mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        //mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        //mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        //mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 브라우저 캐시 허용 여부  네트워크로 받아옴
        mWebSettings.setGeolocationEnabled(true);
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        MyWebViewClient testChromeClient = new MyWebViewClient();
        mWebView.setWebViewClient(testChromeClient);
        //mWebView.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        });


        // mWebView.setWebViewClient(new WebViewClientClass());//새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용


        ////////////////////////////https://play.app.goo.gl/?link=https://play.google.com/store/apps/details?id=com.DevR.mask&ddl=1&pcampaignid=web_ddl_1
        ////////인텐트링크


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

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });
        System.out.println("url main=" + intent.getStringExtra("url"));


        if (intent.getStringExtra("url") == null) {
            //mWebView.loadUrl("http://www.naver.net"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
            jsoupAsyncTask.execute();
            mWebView.loadUrl("https://www.mask-alarm.pe.kr"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        } else
            mWebView.loadUrl(intent.getStringExtra("url"));


        firstURL = mWebView.getUrl();
        currentPageUrl = "firstpage";


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

        getPreferences();
        registerReceiver(m_timeChangedReceiver, s_intentFilter);
    }

    //////시간 변화 감지
    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }


    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // sendPostToFCM();
            chekcTime();            //시간 확인용
        }
    };


    void chekcTime() {

        long now = System.currentTimeMillis();
        Date dateNow = new Date(now);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date dateCreated = dateFormat.parse(rewordtime); //String을 포맷에 맞게 변경
            long duration = dateNow.getTime() - dateCreated.getTime(); // 글이 올라온시간,현재시간비교
            long min = duration / 60000;

            if (min >= 3) { // 5분이상 지났을때
                System.out.println("20분이 지났습니다."+min);
                mAdView.setVisibility(View.VISIBLE);

            } else {
                System.out.println("20분이 지나지 않았습니다."+min);
                mAdView.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        savePreferences(rewordtime);

    }


    // 리워드 시간 불러오기
    private void getPreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        rewordtime = pref.getString("time", "0");
        if(!rewordtime.equals("0")){
            chekcTime();
        }
    }

    // 값 저장하기
    private void savePreferences(String a) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("time", a);
        editor.commit();
    }


    public void loading() {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 100);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }


    @Override
    public void onBackPressed() {
        // 혹시 웹 뷰를 사용하는 액티비티일 경우 웹뷰 뒤로가기를 구현해준다.
        System.out.println(mWebView.getUrl());
        if (mWebView.canGoBack()) {
            currentPageUrl = mWebView.getUrl();
            mWebView.goBack();
            System.out.println("can go");
            System.out.println(mWebView.getUrl());
        } else if (mWebView.getUrl().equals("https://www.mask-alarm.pe.kr/") || mWebView.getUrl().equals("http://mask-alarm.pe.kr/index.html")
                || mWebView.getUrl().equals("https://www.mask-alarm.pe.kr/index.html") || mWebView.getUrl().equals("https://mask-alarm.pe.kr/")) {
            if (minterstitialAd.isLoaded()) {

                final Handler mHandler = new Handler() {            // 실행이 끝난후 확인 가능
                    public void handleMessage(Message msg) {
                        System.out.println("위에 \n");
                        loadingEnd();
                        minterstitialAd.show();
                    }
                };

                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩
                        loading();
                        System.out.println("아래에 \n");
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessageDelayed(message, 600);    // 실행이 끝난후 알림
                    }
                }, 0);

                //로딩progress
                //  System.out.println("아니다");*/
                //minterstitialAd.show();
                minterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        //loadingEnd();
                        // 사용자가 광고를 닫으면 뒤로가기 이벤트를 발생시킨다.
                        System.out.println("전면광고 로드");
                        minterstitialAd.loadAd(new AdRequest.Builder().build());
                        while (true) {
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

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when the ad is displayed.
                        quitdialog();
                    }

                });
            } else {

                quitdialog();

            }


        } else {                                        //앱 종료시
            // 2. 다이얼로그를 생성한다.

            if (minterstitialAd.isLoaded()) {

                final Handler mHandler = new Handler() {            // 실행이 끝난후 확인 가능
                    public void handleMessage(Message msg) {
                        System.out.println("위에 \n");
                        loadingEnd();
                        minterstitialAd.show();
                    }
                };

                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩
                        loading();
                        System.out.println("아래에 \n");
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessageDelayed(message, 600);    // 실행이 끝난후 알림
                    }
                }, 0);

                //로딩progress
                //  System.out.println("아니다");*/
                //minterstitialAd.show();
                minterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        //loadingEnd();
                        // 사용자가 광고를 닫으면 뒤로가기 이벤트를 발생시킨다.
                        System.out.println("전면광고 로드");
                        minterstitialAd.loadAd(new AdRequest.Builder().build());
                        while (true) {
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

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when the ad is displayed.
                        quitdialog();
                    }


                });
            } else {
                quitdialog();
            }
        }


        //super.onBackPressed();


    }


    void quitdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료하시겠습니까?");
        builder.setMessage("'마스크 재판매 알리미'를 종료하셔도 마스크 알림은 계속 받으실 수 있습니다.");
        builder.setNegativeButton("취소", null);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 3. 다이얼로그의 긍정 이벤트일 경우 종료한다.
                finishAffinity();
                System.runFinalization();
                System.exit(0);


            }
        });
        builder.show();

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
        if (setlay.getVisibility() == View.VISIBLE) {
            closewallet();
        }

        reflashRotation(nBefore - 360);
        mWebView.reload();
    }

    public void reflashRotation(int i) {
        RotateAnimation ra = new RotateAnimation(
                nBefore,
                i,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        ra.setDuration(250);
        ra.setFillAfter(true);
        refreshBtn.startAnimation(ra);
        nBefore = i;
    }

    public void home(View view) {

        /*
        Animation anim = AnimationUtils.loadAnimation
                (getApplicationContext(), // 현재화면의 제어권자
                        R.anim.translate_anim);   // 에니메이션 설정 파일
        iv.startAnimation(anim);

        */

        closewallet();

        if (interstitialAd.isLoaded()) {

            final Handler mHandler = new Handler() {            // 실행이 끝난후 확인 가능
                public void handleMessage(Message msg) {
                    System.out.println("위에 \n");
                    loadingEnd();
                    interstitialAd.show();
                }
            };

            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override
                public void run() {
                    // 실행할 동작 코딩
                    loading();
                    System.out.println("아래에 \n");
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessageDelayed(message, 500);    // 실행이 끝난후 알림
                }
            }, 0);


            //  System.out.println("아니다");*/
            //interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // 사용자가 광고를 닫으면 뒤로가기 이벤트를 발생시킨다.
                    System.out.println("전면광고 로드");
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                    while (true) {
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
            });
        } else {
            mWebView.loadUrl("https://www.mask-alarm.pe.kr");
        }

    }


    public void box(View view) {

        if (setlay.getVisibility() != View.VISIBLE) {
            openwallet();
        } else {
            closewallet();
        }

    }

    void openwallet() {
        boxBtn.setImageResource(R.drawable.closebtn);
        Animation anim = AnimationUtils.loadAnimation
                (getApplicationContext(), // 현재화면의 제어권자
                        R.anim.translate_anim);   // 에니메이션 설정 파일
        homelay.setVisibility(View.VISIBLE);
        homelay.startAnimation(anim);
        setlay.setVisibility(View.VISIBLE);
        setlay.startAnimation(anim);


    }

    void closewallet() {
        boxBtn.setImageResource(R.drawable.boxbtn);
        Animation anim = AnimationUtils.loadAnimation
                (getApplicationContext(), // 현재화면의 제어권자
                        R.anim.traslate_close);   // 에니메이션 설정 파일
        setlay.startAnimation(anim);
        setlay.setVisibility(View.INVISIBLE);
        homelay.startAnimation(anim);
        homelay.setVisibility(View.INVISIBLE);

    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("광고 제거");
        builder.setMessage("광고를 보시면 하단 배너를 1시간동안 없애드립니다. 광고를 보시겠습니까?");
        builder.setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //예 눌렀을때의 이벤트 처리
                        mAdView.setVisibility(View.GONE);

                        rewordTime();           //리워드 받은 시간 저장

                    }
                });
        builder.setPositiveButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdView.setVisibility(View.VISIBLE);
                        //아니오 눌렀을때의 이벤트 처리
                    }
                });
        builder.show();
    }

    void rewordTime() {

        long now = System.currentTimeMillis();
        Date dateNow = new Date(now);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        rewordtime=dateFormat.format(dateNow);;
        savePreferences(rewordtime);
        System.out.println("savwtime="+rewordtime);

    }


    public void settingact(View view) {     //세팅버튼 클릭시 이동

        closewallet();
        deleteDialog();     //광고보고 배너 없앨건지 물음


  /*
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }else{
            Toast.makeText(context,"현재 GPS가 켜져 있습니다.",Toast.LENGTH_SHORT).show();

        }

        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);



        if(subscribe==0) {
            subscribe = 1;
            System.out.println("subscribe=1로 바꿈");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("ALL");
            FirebaseMessaging.getInstance().subscribeToTopic("1");
        }else{
            subscribe=0;
            System.out.println("subscribe=0으로 바꿈");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("1");
            FirebaseMessaging.getInstance().subscribeToTopic("ALL");
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic("ALL");
        FirebaseMessaging.getInstance().subscribeToTopic("1");

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
*/
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