# MaskStockClient
> [App] 국내 온라인 마스크 판매처의 판매 정보를 공개하고, 알림을 보내주는 앱입니다. 또한, 위치 한 곳의 근처 약국의 공적마스크 재고 현황정보 서비스도 제공합니다.
<br/>

## 📖 Introduction  
이번 프로젝트는 실질적으로 출시되어질 mask Client app과<br/>
서버측에서 push를 자동으로 보내주는 mask Server app.<br/>
그리고 client 앱의 웹뷰에 보여질 mask Web. 이 세 개를 동시에 진행한 프로젝트입니다.<br/><br/>

우선 이 Android App개발을 통해 게릴라, 지정시간 판매 마스크의 정보를 빠르게 제공하고, 공적마스크 정보 또한 알려주는 앱 개발을 목표로 하고 있습니다.<br/>
1. Firebase를 통한 PUSH 기능 구현
2. 하이브리드 앱 형태로 웹뷰 구현
3. 웹뷰 내 새로고침 버튼, 홈버튼 구현
4. 웹뷰에 띄울 웹 구현
5. 공적마스크 재고 알림을 위한 네이버 MAPS API를 통한 기능 구현
  <br/><br/><br/>
## 👨‍💻 System requirements
기본적으로 Android Studio에서 JAVA언어 기반으로 개발을 진행합니다.
이를위해 Android Studio 설치가 필수적입니다.
또한 안드로이드 SDK Android 10 / API Level 29 을 타겟으로 개발합니다.
  <br/><br/><br/>

## 📝 Todo list
제작할 코드와 문서들입니다.

- [x] [💻] 웹뷰 생성
- [x] [💻] Firebase 프로젝트 생성 및 연동
- [x] [💻] 앱 내 Notification 처리 코드 작성
- [x] [💻] PUSH를 자동으로 보내줄 Server측 구현
- [x] [💻] 웹뷰 새로고침 버튼, 뒤로가기, 메인화면 이동 버튼 구현
- [x] [💻] 웹뷰에 띄울 웹 사이트 구현
- [x] [🔨] 웹 서버 변경을 대비한 link.txt파일 생성
- [x] [🔨] 애드몹 구현
- [x] [🔨] 로고제작 및 앱 홍보 이미지 제작
- [x] [🔒] 안드로이드 플레이스토어 출시
  <br/> <br/><br/>
  
## 📝 웹뷰 생성
웹뷰는 Chromium 기반으로 생성되어지는 웹뷰를 구현하였습니다. <br/>
세부 설정은 아래와 같습니다.<br/>
<img src="https://user-images.githubusercontent.com/56837413/78501199-a462eb00-7795-11ea-87eb-ec2664dd21eb.png" width="80%"></img>
  <br/> <br/><br/>
   
## 📝 Firebase 프로젝트 생성 및 연동
Firebase의 cloud messaging 기능 사용을 위해 firebase에 새 프로젝트를 생성하고 앱을 등록했습니다.
<img src="https://user-images.githubusercontent.com/56837413/78501256-dffdb500-7795-11ea-91da-c41def57379c.png" width="30%"></img>
  <br/> <br/><br/>
  
## 📝 앱 내 Notification 처리 코드 작성
앱에서 PUSH를 받았을때 어떻게 처리를 할지에 관련된 코드를 작성해 두었습니다.<br/>
Push Message에 담겨져있는 title, body, contents, from의 정보를 확인하고 가공하여 사용자의 스마트폰에 Notify합니다.
[MyFirebaseMessagingService.java](https://github.com/Changyu-Ryou/MaskStockClient/blob/master/app/src/main/java/com/DevR/mask/MyFirebaseMessagingService.java).
<br/> <br/><br/>
   
## 📝 PUSH를 자동으로 보내줄 Server측 구현
서버측에서는 두가지의 기능이 있습니다.
1. 온라인 판매처의 페이지를 자동으로 크롤링 하여 재고가 들어오면 알림을 push알림을 보내는 기능
2. 지정 시간 판매처의 경우 지정된 판매시간 5분~10분전 미리 push알림을 보내는 기능


다른 Repository를 만들었지만 firebase의 server 코드가 포함되어져 있어 private로 설정해 두었습니다. 또한 1번 기능의 경우 robots.txt를 통해 crawling이 가능함을 확인했다 하더라도, 쇼핑몰 페이지의 트래픽 부담과 다른 법적인 문제가 있을것 같아 플레이스토어에 앱을 업로드한 이후에는 작동시키지 않았습니다.
   <br/><br/><br/>
   
## 📝 웹뷰 새로고침 버튼, 뒤로가기, 메인화면 이동 버튼 구현
<br/>

1. 뒤로가기  

  뒤로가기 버튼 클릭시 메인화면인지 확인하고 메인화면인 상태에서 뒤로가기를 누르면 로딩창과 함께 전면광고를 띄웁니다.<br/>
  이후 전면광고가 종료되면 종료할지 계속할지를 묻는 다이얼로그가 나타납니다.
<br/> <br/>

2. 메인화면 이동 버튼  

  메인화면 이동 버튼을 클릭하면 어떤 화면에서든 메인 웹사이트 로 이동이 가능합니다.<br/>
  또한 메인 화면 이동시 전면 광고가 뜨게 됩니다. 
<br/><br/>

3. 새로고침 버튼  

  마스크 구매의 과정은 새로고침의 반복과 순발력이 필수라고 할 수 있습니다. 그렇기 때문에 구매하기 버튼 근처에 새로고침 버튼을 배치함으로써 빠른 새 로고침을 가능케하고 '구매'버튼이 떳을때 짧은 이동동선을 가져감으로써 더 빠른 구매를 가능케 돕습니다.

<br/><br/>
<img src="https://user-images.githubusercontent.com/56837413/78503364-1d1b7480-77a1-11ea-9a58-f1f2bed57ff9.jpg" width="30%"></img>
 
 <br/> <br/><br/>
   
## 📝 웹뷰에 띄울 웹 사이트 구현
웹뷰에 띄울 웹사이트는 다른 repository를 생성해 두었습니다.
이 repository에서 공적마스크 api와 네이버 maps api사용 코드와 사이트맵등을 확인할 수 있습니다.<br/>
[MaskStock_web](https://github.com/Changyu-Ryou/MaskStock_web).
<br/><br/>
<img src="https://user-images.githubusercontent.com/56837413/78503423-708dc280-77a1-11ea-8c09-f96c65ef2ee9.jpg" width="30%"></img>
   <br/> <br/><br/>

## 📝 웹 서버 변경을 대비한 link.txt파일 생성
앱을 하이브리드 앱 형태로 구상하면서 웹뷰에 띄울 메인 웹을 만들어야 했습니다. 그러나 당시 호스팅하고 있는 웹이 없었고 방법을 찾을 수 없어 임시방편으로 CAFE24의 무료 호스팅을 사용해야겠다고 생각했습니다. 그러나 사용자가 늘어나면 무료호스팅만으로는 부족할것이라 생각했고 앱 업데이트 없이도 외부에서도 메인 웹 주소를 바꿀 수 있도록 구조를 변경했습니다.<br/><br/>

기존에 안드로이드 스튜디오 내에서 코드를 통해 첫페이지 주소를 정해놓는데 이 주소를 Git 현재 repository에 link.txt에 남겨둡니다.
그러면 이제 앱에서는 앱을 켰을때 link.txt의 내용을 가져와 그 주소를 메인 주소로 사용하는 것입니다.<br/><br/>

나중에는 초기에 쓰던 cafe24의 트래픽 증가와 동시접속자 증가로 인해 다른 호스팅 업체의 무제한 트래픽 플랜을 구매하여 이동했고, link.txt의 수정을 통해 메인 웹 주소를 변경하였습니다.<br/>
[link.txt](https://github.com/Changyu-Ryou/MaskStockClient/blob/master/link.txt).

   <br/> <br/><br/>

## 📝 애드몹 구현
애드몹을 통해 총 3곳에 광고를 송출하였습니다.
   
1. 하단 배너  

<img src="https://user-images.githubusercontent.com/56837413/78503640-ee9e9900-77a2-11ea-8a9b-1da214582311.png" width="30%"></img>
<br/>
2. 종료 직전 전면광고  

<img src="https://user-images.githubusercontent.com/56837413/78503865-3a9e0d80-77a4-11ea-872e-78b51bfc1458.gif" width="30%"></img>
<br/>

3. 메인 화면 이동시 전면광고  

<img src="https://user-images.githubusercontent.com/56837413/78503888-71742380-77a4-11ea-8e38-257c0d67093b.gif" width="30%"></img>
   
  
   
 <br/> <br/><br/>
## 📝 로고제작 및 앱 홍보 이미지 제작
<br/><br/>
> 로고  
<br/><br/>
<img src="https://user-images.githubusercontent.com/56837413/78503948-e182a980-77a4-11ea-9798-cb2233bde7c6.png" width="30%"></img>
<br/>  <br/><br/>


> 이미지  
<br/><br/>
<img src="https://user-images.githubusercontent.com/56837413/78503969-0b3bd080-77a5-11ea-8f8e-91f74e842ead.png" width="91%"></img>
<img src="https://user-images.githubusercontent.com/56837413/78503974-142ca200-77a5-11ea-96d6-d0e1dd33d0da.png" width="30%"></img>
<img src="https://user-images.githubusercontent.com/56837413/78503979-155dcf00-77a5-11ea-92fa-deb420d680f8.png" width="30%"></img>
<img src="https://user-images.githubusercontent.com/56837413/78503982-1a228300-77a5-11ea-9b85-33a2140ead88.png" width="30%"></img>
<img src="https://user-images.githubusercontent.com/56837413/78503983-1c84dd00-77a5-11ea-9844-d59a63faae2c.png" width="30%"></img>

## ☁️ 결과물

<br/>

[![앱 다운받기](https://play.google.com/intl/ko/badges/static/images/badges/ko_badge_web_generic.png)](https://play.app.goo.gl/?link=https://play.google.com/store/apps/details?id=com.DevR.mask&ddl=1&pcampaignid=web_ddl_1)

<br/>  

위 이미지 클릭 시 출시한 앱 google playstore로 이동됩니다.
