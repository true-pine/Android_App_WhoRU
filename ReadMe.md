# WhoRU - 내 휴대폰의 잠금을 해제하려고 한 사람을 알 수 있는 어플
![previewImage](https://github.com/true-pine/Android_App_WhoRU/blob/master/docs/preview.webp)
### 1. 개발배경  
내 휴대폰의 잠금을 누군가 해제하려고 시도했을 때 누구인지 증거를 남길 수 없을까? 라는 생각으로 어플을 개발  
### 2. 주요기능  
휴대폰의 잠금해제를 3번 실패하면 전면 카메라로 사진을 촬영하여 갤러리에 저장  
### 3. 핵심기술  
관리자 권한을 얻을 수 있는 DevicePolicyManager 클래스와 DeviceAdminReceiver 클래스를 이용하여  
잠금해제 실패 이벤트 발생마다 현재 실패한 횟수를 카운트하고  
그 횟수가 3회가 되면 카메라 촬영 서비스를 동적으로 실행시킴  
### 4. 배운 점  
- 안드로이드 4대 구성요소 중 Activity, Receiver, Service
- KeyguardManager : 잠금화면을 관리하는 클래스
- DevicePolicyManager : 관리자 권한을 관리하는 클래스
- Android 6.0(API 23) 이상 기기를 위한 퍼미션 요청 및 체크
- Toolbar, Switch와 같은 View
