package com.DevR.mask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    static WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    //static String from=MyFirebaseMessagingService.from;
    //static String title=MyFirebaseMessagingService.title;
    //static String body=MyFirebaseMessagingService.body;
    //static Map<String, String> data=MyFirebaseMessagingService.data;
    private FirebaseAnalytics mFirebaseAnalytics;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String id = "id";
        String name = "chan";
        intent = getIntent();
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
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        System.out.println("url main="+intent.getStringExtra("url"));
        if (intent.getStringExtra("url") == null)
            mWebView.loadUrl("http://www.naver.net"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        else
            mWebView.loadUrl(intent.getStringExtra("url"));


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


    public void btn(View view) {
        finish();
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