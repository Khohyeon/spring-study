## Spring Boot Actuator로 RESTful 웹 서비스 구축
Spring Boot Actuator 는 Spring Boot의 하위 프로젝트입니다. 약간의 노력으로 응용 프로그램에 여러 생산 등급 서비스를 추가합니다. 이 가이드에서는 애플리케이션을 빌드한 다음 이러한 서비스를 추가하는 방법을 살펴봅니다.

### 무엇을 만들 것인가
이 가이드는 Spring Boot Actuator를 사용하여 "Hello, world" RESTful 웹 서비스를 만드는 과정을 안내합니다. 다음 HTTP GET 요청을 수락하는 서비스를 빌드합니다.

```
$ curl http://localhost:9000/hello-world
```
다음 JSON으로 응답합니다.
```
{"id":1,"content":"Hello, World!"}
```
프로덕션(또는 기타) 환경에서 서비스를 관리하기 위해 애플리케이션에 많은 기능이 추가되었습니다. 빌드하는 서비스의 비즈니스 기능은 RESTful 웹 서비스 빌드 에서와 동일합니다 . 결과를 비교하는 것이 흥미로울 수 있지만 이 가이드를 활용하기 위해 해당 가이드를 사용할 필요는 없습니다.
### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.
처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .
기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-actuator-service.git
* cd 로gs-actuator-service/initial
* 표현 클래스 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-actuator-service/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Spring Web 및 Spring Boot Actuator 를 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

빈 서비스 실행
Spring Initializr는 시작하는 데 사용할 수 있는 빈 애플리케이션을 생성합니다. 다음 예제( src/main/java/com/example/actuatorservice/ActuatorServiceApplication)는 Spring Initializr에 의해 생성된 클래스를 보여줍니다.
```java
package com.example.actuatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActuatorServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ActuatorServiceApplication.class, args);
  }

}
```
이 애플리케이션에 정의된 엔드포인트는 없지만 사물을 시작하고 Actuator의 일부 기능을 보기에 충분합니다. 이 SpringApplication.run()명령은 웹 응용 프로그램을 시작하는 방법을 알고 있습니다. 다음 명령을 실행하기만 하면 됩니다.

```
$ ./gradlew clean build && java -jar build/libs/gs-actuator-service-0.1.0.jar
```

아직 코드를 작성하지 않았는데 무슨 일이 일어나고 있나요? 응답을 보려면 서버가 시작될 때까지 기다렸다가 다른 터미널을 열고 다음 명령을 시도하십시오(출력과 함께 표시됨).
```
$ curl localhost:8080
{"timestamp":1384788106983,"error":"Not Found","status":404,"message":""}
```

이전 명령의 출력은 서버가 실행 중이지만 아직 비즈니스 엔드포인트를 정의하지 않았음을 나타냅니다.
기본 컨테이너 생성 HTML 오류 응답 대신 Actuator /error 엔드포인트의 일반 JSON 응답이 표시됩니다. 즉시 제공되는 엔드포인트를 서버 시작의 콘솔 로그에서 확인할 수 있습니다. 
엔드 포인트 /health 를 포함하여 이러한 엔드포인트 중 일부를 시도할 수 있습니다. 다음 예에서는 이를 수행하는 방법을 보여줍니다.

```
$ curl localhost:8080/actuator/health
{"status":"UP"}
```
상태가 UP이므로 액추에이터 서비스가 실행 중입니다.

표현 클래스 만들기
먼저 API가 어떤 모습일지 생각해야 합니다.

/hello-world 선택적으로 이름 쿼리 매개변수를 사용하여 에 대한 GET 요청을 처리하려고 합니다. 
이러한 요청에 대한 응답으로 다음과 같은 인사말을 나타내는 JSON을 다시 보내려고 합니다.

```json
{
    "id": 1,
    "content": "Hello, World!"
}
```

이 id 필드는 인사말의 고유 식별자이며 content 인사말의 텍스트 표현을 포함합니다.

인사말 표현을 모델링하려면 표현 클래스를 만듭니다. 
다음 목록에서 (src/main/java/com/example/actuatorservice/Greeting.java) 은 Greeting 클래스를 보여줍니다.

```java
package com.example.actuatorservice;

public class Greeting {

  private final long id;
  private final String content;

  public Greeting(long id, String content) {
    this.id = id;
    this.content = content;
  }

  public long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

}
```
이제 표현 클래스를 제공할 끝점 컨트롤러를 만들어야 합니다.

### 리소스 컨트롤러 만들기
Spring에서 REST 끝점은 Spring MVC 컨트롤러입니다. 
다음 Spring MVC 컨트롤러(src/main/java/com/example/actuatorservice/HelloWorldController.java)는 끝점에 대한 GET 요청을 처리 /hello-world하고 리소스를 반환합니다 

```java
package com.example.actuatorservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/hello-world")
  @ResponseBody
  public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
    return new Greeting(counter.incrementAndGet(), String.format(template, name));
  }

}
```

- 컨트롤러와 REST 끝점 컨트롤러의 주요 차이점은 응답이 생성되는 방식에 있습니다. 
- HTML 에서 모델 데이터를 렌더링하기 위해 보기(예: JSP)에 의존하는 대신 엔드포인트 컨트롤러는 응답 본문에 직접 쓸 데이터를 반환합니다.

- 어노테이션 @ResponseBody은 Spring MVC에게 모델을 뷰로 렌더링하지 않고 반환된 객체를 응답 본문에 작성하도록 지시합니다. 
- Spring의 메시지 변환기 중 하나를 사용하여 이를 수행합니다. 
- Jackson 2가 클래스 경로에 있기 때문에 요청의 헤더가 JSON이 반환되어야 한다고 지정하는 경우 개체를 JSON으로 MappingJackson2HttpMessageConverter변환하는 작업을 처리합니다 .

### 애플리케이션 실행
사용자 지정 기본 클래스 또는 구성 클래스 중 하나에서 직접 애플리케이션을 실행할 수 있습니다. 
이 간단한 예제에서는 도우미 클래스를 사용할 수 있습니다 SpringApplication. 
이것은 Spring Initializr가 생성한 애플리케이션 클래스이며 이 간단한 애플리케이션에서 작동하도록 수정하지 않아도 됩니다. 
다음 목록에서 (src/main/java/com/example/actuatorservice/HelloWorldApplication.java)은 애플리케이션 클래스를 보여줍니다.

```java
package com.example.actuatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication {

  public static void main(String[] args) {
    SpringApplication.run(HelloWorldApplication.class, args);
  }

}
```

Spring Boot는 클래스 경로에서 spring-webmvc 를 감지하면 이 주석을 자동으로 켭니다. 
이렇게 하면 다음 단계에서 컨트롤러를 구축할 수 있습니다.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar 빌드/libs/gs-actuator-service-0.1.0.jar
```

서비스가 실행되면( spring-boot:run터미널에서 실행했기 때문에) 별도의 터미널에서 다음 명령을 실행하여 테스트할 수 있습니다.

```
$ curl localhost:8080/hello-world
{"id":1,"content":"Hello, Stranger!"}
```

### 다른 서버 포트로 전환
Spring Boot Actuator는 기본적으로 포트 8080에서 실행됩니다. 
파일을 추가하면 해당 설정을 재정의할 수 있습니다. 다음 목록에서 (src/main/resources/application.properties)은 필요한 변경 사항이 있는 파일을 보여줍니다.

```properties
server.port: 9000
management.server.port: 9001
management.server.address: 127.0.0.1
```

터미널에서 다음 명령을 실행하여 서버를 다시 실행하십시오.
```
$ ./gradlew 클린 빌드 && java -jar 빌드/libs/gs-actuator-service-0.1.0.jar
```

이제 서비스가 포트 9000에서 시작됩니다.

터미널에서 다음 명령을 실행하여 포트 9000에서 작동하는지 테스트할 수 있습니다.

```
$ curl localhost:8080/hello-world
curl: (52) Empty reply from server
$ curl localhost:9000/hello-world
{"id":1,"content":"Hello, Stranger!"}
$ curl localhost:9001/actuator/health
{"status":"UP"}
```

### 애플리케이션 테스트
애플리케이션이 작동하는지 확인하려면 애플리케이션에 대한 단위 및 통합 테스트를 작성해야 합니다. 테스트 클래스는 src/test/java/com/example/actuatorservice/HelloWorldApplicationTests.java다음을 보장합니다.
컨트롤러가 반응합니다.
관리 엔드포인트가 응답합니다
테스트는 임의의 포트에서 응용 프로그램을 시작합니다. 다음 목록은 테스트 클래스를 보여줍니다.

```java
/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.actuatorservice;

import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Basic integration tests for service demo application.
 *
 * @author Dave Syer
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class HelloWorldApplicationTests {

  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgt;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void shouldReturn200WhenSendingRequestToController() throws Exception {
    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
        "http://localhost:" + this.port + "/hello-world", Map.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
        "http://localhost:" + this.mgt + "/actuator", Map.class);
    then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
```

