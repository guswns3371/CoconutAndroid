# 시스템 구조

- 클라이언트 : (
  Android/Kotlin) [https://github.com/guswns3371/Coconut](https://github.com/guswns3371/Coconut)
- 서버 : (Spring Boot
  Application) [https://github.com/guswns3371/coconut-spring-server](https://github.com/guswns3371/coconut-spring-server)

# 시연 영상

[https://www.youtube.com/watch?v=2taqqWY0Bdc](https://www.youtube.com/watch?v=2taqqWY0Bdc)


---

- [커밋 제목 규칙]
    - 앞에 키워드를 적고 코드에 관한 설명 적기
- [ADD]
    - 기능 추가
- [DELETE]
    - 기능 삭제
- [MODIFY]
    - 기능 변경
- [BUGFIX]
    - 버그 수정
- [REFACTORING]
    - 코드 리팩토링

---

- 메인 코드에 직접적인 수정이 발생한 경우
- [FORMAT]
    - 코드형식 정렬 주석 등을 변경했을때

---

- 메인 코드 수정했으나 동작에 영향 주는 변화 없음
- [TEST]
    - 테스트코드 수정, 추가, 삭제시
- [DOC]
    - 문서 추가 삭제 변경(리드미 같은거)
- [PROJECT]
    - 프로젝트 관리 측면에서 변경한거(빌드스크립트 수정, git 설정, 배포 설정 등)
- [ETC]
    - 위에 해당되지 않는 모든 변경

---  
Ex)

- 회원가입 기능 추가해서 올릴시(main 코드 부분)
    - [ADD] user join
- 회원 가입 기능 테스트 완료시(test 코드 부분)
    - [TEST] join test finished

---

# OAuth2.0

- secret.gradle 파일과 secret.keystore 파일이 있어야 build 가능.

1. secret.gradle 파일 양식

```gradle
ext {

    // modify the apiKey and clientPrefix

    apiKey = "YOUR_API_KEY"

    clientPrefix = "YOUR_CLIENT_ID_PREFIX"

    // google identity service

    discoveryEndpoint = "https://accounts.google.com/.well-known/openid-configuration"

    authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth"
    tokenEndpoint = "https://www.googleapis.com/oauth2/v4/token"
    userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo"

    // google identity client

    clientId = "${clientPrefix}.apps.googleusercontent.com"
    redirectScheme = "com.googleusercontent.apps.${clientPrefix}"
    redirectUri = "com.googleusercontent.apps.${clientPrefix}:/oauth2redirect"

    authorizationScope = "openid email profile https://www.googleapis.com/auth/books"

    // client keystore

    secret = "secret"

    keystore = "${secret}.keystore"
}
```

2. secret.keystore 생성 방법

`keytool -genkey -keystore secret.keystore -alias secret -keyalg RSA -keysize 2048 -validity 10000 -keypass secret -storepass secret -dname 'CN=secret'`

- `keytool -list -v -keystore secret.keystore -storepass secret | grep SHA1` 명령어로
  SHA1값을 [Google Credentials](https://console.cloud.google.com/apis/credentials) 에 입력한다.
