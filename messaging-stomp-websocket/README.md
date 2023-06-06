## WebSocket을 사용하여 대화형 웹 애플리케이션 구축
이 가이드는 브라우저와 서버 간에 메시지를 주고 받는 "Hello, world" 애플리케이션을 만드는 과정을 안내합니다. WebSocket은 TCP 위의 얇고 가벼운 레이어입니다. 따라서 "서브 프로토콜"을 사용하여 메시지를 삽입하는 데 적합합니다. 이 가이드에서는 Spring과 함께 STOMP 메시징을 사용하여 대화형 웹 애플리케이션을 만듭니다. STOMP는 하위 수준 WebSocket 위에서 작동하는 하위 프로토콜입니다.

### 무엇을 만들 것인가
사용자 이름이 포함된 메시지를 수락하는 서버를 구축합니다. 이에 대한 응답으로 서버는 인사말을 클라이언트가 구독하는 대기열로 푸시합니다.

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-messaging-stomp-websocket.git

* cd 로gs-messaging-stomp-websocket/initial

* 리소스 표시 클래스 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-messaging-stomp-websocket/complete.


### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Websocket 을 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.


Gradle을 사용하는 경우 다음 종속 항목을 추가해야 합니다.

```
implementation 'org.webjars:webjars-locator-core'
implementation 'org.webjars:sockjs-client:1.0.2'
implementation 'org.webjars:stomp-websocket:2.3.3'
implementation 'org.webjars:bootstrap:3.3.7'
implementation 'org.webjars:jquery:3.1.1-1'
```

완성된 build.gradle 파일은 다음과 같습니다.

```
plugins {
	id 'org.springframework.boot' version '3.1.0'
	id 'io.spring.dependency-management' version '1.1.0'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-websocket'
	implementation 'org.webjars:webjars-locator-core'
	implementation 'org.webjars:sockjs-client:1.0.2'
	implementation 'org.webjars:stomp-websocket:2.3.3'
	implementation 'org.webjars:bootstrap:3.3.7'
	implementation 'org.webjars:jquery:3.1.1-1'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
	useJUnitPlatform()
}
```

### 리소스 표현 클래스 만들기
이제 프로젝트 및 빌드 시스템을 설정했으므로 STOMP 메시지 서비스를 만들 수 있습니다.

서비스는 본문이 JSON 객체인 STOMP 메시지에 이름이 포함된 메시지를 수락합니다. 이름이 Fred인 경우 메시지는 다음과 유사할 수 있습니다.

```json
{
    "name": "Fred"
}
```


이름을 전달하는 메시지를 모델링하려면 다음 목록(src/main/java/com/example/messagingstompwebsocket/HelloMessage.java의 ) 보여줍니다

```java
package com.example.messagingstompwebsocket;

public class HelloMessage {

  private String name;

  public HelloMessage() {
  }

  public HelloMessage(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```
메시지를 수신하고 이름을 추출하면 서비스는 인사말을 만들고 해당 인사말을 클라이언트가 구독하는 별도의 대기열에 게시하여 메시지를 처리합니다. 인사말은 다음 목록과 같이 JSON 객체이기도 합니다.
```json
{
    "content": "Hello, Fred!"
}
```

인사말 표현을 모델링하려면 다음 목록(src/main/java/com/example/messagingstompwebsocket/Greeting.java에서)에 표시된 것처럼 콘텐츠 속성 및 해당 getContent() 메서드가 있는 또 다른 일반 이전 Java 개체를 추가합니다.

```java
package com.example.messagingstompwebsocket;

public class Greeting {

  private String content;

  public Greeting() {
  }

  public Greeting(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

}
```


Spring은 Greeting 유형의 인스턴스를 JSON으로 자동 마샬링하기 위해 Jackson JSON 라이브러리를 사용할 것입니다.
마샬링(Marshalling)은 데이터를 일련의 바이트로 변환하는 프로세스입니다.

다음으로 Hello 메시지를 수신하고 인사말 메시지를 보낼 컨트롤러를 생성합니다.

### STOMP 특징
- STOMP 프로토콜은 클라이언트/서버 간 전송할 메시지의 유형, 형식, 내용들을 정의한 규칙.
- TCP 또는 WebSocket과 같은 양방향 네트워크 프로토콜 기반으로 동작.
- 헤더 값을 기반으로 통신 시 인증처리를 구현할 수 있음.

### 메시지 처리 컨트롤러 만들기
STOMP 메시징 작업에 대한 Spring의 접근 방식에서 STOMP 메시지는 @Controller 클래스로 라우팅될 수 있습니다. 

예를 들어 GreetingController(src/main/java/com/example/messagingstompwebsocket/GreetingController.java에서)는 다음 목록과 같이 /hello 대상에 대한 메시지를 처리하도록 매핑됩니다.
```java
@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage helloMessage) throws Exception{
        Thread.sleep(1000);
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(helloMessage.getName()) + "!");
    }
}

```

- @MessageMapping 어노테이션을 사용하면 /hello로 라우팅되는 메시지를 greeting() 메서드로 라우팅할 수 있습니다. <br>
-> .hello 대상으로 전송되는 경우 greeting() 메서드가 호출됩니다.
- 내부적으로 메서드 구현은 스레드를 1초 동안 휴면 상태로 만들어 처리 지연을 시뮬레이트합니다. 이는 클라이언트가 메시지를 보낸 후 서버가 메시지를 비동기식으로 처리하는 데 필요한 시간만큼 오래 걸릴 수 있음을 보여주기 위한 것입니다. 클라이언트는 응답을 기다리지 않고 필요한 모든 작업을 계속할 수 있습니다.
- 1초 지연 후 greeting() 메서드는 Greeting 객체를 생성하고 반환합니다. 반환 값은 @SendTo 주석에 지정된 대로 /topic/greetings의 모든 구독자에게 브로드캐스트됩니다. 입력 메시지의 이름은 삭제됩니다. 이 경우 클라이언트 측의 브라우저 DOM에서 다시 표시되고 다시 렌더링되기 때문입니다..
- HtmlUtils.htmlEscape()는 메시지를 보낼 때 특수 문자를 이스케이프합니다.<br>
-> 이스케이프를 하는 이유는 HTML 문서에 데이터를 안전하게 출력할 수 있게 만들기 위해서 입니다.<br>
-> 예를 들어서 ` < ` 를 `&lt;` 로, ` > ` 를 `&gt;`로, ` " ` 를 `&quot;`로,` ' ` 를 `&#39;`로, &를 `&amp;`로 변환

### STOMP 메시징을 위한 Spring 구성 
서비스의 필수 구성 요소가 생성되었으므로 WebSocket 및 STOMP 메시징을 활성화하도록 Spring을 구성할 수 있습니다.

다음 목록과 유사한 WebSocketConfig라는 Java 클래스를 만듭니다(src/main/java/com/example/messagingstompwebsocket/WebSocketConfig.java)
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS(); // SockJS is used to enable fallback options for browsers that don’t support websocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
```
- WebSocketConfig는 Spring 구성 클래스임을 나타내기 위해 @Configuration으로 주석 처리됩니다. 또한 
- @EnableWebSocketMessageBroker는 WebSocket 서버를 활성화하고 메시지 브로커를 구성하는 데 사용됩니다.
- configureMessageBroker() 메서드는 WebSocketMessageBrokerConfigurer의 기본 메서드를 구현하여 메시지 브로커를 구성합니다. 간단한 메모리 기반 메시지 브로커가 인사말 메시지를 /topic 접두사가 있는 대상의 클라이언트로 다시 전달할 수 있도록 enableSimpleBroker()를 호출하는 것으로 시작합니다
- @MessageMapping으로 주석이 달린 메서드에 바인딩된 메시지의 /app 접두사를 지정합니다. 이 접두사는 모든 메시지 매핑을 정의하는 데 사용됩니다. 예를 들어 /app/hello는 GreetingController.greeting() 메서드가 처리에 매핑되는 엔드포인트입니다.
- registerStompEndpoints() 메서드는 /gs-guide-websocket 엔드 포인트를 등록합니다. SockJS 옵션을 사용하면 WebSocket을 지원하지 않는 브라우저에 대한 대체 옵션을 활성화할 수 있습니다.

### 브라우저 클라이언트 생성
서버 측 부분이 준비되면 서버 측과 메시지를 주고받는 JavaScript 클라이언트에 주의를 기울일 수 있습니다.

다음 목록과 유사한 index.html 파일을 만듭니다(src/main/resources/static/index.html에서).

```html
<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/main.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script src="/app.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">WebSocket connection:</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="name">What is your name?</label>
                    <input type="text" id="name" class="form-control" placeholder="Your name here...">
                </div>
                <button id="send" class="btn btn-default" type="submit">Send</button>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>Greetings</th>
                </tr>
                </thead>
                <tbody id="greetings">
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
```

이 HTML 파일은 websocket을 통해 STOMP를 통해 서버와 통신하는 데 사용되는 SockJS 및 STOMP 자바스크립트 라이브러리를 가져옵니다. 또한 클라이언트 애플리케이션의 로직이 포함된 app.js를 가져옵니다. 다음 목록(src/main/resources/static/app.js에서)은 해당 파일을 보여줍니다.

```javascript
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});
```
이해해야 할 이 JavaScript 파일의 주요 부분은 connect() 및 sendName() 함수입니다.

- connect() 함수는 SockJS를 통해 WebSocket 연결을 설정하고 STOMP 클라이언트를 사용하여 메시지를 보내고 받습니다.
- 성공적으로 연결되면 클라이언트는 /topic/greetings 대상을 구독하고 서버는 인사말 메시지를 게시합니다.
- sendName() 함수는 /app/hello 대상으로 메시지를 보냅니다. 이것은 GreetingController.greeting() 메서드에 매핑됩니다.
- showGreeting() 함수는 인사말을 표시합니다.
- 마지막으로, index.html 파일은 WebSocket을 지원하지 않는 브라우저에 대한 대체 옵션을 제공합니다.

### 응용 프로그램을 실행 가능하게 만들기
Spring Boot는 애플리케이션 클래스를 생성합니다. 이 경우 더 이상 수정할 필요가 없습니다. 이 응용 프로그램을 실행하는 데 사용할 수 있습니다. 
다음 목록(src/main/java/com/example/messagingstompwebsocket/MessagingStompWebsocketApplication.java)은 애플리케이션 클래스를 보여줍니다
```java
@SpringBootApplication
public class MessagingStompWebsocketApplication {

  public static void main(String[] args) {
    SpringApplication.run(MessagingStompWebsocketApplication.class, args);
  }
}
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/messaging-stomp-websocket-0.1.0.jar
```

### 서비스 테스트
이제 서버가 실행되고 있으므로 브라우저에서 http://localhost:8080을 열고 Connect 버튼을 클릭합니다. 그런 다음 브라우저 콘솔을 확인하십시오. 연결이 성공적으로 설정되면 다음과 같은 메시지가 표시됩니다.

연결을 열면 이름을 묻는 메시지가 표시됩니다. 이름을 입력하고 보내기를 클릭합니다. 귀하의 이름은 STOMP를 통해 JSON 메시지로 서버에 전송됩니다. 1초의 시뮬레이션 지연 후 서버는 페이지에 표시되는 "Hello" 인사말과 함께 메시지를 다시 보냅니다. 이 시점에서 다른 이름을 보내거나 연결 끊기 버튼을 클릭하여 연결을 닫을 수 있습니다.






