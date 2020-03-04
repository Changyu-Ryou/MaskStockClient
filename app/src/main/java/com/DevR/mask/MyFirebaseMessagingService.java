package com.DevR.mask;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
   // static String data;
    Map<String, String> data;
    static String from;
    RemoteMessage remoteMessage1;
    static String urlString;
    static String messageBody;
    static String messageTitle;

    public MyFirebaseMessagingService() {
    }

    // 새로운 토큰을 확인했을 때 호출되는 메소드.
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // 토큰 정보를 출력합니다.
        Log.e(TAG, "onNewToken 호출됨: " + token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        remoteMessage1=remoteMessage;
        // 일단 받은 데이터 중, 내용만 가지고와 출력하는 메소드 입니다. (파이어 베이스 홈페이지에서 보내면 데이터는 값이 없을 수 있습니다.)
        from = remoteMessage.getFrom();
       // Log.d(TAG,
      //          "title:" + remoteMessage.getNotification().getTitle()
       //                 + ", body:" + remoteMessage.getNotification().getBody()
      //                  + ", data:" + remoteMessage.getData()
      //  );

        data=remoteMessage.getData();
        urlString = (String) data.get("url");
        messageBody =  data.get("body");
        messageTitle = data.get("title");
        System.out.println("url===="+urlString);


      //  String messageBody = remoteMessage.getNotification().getBody();
      //  String messageTitle = remoteMessage.getNotification().getTitle();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("url", urlString);
        intent.putExtras(bundle);
        intent.putExtra("from", from);
        intent.putExtra("title", messageTitle);
        intent.putExtra("body", messageBody);
        intent.putExtra("contents", messageBody+"11");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "/topics/ALL";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.mask)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "/topics/ALL";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());








        // 액티비티 쪽으로 메시지를 전달하는 메소드를 호출합니다. 메시지 받으면 바로 켜지도록
      /*  sendToActivity(
                getApplicationContext()
                , remoteMessage.getFrom()
                , remoteMessage.getNotification().getTitle()
                , remoteMessage.getNotification().getBody()
                , remoteMessage.getData().toString()
        );

        sendToActivity(
                getApplicationContext()
                , remoteMessage.getFrom()
                , null
                , null
                , remoteMessage.getData().toString()
        );*/

    }



    // Activity 쪽으로 메소드를 전달하는 메소드 입니다.
    private void sendToActivity(Context context, String from, String title, String body, String contents ){
      /*  Log.d(TAG,
                "title:" + remoteMessage1.getNotification().getTitle()
                        + ", body:" + remoteMessage1.getNotification().getBody()
                        + ", data:" + remoteMessage1.getData()
        );*/
        //Uri uri = Uri.parse(data);
        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", urlString);
        intent.putExtras(bundle);
        intent.putExtra("from", from);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("contents", contents);


        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
       );


        context.startActivity(intent);


    }


}







/*
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static String aaa;
    private static final String TAG = "FCM";
    static String from;
    static String title;
    static String body;
    static Map<String, String> data;


    public MyFirebaseMessagingService() {
    }

    // 새로운 토큰을 확인했을 때 호출되는 메소드.
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // 토큰 정보를 출력합니다.
        Log.e(TAG, "onNewToken 호출됨: " + token);

    }


    // 새로운 메시지를 받았을 때 호출되는 메소드.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 일단 받은 데이터 중, 내용만 가지고와 출력하는 메소드 입니다. (파이어 베이스 홈페이지에서 보내면 데이터는 값이 없을 수 있습니다.)
        from = remoteMessage.getFrom();




 */

       /* title=remoteMessage.getNotification().getTitle();
        body=remoteMessage.getNotification().getBody();
        data=remoteMessage.getData();
        Log.d(TAG,
                "title:" + title
                        + ", body:" + body
                        + ", data:" + data
        );

        aaa="title:" + remoteMessage.getNotification().getTitle()
                + ", body:" + remoteMessage.getNotification().getBody()
                + ", data:" + remoteMessage.getData();*/
    // 액티비티 쪽으로 메시지를 전달하는 메소드를 호출합니다.
        /*
        sendToActivity(
                getApplicationContext()
                , remoteMessage.getFrom()
                , remoteMessage.getNotification().getTitle()
                , remoteMessage.getNotification().getBody()
                , remoteMessage.getData().toString()
        );
*/


        /*
        sendToActivity(
                getApplicationContext()
                , remoteMessage.getFrom()
                , null
                , null
                , remoteMessage.getData().toString()
        );

    }



    // Activity 쪽으로 메소드를 전달하는 메소드 입니다.
    private void sendToActivity(Context context, String from, String title, String body, String contents ){


        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("contents", contents);


        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );


        context.startActivity(intent);


    }



}
*/