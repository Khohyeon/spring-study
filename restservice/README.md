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

처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-rest-service.git

* cd 로gs-rest-service/initial

* 리소스 표시 클래스 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-rest-service/complete.
### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Web 을 선택하십시오 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 리소스 표현 클래스 만들기
이제 프로젝트 및 빌드 시스템을 설정했으므로 웹 서비스를 만들 수 있습니다.

서비스 상호 작용에 대해 생각하여 프로세스를 시작하십시오.

이 서비스는 선택적으로 쿼리 문자열의 매개변수를 사용하여 GET에 대한 요청을 처리합니다 . 요청 은 인사말을 나타내는 본문에 JSON이 포함된 응답을 반환해야 합니다 . 다음 출력과 유사해야 합니다./greetingnameGET200 OK
```json
{
    "id": 1,
    "content": "Hello, World!"
}
```
출력을 받을 JSON 데이터

이 id필드는 인사말의 고유 식별자이며 content인사말의 텍스트 표현입니다.

인사말 표현을 모델링하려면 자원 표현 클래스를 작성하십시오. 이렇게 하려면 다음 목록(에서 ) 과 같이 id및 데이터에 대한 Java 레코드 클래스를 제공하십시오.contentsrc/main/java/com/example/restservice/Greeting.java
```java
package com.example.restservice;

/*
   Greeting record 생성
 */
public record Greeting(
        long id
        , String content
) {
}

```

```
이 애플리케이션은 Jackson JSON 라이브러리를 사용하여 유형의 인스턴스를 JSON으로 자동 마샬링합니다 Greeting. Jackson은 기본적으로 웹 스타터에 포함되어 있습니다.
```


### 리소스 컨트롤러 만들기
RESTful 웹 서비스 구축에 대한 Spring의 접근 방식에서 HTTP 요청은 컨트롤러에 의해 처리됩니다. @RestController이러한 구성 요소는 주석 으로 식별되며 GreetingController다음 목록(에서 src/main/java/com/example/restservice/GreetingController.java) 에 표시된 클래스의 새 인스턴스를 반환하여 GET요청을 처리합니다 ./greetingGreeting

``` java
package com.example.restservice.controller;

import com.example.restservice.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    // @RequestParam 으로 name 값을 받아 오는데 default 값으로 World 를 설정
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    // AtomicLong 은 long 값을 위한 thread-safe 한 클래스 (멀티스레드 환경에서 주로 사용)
    // AtomicLong 객체의 incrementAndGet() 메서드 사용해서 값이 들어 올 때 마다 counter를 1씩 올린다.
    // String.format() 메서드를 사용해서 name 값을 포함한 문자열을 만들고 Greeting 객체를 생성해서 반환한다.
}
```

이 컨트롤러는 간결하고 단순하지만 내부적으로 많은 일이 진행되고 있습니다. 우리는 그것을 단계별로 분해합니다.

주석은 HTTP GET 요청이 메서드 에 매핑되도록 @GetMapping합니다 ./greetinggreeting()

#### Gradle을 사용하는 경우
./gradlew bootRun. ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```gradle
java -jar build/libs/restservice-0.1.0.jar
```

### 서비스 테스트
이제 서비스가 시작되었으므로 을(를) 방문하여 http://localhost:8080/greeting다음을 확인해야 합니다.

```json
{"id":1,"content":"Hello, World!"}
``` 
name를 방문하여 쿼리 문자열 매개변수를 제공하십시오 http://localhost:8080/greeting?name=User. 다음 목록과 같이 속성 값이 에서 로 어떻게 변경 content되는지 확인하십시오.Hello, World!Hello, User!
```json
{"id":2,"content":"Hello, User!"}
```

이 변경은 @RequestParam배열이 GreetingController예상대로 작동하고 있음을 보여줍니다. 매개 변수 name에 기본값이 지정되었지만 World쿼리 문자열을 통해 명시적으로 재정의할 수 있습니다.

