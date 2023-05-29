### RESTful 웹 서비스 구축
이 가이드는 Spring으로 "Hello, World" RESTful 웹 서비스를 만드는 과정을 안내합니다.

#### 무엇을 만들 것인가
에서 HTTP GET 요청을 수락하는 서비스를 빌드합니다 http://localhost:8080/greeting.


다음 목록과 같이 인사말의 JSON 표현으로 응답합니다.
```java
{"id":1,"content":"Hello, World!"}
```

name다음 목록과 같이 쿼리 문자열의 선택적 매개 변수를 사용하여 인사말을 사용자 지정할 수 있습니다 .
```
http://localhost:8080/greeting?name=User
```

매개 name변수 값은 다음 목록과 같이 기본값을 재정의 World하고 응답에 반영됩니다.
```java
{"id":1,"content":"Hello, User!"}
```

### 필요한 것
* 약 15 분의 시간
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.


### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

### 스프링 이니셜라이저로 시작하기
